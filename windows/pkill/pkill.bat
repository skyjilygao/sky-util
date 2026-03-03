@echo off
setlocal enabledelayedexpansion

:: 设置党的代码页为936 (GBK)来支持中文显示
chcp 65001 >nul

:: 检查是否提供了端口参数
if "%~1"=="" (
    echo 用法: pkill [端口号] [taskkill参数]
    echo 示例: pkill 8080 /f
    exit /b 1
)

:: 设置端口号变量
set port=%1
:: 收集额外的taskkill参数
set taskkill_args=
if "%~2" neq "" set taskkill_args=%taskkill_args% %~2
if "%~3" neq "" set taskkill_args=%taskkill_args% %~3
if "%~4" neq "" set taskkill_args=%taskkill_args% %~4
if "%~5" neq "" set taskkill_args=%taskkill_args% %~5

:: 如果用户没有提供额外参数，添加/f作为默认值
if "%taskkill_args%"=="" set taskkill_args=/f

:: 查找端口对应的所有进程并显示
set count=0
set choice=1
for /f "tokens=1,2,3,4,5" %%a in ('netstat -ano ^| findstr ":%port%"') do (
    set /a count+=1
    set "protocol[!count!]=%%a"
    set "local_addr[!count!]=%%b"
    set "foreign_addr[!count!]=%%c"
    set "state[!count!]=%%d"
    set "pid[!count!]=%%e"
)

if %count% equ 0 (
    echo 未找到端口 %port% 对应的进程
    exit /b 1
)

if %count% equ 1 (
    echo.
    echo 找到端口 %port% 对应的进程:
    echo !protocol[1]!    !local_addr[1]!    !foreign_addr[1]!    !state[1]!    !pid[1]!
    echo.
    set pid=!pid[1]!
    goto confirm_single
)

:: 多个结果 - 让用户选择
echo.
echo 找到 %count% 个端口 %port% 对应的进程:
echo.
for /l %%i in (1,1,%count%) do (
    echo [%%i] !protocol[%%i]!    !local_addr[%%i]!    !foreign_addr[%%i]!    !state[%%i]!    !pid[%%i]!
)
echo.
@REM echo 请选择要终止的进程 (1-%count%): 
set /p choice="请选择要终止的进程 (1-%count%): "
if %choice% lss 1 (
    echo 无效的选择
    exit /b 1
)
if %choice% gtr %count% (
    echo 无效的选择
    exit /b 1
)

set pid=!pid[%choice%]!


echo.
echo 选中的进程:
echo !protocol[%choice%]!    !local_addr[%choice%]!    !foreign_addr[%choice%]!    !state[%choice%]!    !pid[%choice%]!


echo.

:confirm_single


@REM echo 是否终止此进程? (yes/y 确认, 其他取消): 
set /p confirm="是否终止此进程? (yes/y 确认, 其他取消): "
if /i "%confirm%"=="yes" goto kill
if /i "%confirm%"=="y" goto kill



echo 操作已取消
exit /b 0

:kill


echo.
echo 正在终止进程...
if "%taskkill_args%"=="" (
    taskkill /pid %pid%
) else (
    taskkill /pid %pid% %taskkill_args%
)
if %errorlevel% equ 0 (
    echo 进程已成功终止
) else (
    echo 终止进程失败
    exit /b %errorlevel%
)

endlocal