#!/bin/bash
# Runs the performance benchmark suite and writes CSVs into results/.
if [ ! -f "bin/campushub/RunBenchmarks.class" ]; then
    echo "Project not compiled yet. Running compile.sh first..."
    ./compile.sh
fi
java -cp "bin" campushub.RunBenchmarks
