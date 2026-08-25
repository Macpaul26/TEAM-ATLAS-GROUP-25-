@echo off
REM Compiles the whole project. Run this once (or after any code change)
REM before using run.bat, test.bat, benchmarks.bat or trace.bat.
REM Just double-click this file, or run it from a Command Prompt / PowerShell
REM window opened INSIDE the SEG26-41-SYNERGY folder.

echo Compiling Ghana Smart Service Operations Optimizer...
if not exist bin mkdir bin

dir /s /b src\*.java > sources.txt
javac -cp "lib\sqlite-jdbc.jar" -d bin @sources.txt

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo COMPILE FAILED. Scroll up to see the error above.
    pause
    exit /b 1
)

echo.
echo Compiled successfully. You can now run run.bat, test.bat, trace.bat or benchmarks.bat
pause
