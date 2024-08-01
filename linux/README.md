# Linux小工具
1. 快捷进入进程目录：cdp

封装`pwd`命令，读取程序所在目录后将工作路径切换到程序所在目录。
- 使用：将此命令放到`/usr/bin/`下即可使用
- 缺点：exec实现切换工作目录，属于新开启一个会话，切换前的命令及历史记录不会保留。
- todo：可以直接输入进程名称，只命中一个pid则直接进入，命中多个则显示列表，输入列表序号后回车进入程序目录（类似arthas命中多个进程的操作）

2. 快捷启动：start_jar

手动发布java进程时，经常会重启，手动操作非常麻烦。
- [start_jar.sh](start_jar%2Fstart_jar.sh) : 可接收4个参数：环境，jar文件名，
  - 功能：检查进程，并执行 `kill -15` 命令，等待20秒后进程仍存在则 `kill -9`，最后重启进程
  - 参数：
    - 环境：`-Dspring.profiles.active`的值，也是springboot项目中application.yml的环境值。
    - jar文件名：jar包文件名，可不携带版本号。
    - jvm参数Xmx：可选，默认512m
    - jvm参数Xms：可选，默认128m
- [sky_demo_fat.sh](start_jar%2Fsky_demo_fat.sh) : 示例脚本，该脚本名可自定义。
  - 示例： `./start_jar.sh prod_test sky-demo Xmx=1g` 表示在当前目录下找`sky-demo.*.jar`文件，并设置`-Dspring.profiles.active=prod_test -Xmx1g`参数启动jar文件

3. java进程守护脚本
- [daemon_jar.txt](daemon_java_pid%2Fdaemon_jar.txt) : 需要守护进程的jar文件名前缀，与[sky_demo_fat.sh](start_jar%2Fsky_demo_fat.sh)文件名配套使用。例如： `sky-demo`则对应 `sky_demo*.sh` 作为该jar文件的启动脚本，由`sky_demo*.sh`执行[start_jar.sh](start_jar%2Fstart_jar.sh)
- [daemon_java_pid.sh](daemon_java_pid%2Fdaemon_java_pid.sh)：守护进程逻辑。利用`ps`检查是否存在进程，存在则记录对应进程所在目录路径`appdir`；不存在则进入`appdir`后执行jar文件的启动脚本（例如`sky_demo*.sh`），若`appdir`为空，则可能第一次执行亦或其他情况导致没有记录。需要手动启动jar文件
- [daemon_start.sh](daemon_java_pid%2Fdaemon_start.sh) : 读取[daemon_jar.txt](daemon_java_pid%2Fdaemon_jar.txt)文件，判断是否存在相应守护脚本，存在则跳过，否则启动相应守护脚本。

# 版本
## 2024-08-01
- cdp支持检索jar包名称。参考arthas脚本`as.sh`中参数解析。
  - 示例：`cdp demo-test` # 若只有一个进程则直接进入相应目录；若多个进程则列出进程选项，输入相应序号并回车后进入相应目录
  - 示例：`cdp -v demo-test` # 若只有一个进程则直接进入相应目录；若多个进程则列出进程选项（显示启动参数，参考`jps -lv`），输入相应序号并回车后进入相应目录
  - 示例：`cdp <pid>` # 利用`jps`查进程，并不是直接进入相应目录。若只有一个进程则直接进入相应目录；若多个进程则列出进程选项，输入相应序号并回车后进入相应目录

## 2024-07-30
- 新增Linux小工具