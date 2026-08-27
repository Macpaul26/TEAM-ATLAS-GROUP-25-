@echo off
REM Launches the graphical (Swing) version of the program.
REM This is a second way to run the project alongside run.bat (console menu) -
REM both call the exact same backend code, just with a different front end.

if not exist bin\campushub\RunGui.class (
    echo Project not compiled yet. Running compile.bat first...
    call compile.bat
)

java -cp "bin;lib\sqlite-jdbc.jar" campushub.RunGui
