@echo off
REM Prints all six required trace tables (binary search, insertion sort,
REM merge sort, Dijkstra, Kruskal, knapsack DP).

if not exist bin\campushub\trace\Traces.class (
    echo Project not compiled yet. Running compile.bat first...
    call compile.bat
)

java -cp "bin" campushub.trace.Traces
pause
