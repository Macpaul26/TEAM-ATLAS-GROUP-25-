@echo off
REM Runs the performance benchmark suite (6 experiments, 3-run averages)
REM and writes CSV files into the results\ folder.

if not exist bin\campushub\RunBenchmarks.class (
    echo Project not compiled yet. Running compile.bat first...
    call compile.bat
)

java -cp "bin" campushub.RunBenchmarks
pause
