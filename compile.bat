@echo off
REM Compiles the whole project. Run this once (or after any code change)
REM before using run.bat, test.bat, benchmarks.bat or trace.bat.
REM Just double-click this file, or run it from a Command Prompt / PowerShell
REM window opened INSIDE the TEAM-ATLAS-GROUP-25 folder.
REM
REM NOTE: paths below are quoted AND converted to forward slashes on purpose.
REM javac's @sources.txt file list treats backslash as an escape character
REM inside quotes (so "C:\Users\..." silently loses its backslashes) -
REM forward slashes avoid that entirely and work fine on Windows too.

setlocal enabledelayedexpansion
echo Compiling Ghana Smart Service Operations Optimizer...
if not exist bin mkdir bin

if exist sources.txt del sources.txt
for /r src %%f in (*.java) do (
    set "p=%%f"
    set "p=!p:\=/!"
    echo "!p!">>sources.txt
)
javac -cp "lib/sqlite-jdbc.jar" -d bin @sources.txt

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo COMPILE FAILED. Scroll up to see the error above.
    pause
    exit /b 1
)

echo.
echo Compiled successfully. You can now run run.bat, test.bat, trace.bat or benchmarks.bat
pause
