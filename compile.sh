#!/bin/bash
# Compiles the whole project. Run this once (or after any code change)
# before using run.sh, test.sh, trace.sh or benchmarks.sh.
set -e
echo "Compiling Ghana Smart Service Operations Optimizer..."
mkdir -p bin
find src -name "*.java" > sources.txt
javac -cp "lib/sqlite-jdbc.jar" -d bin @sources.txt
echo ""
echo "Compiled successfully. You can now run ./run.sh, ./test.sh, ./trace.sh or ./benchmarks.sh"
