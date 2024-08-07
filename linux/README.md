# Linux小工具
1. 快捷进入进程目录：cdp

封装`pwd`命令，读取程序所在目录后将工作路径切换到程序所在目录。
- 使用：将此命令放到`/usr/bin/`下即可使用
- 缺点：exec实现切换工作目录，属于新开启一个会话，切换前的命令及历史记录不会保留。
- 若执行时提示：`: No such file or directory`，这是由于脚本文件本身包含了 Windows 样式的换行符（CR LF），而不是 Unix/Linux 系统所期望的换行符（LF）。可以使用`sed -i -e 's/\r$//' ./cdp`替换后执行
- 

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

4. 追踪进程：`tracep`

追踪进程由哪个用户启动的，如果root角色，不清楚是普通用户sudo到root角色后还是登录到root后启动进程。
- 使用：
  - `chmod +x tracep`
  - `tracep <pid>`
- 示例：tracep 1234

5. 删除旧jar文件：`del_old_jar_file.sh`

背景：java项目往往一个大工程里分多个模块。gitlab发版java项目时，每次可能都发版不同模块。时间一长暂用磁盘空间太大，删除旧jar文件时需要每个发版目录都确认一下，每个jar文件保留最后几次发版文件，方便回滚。基于此，该脚本诞生。
- `jar_parent_dir`：脚本内变量，指的是项目发布目录的上级目录，我这里示例中是固定的：`/home1/java`。若不一致可修改
- 参数`$1`：脚本参数，指项目发布目录。比如gitlab发版到`/home1/java/demo`，该值就是`demo`
- 特性：
  - 每个jar文件保留最后3次发版文件。若需，可修改脚本内`lastn`变量
  - 删除jar文件后，检查目录是否存在jar文件，存在则跳过，否则删除该目录
- 使用：
  - 将`del_old_jar_file.sh`拷贝到`/home1/java`
  - `chmod +x del_old_jar_file.sh`
  - `./del_old_jar_file.sh demo`
  - 以下为输出结果：
  ```
  [root@gp8bscefb0iZ java]# ./del_old_jar_file.sh demo
  clear dir: demo
  delete jar file: /home1/java/demo/java_#8703/daily-report-data-sync-1.0.0.jar
  delete jar file: /home1/java/demo/java_#8719/daily-report-data-sync-1.0.0.jar
  delete jar file: /home1/java/demo/java_#8720/daily-report-data-sync-1.0.0.jar
  delete jar file: /home1/java/demo/java_#7950/data-compatible-1.0.0.jar
  delete jar file: /home1/java/demo/java_#7955/data-compatible-1.0.0.jar
  delete jar file: /home1/java/demo/java_#7434/old-data-migration-1.0.0.jar
  delete jar file: /home1/java/demo/java_#7955/old-data-migration-1.0.0.jar
  删除目录: /home1/java/demo/java_#7434
  删除目录: /home1/java/demo/java_#7450
  删除目录: /home1/java/demo/java_#7950
  删除目录: /home1/java/demo/java_#7955
  删除目录: /home1/java/demo/java_#8027
  删除目录: /home1/java/demo/java_#8036
  删除目录: /home1/java/demo/java_#8703
  删除目录: /home1/java/demo/java_#8719
  删除目录: /home1/java/demo/java_#8720
  目录 /home1/java/demo/java_#8779 包含 .jar 文件，跳过删除
  删除目录: /home1/java/demo/java_#8957
  目录 /home1/java/demo/java_#9710 包含 .jar 文件，跳过删除
  目录 /home1/java/demo/java_#9711 包含 .jar 文件，跳过删除
  目录 /home1/java/demo/java_#9786 包含 .jar 文件，跳过删除
  目录 /home1/java/demo/java_#9794 包含 .jar 文件，跳过删除
  目录 /home1/java/demo/java_#9795 包含 .jar 文件，跳过删除
  删除目录: /home1/java/demo/java_#9936
  [root@gp8bscefb0iZ java]#
  ```

# 版本
## 2024-08-07
- 新增：删除旧jar文件：`del_old_jar_file.sh`
## 2024-08-05
- 新增：追踪进程：`tracep`
## 2024-08-01
- cdp支持检索jar包名称。参考arthas脚本`as.sh`中参数解析。
  - 示例：`cdp demo-test` # 若只有一个进程则直接进入相应目录；若多个进程则列出进程选项，输入相应序号并回车后进入相应目录
  - 示例：`cdp -v demo-test` # 若只有一个进程则直接进入相应目录；若多个进程则列出进程选项（显示启动参数，参考`jps -lv`），输入相应序号并回车后进入相应目录
  - 示例：`cdp <pid>` # 利用`jps`查进程，并不是直接进入相应目录。若只有一个进程则直接进入相应目录；若多个进程则列出进程选项，输入相应序号并回车后进入相应目录

## 2024-07-30
- 新增Linux小工具