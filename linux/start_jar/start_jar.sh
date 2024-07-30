#!/bin/sh
echo "This script: $0 -> PID: $$"
# 启动环境
spring_profiles_active=$1
# jar文件名称
appname=$2
echo "spring_profiles_active=$spring_profiles_active"
echo "appname=appname"

# 移除前两个参数，从第三个参数开始，手动解析其他参数
shift 2
Xms=""
Xmx=""
# 解析剩余参数
while [[ $# -gt 0 ]]; do
    case "$1" in
        Xms=*) Xms="${1#*=}"; shift ;; # 提取Xms的值
        Xmx=*) Xmx="${1#*=}"; shift ;; # 提取Xmx的值
        *) echo "未知参数: $1"; shift ;;
    esac
done
# 检查变量是否设置，否则设置默认值
if [[ -z "$Xms" ]]; then
    echo "default value -> Xms=128m"
    Xms="128m"
fi
if [[ -z "$Xmx" ]]; then
    echo "default value -> Xmx=512m"
    Xmx=512m
fi

#### 检查进程 ################################################################################################
jinchengid=$(ps -ef | grep -v grep| grep -v $$ |grep -v daemon | grep $spring_profiles_active | grep $appname | awk -F" " '{print $2}')
echo 'pid:' $jinchengid $appname
kill -15 $jinchengid
maxc=20
sc=1
####################################################################################################
i=0
str=""
label=('|' '/' '-' '\\')
#index=0
####################################################################################################
for((;$sc<=$maxc;((sc++))));
do
    let jindu
    if ps -ef | grep -v grep| grep -v $$ |grep -v daemon | grep $spring_profiles_active | grep $appname>/dev/null; then
        jindu=$(($sc*5))
    else
        str="####################"
        jindu=100
    fi
    ##### print jin du tiao ######################################################################################
    let index=sc%4
    printf "\e[47m\e[31m[%-20s]\e[0m\e[47;32m[%c]\e[1;0m\e[47;35m[%-3d%%]\e[1;0m\r" $str ${label[$index]} $jindu
    str+="#"
    if [ $jindu -ge 100 ]
    then
        break
    fi
    sleep 1
done
echo

if ps -ef | grep -v grep| grep -v $$ |grep -v daemon | grep $spring_profiles_active | grep $appname>/dev/null; then
    echo '仍然存在此进程，强制停止进程： kill -9 ' $jinchengid
    kill -9 $jinchengid
    sleep 1
else
    echo $jinchengid "has been killed."
fi
echo 'starting... ' $appname-*.jar
## nohup java -XX:+UseParNewGC -Xms128m -Xmx512m -Dspring.profiles.active=$spring_profiles_active -jar $appname-*.jar >/dev/null 2>&1 &
## -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath='./java_%p.hprof'
nohup java -Xss1m -Xms"$Xms" -Xmx"$Xmx" -XX:+UseG1GC -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m -XX:MaxDirectMemorySize=256m -XX:MaxGCPauseMillis=500 -XX:InitiatingHeapOccupancyPercent=40 -XX:+HeapDumpOnOutOfMemoryError -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGCTimeStamps -Dbasedir=./ -Xloggc:gc_%p-%t.log -Dfile.encoding=UTF-8 -Dspring.profiles.active=$spring_profiles_active -jar $appname-*.jar >/dev/null 2>&1 &
sleep 1
ps -ef | grep -v grep | grep -v $0 |grep -v daemon | grep $spring_profiles_active | grep $appname
