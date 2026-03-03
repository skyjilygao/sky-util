@echo off
setlocal enabledelayedexpansion

:: Check if port parameter provided
if "%~1"=="" (
    echo Usage: pkill [port_number] [taskkill_parameters] 
    echo Example: pkill 8080 /f
    exit /b 1
)

:: Set port variable
set port=%1
:: Collect additional taskkill parameters by building them
set taskkill_args=
if "%~2" neq "" set taskkill_args=%taskkill_args% %~2
if "%~3" neq "" set taskkill_args=%taskkill_args% %~3
if "%~4" neq "" set taskkill_args=%taskkill_args% %~4
if "%~5" neq "" set taskkill_args=%taskkill_args% %~5

:: Add /f as default only if user provided no additional arguments
if "%taskkill_args%"=="" set taskkill_args=/f

:: Find all processes for port and display them
set count=0
set choice=1
for /f "tokens=1,2,3,4,5" %%a in ('C:\Windows\System32\netstat.exe -ano ^| C:\Windows\System32\findstr.exe ":%port%"') do (
    set /a count+=1
    set "protocol[!count!]=%%a"
    set "local_addr[!count!]=%%b"
    set "foreign_addr[!count!]=%%c"
    set "state[!count!]=%%d"
    set "pid[!count!]=%%e"
)

if %count% equ 0 (
    echo No process found for port %port%
    exit /b 1
)

if %count% equ 1 (
    echo.
    echo Found process for port %port%:
    echo !protocol[1]!    !local_addr[1]!    !foreign_addr[1]!    !state[1]!    !pid[1]!
    echo.
    set pid=!pid[1]!
    goto confirm_single
)

:: Multiple results - ask user to choose
echo.
echo Found %count% processes for port %port%:
echo.
for /l %%i in (1,1,%count%) do (
    echo [%%i] !protocol[%%i]!    !local_addr[%%i]!    !foreign_addr[%%i]!    !state[%%i]!    !pid[%%i]!
)
echo.
set /p choice=Select process to terminate (1-%count%): 
if %choice% lss 1 (
    echo Invalid selection
    exit /b 1
)
if %choice% gtr %count% (
    echo Invalid selection
    exit /b 1
)

set pid=!pid[%choice%]!
echo.
echo Selected process:
echo !protocol[%choice%]!    !local_addr[%choice%]!    !foreign_addr[%choice%]!    !state[%choice%]!    !pid[%choice%]!
echo.

:confirm_single
:: Interactive confirmation
set /p confirm=Terminate this process? (yes/y to confirm, anything else to cancel): 
if /i "%confirm%"=="yes" goto kill
if /i "%confirm%"=="y" goto kill

echo Operation cancelled
exit /b 0

:kill
echo.
echo Terminating process...
if "%taskkill_args%"=="" (
    taskkill /pid %pid%
) else (
    taskkill /pid %pid%%taskkill_args%
)
if %errorlevel% equ 0 (
    echo Process terminated successfully
) else (
    echo Failed to terminate process
    exit /b %errorlevel%
)

endlocal