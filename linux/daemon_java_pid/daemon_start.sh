#!/bin/sh
cat "daemon_jar.txt" | while read -r line; do
    if [[ $line =~ ^\s*# ]]; then
        #    echo "当前行为注释内存，因为以 # 开头" $line
        continue
    fi
#    echo "程序：$line"
    if pgrep -f "daemon_java_pid.sh $line" | grep -v $$ >/dev/null; then
        echo "已存在[$line]守护进程" $(pgrep -f "daemon_java_pid.sh $line" | grep -v $$)
        continue
    fi
    echo "启动守护脚本" $line
    nohup ./daemon_java_pid.sh $line >/dev/null 2>&1 &
done