#!/bin/bash

set -euo pipefail

BASE_DIR="$(pwd)"
SCRIPT_NAME="$(basename "$0")"
LOG_FILE="$BASE_DIR/pack_$(date +%Y%m%d_%H%M%S).log"

cd "$BASE_DIR" || { echo "❌ 无法进入目录 $BASE_DIR"; exit 1; }

PARENT_PATH=$(dirname "$BASE_DIR")
PARENT_NAME=$(basename "$PARENT_PATH")
CURRENT_DIR_NAME=$(basename "$BASE_DIR")
ARCHIVE_PREFIX="${PARENT_NAME}.${CURRENT_DIR_NAME}"
TIMESTAMP=$(date +"%Y%m%d%H%M")

# === 主流程配置 ===
MAIN_EXCLUDE_PATTERNS=(
    "*.txt"
    "*.sh"
    "*.zip"
    "$SCRIPT_NAME"
    "pack_*.log"
)

MAIN_SKIP_DIRS=(
    "logs"
    ".pki"
    ".oracle_jre_usage"
)

# === Other 打包配置（完全独立）===
OTHER_EXCLUDE_PATTERNS=(
    "$SCRIPT_NAME"
    "pack_*.log"
    "*.tmp"
    "*.swp"
    "*.log"
)

OTHER_SKIP_DIRS=(
    "logs"      # 即使主流程跳过了 logs，other 也不打包它
    # 可按需添加，如 ".cache", "temp" 等
)

# 构建 tar 参数
MAIN_EXCLUDE_ARGS=()
for p in "${MAIN_EXCLUDE_PATTERNS[@]}"; do
    MAIN_EXCLUDE_ARGS+=(--exclude="$p")
done

OTHER_EXCLUDE_ARGS=()
for p in "${OTHER_EXCLUDE_PATTERNS[@]}"; do
    OTHER_EXCLUDE_ARGS+=(--exclude="$p")
done

# 日志函数
log() {
    local msg="$1"
    echo "$(date '+%Y-%m-%d %H:%M:%S') - $msg" | tee -a "$LOG_FILE"
}

# 辅助函数：判断元素是否在数组中
contains() {
    local needle="$1"; shift
    for item; do
        if [[ "$item" == "$needle" ]]; then
            return 0
        fi
    done
    return 1
}

# === 主流程：扫描并打包目录 ===
log "🔍 扫描子目录并按磁盘占用排序..."
mapfile -t DIRS < <(
    find . -maxdepth 1 -type d ! -name '.' -exec du -sb {} + 2>/dev/null \
    | sort -n \
    | cut -f2 \
    | xargs -r -I{} basename {}
)

if [ ${#DIRS[@]} -eq 0 ]; then
    log "⚠️ 未发现任何子目录，退出。"
    exit 0
fi

log "📦 共发现 ${#DIRS[@]} 个目录待处理: ${DIRS[*]}"

declare -A STATUS
for dir in "${DIRS[@]}"; do
    STATUS["$dir"]="pending"
done

# 新增：记录主流程成功生成的 tar.gz 文件
SUCCESS_ARCHIVES=()

log "🚀 开始逐个打包目录（按大小升序）..."

for dir in "${DIRS[@]}"; do
    if [[ ! -d "$dir" ]]; then
        log "⚠️ 跳过非目录项: $dir"
        STATUS["$dir"]="skipped"
        continue
    fi

    if contains "$dir" "${MAIN_SKIP_DIRS[@]}"; then
        log "⏭️  主流程跳过目录: $dir"
        STATUS["$dir"]="skipped"
        continue
    fi

    log "🔄 正在打包: $dir"
    archive_name="${ARCHIVE_PREFIX}.${dir}.${TIMESTAMP}.tar.gz"

    if tar -czf "$archive_name" "${MAIN_EXCLUDE_ARGS[@]}" "$dir" 2>>"$LOG_FILE"; then
        log "✅ 打包成功: $archive_name"
        STATUS["$dir"]="done"
        SUCCESS_ARCHIVES+=("$archive_name")   # ← 关键：记录成功文件
    else
        log "❌ 打包失败: $dir"
        STATUS["$dir"]="failed"
    fi
done

# === Other 打包 ===
log "📎 准备打包 'other'（未处理的文件 + 非 OTHER_SKIP_DIRS 的跳过目录）..."

# 获取所有一级项目（不含 . 和 ..）
mapfile -t ALL_ITEMS < <(find . -maxdepth 1 ! -name '.' ! -name '..' -printf '%P\n' 2>/dev/null)

OTHER_ITEMS=()

# 1. 从 MAIN_SKIP_DIRS 中筛选出不在 OTHER_SKIP_DIRS 的目录
for dir in "${MAIN_SKIP_DIRS[@]}"; do
    if [[ -d "$dir" ]] && ! contains "$dir" "${OTHER_SKIP_DIRS[@]}"; then
        OTHER_ITEMS+=("$dir")
        log "➕ 加入 other 目录: $dir"
    elif [[ -d "$dir" ]]; then
        log "⏭️  other 也跳过目录: $dir"
    fi
done

# 2. 添加普通文件，但排除：
#    - 脚本自身、日志
#    - 主流程成功生成的 .tar.gz
for item in "${ALL_ITEMS[@]}"; do
    if [[ ! -d "$item" ]]; then
        # 排除脚本和日志
        if [[ "$item" == "$SCRIPT_NAME" ]] || [[ "$item" == pack_*.log ]]; then
            continue
        fi

        # 排除主流程生成的 tar.gz
        skip_item=false
        for arch in "${SUCCESS_ARCHIVES[@]}"; do
            if [[ "$item" == "$arch" ]]; then
                skip_item=true
                break
            fi
        done
        if [[ "$skip_item" == true ]]; then
            log "⏭️  other 跳过主流程生成的压缩包: $item"
            continue
        fi

        OTHER_ITEMS+=("$item")
        log "➕ 加入 other 文件: $item"
    fi
done

if [ ${#OTHER_ITEMS[@]} -eq 0 ]; then
    log "📭 other 无内容可打包。"
else
    log "📦 other 包含: ${OTHER_ITEMS[*]}"
    other_archive="${ARCHIVE_PREFIX}.other.${TIMESTAMP}.tar.gz"
    if tar -czf "$other_archive" "${OTHER_EXCLUDE_ARGS[@]}" "${OTHER_ITEMS[@]}" 2>>"$LOG_FILE"; then
        log "✅ other 打包成功: $other_archive"
    else
        log "❌ other 打包失败！"
    fi
fi

# === 最终汇总 ===
log "🎉 所有任务完成！"
{
    echo -e "\n=== 最终状态汇总 ==="
    for dir in "${DIRS[@]}"; do
        case "${STATUS[$dir]}" in
            "done")     echo "✅ $dir" ;;
            "failed")   echo "❌ $dir" ;;
            "skipped")  echo "⏭️  $dir" ;;
            *)          echo "❓ $dir" ;;
        esac
    done
    echo "📁 other 内容: ${OTHER_ITEMS[*]:-（无）}"
    echo "🗑️  other 跳过的主流程压缩包: ${SUCCESS_ARCHIVES[*]:-（无）}"
} | tee -a "$LOG_FILE"
