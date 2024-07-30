#!/bin/bash

appname=$1
appname_sh=$(echo "${appname}" | tr '-' '_')
appbash=${appname_sh}*.sh
echo '--> appname=' ${appname}
appdir=''
while true; do
    #cd -P "$appdir"
    #echo "当前目录: $(pwd)"
    #source $appdir/$appbash
    appPid=$(ps -ef | grep -v grep | grep -v "$$" | grep "$appname.*.jar" | awk '{print $2}')
    echo 'appPid:' $appPid '程序目录:' $(readlink /proc/$appPid/cwd)
    #echo "pgrep -f $appname_sh | grep -v $$"
    appname_sh_id=$(pgrep -f "$appname_sh" | grep -v $$)

    #if pgrep -f "$appname_sh" | grep -v $$ >/dev/null;then
    # echo '其他启动脚本进程：' $appname_sh_id
    #else
    # echo '没有其他启动脚本'
    #fi

    if [ -z "$appPid" ]; then
        echo "不存在 $appname 进程，可能需要重启"
        # 进程不存在，检查程序脚本是否在执行
        if pgrep -f "$appname_sh" | grep -v $$ >/dev/null; then
            echo "程序脚本 $appbash [pid: $appname_sh_id]正在执行中，跳过当前启动流程"
        else
            # 进入程序目录并启动程序脚本
            # appdir=$(readlink /proc/$appPid/cwd)
            if [ -n "$appdir" ]; then
                echo "进入程序目录 $appdir"
                cd -P "$appdir" || {
                    echo "无法进入程序目录 $appdir"
                    exit 1
                }
                pwd
                echo "启动程序脚本 $appbash"
                source $appdir/$appbash &
                cd -
            else
                echo "无法获取程序目录，需手动启动"
            fi
        fi
    else
        appdir=$(readlink /proc/$appPid/cwd)
    #    echo "进程 $appname (PID: $appPid) 存在，目录：$appdir"
    fi
    sleep 1
done