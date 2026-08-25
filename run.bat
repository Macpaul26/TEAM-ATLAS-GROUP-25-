@echo off
REM Runs the interactive console menu (the examiner entry point).
REM This is where "is lib/sqlite-jdbc.jar on the classpath?" errors happen
REM if run manually and the classpath separator is wrong (Windows needs ; not :).
REM This script sets it correctly for you every time.

if not exist bin\campushub\Main.class (
    echo Project not compiled yet. Running compile.bat first...
    call compile.bat
)

java -cp "bin;lib\sqlite-jdbc.jar" campushub.Main
pause
