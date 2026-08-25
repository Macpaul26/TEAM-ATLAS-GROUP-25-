@echo off
REM Runs all 154 unit tests and prints a PASS/FAIL summary.

if not exist bin\campushub\RunTests.class (
    echo Project not compiled yet. Running compile.bat first...
    call compile.bat
)

java -cp "bin" campushub.RunTests
pause
