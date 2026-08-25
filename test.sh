#!/bin/bash
# Runs all 154 unit tests and prints a PASS/FAIL summary.
if [ ! -f "bin/campushub/RunTests.class" ]; then
    echo "Project not compiled yet. Running compile.sh first..."
    ./compile.sh
fi
java -cp "bin" campushub.RunTests
