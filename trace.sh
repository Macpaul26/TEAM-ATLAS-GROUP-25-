#!/bin/bash
# Prints all six required trace tables.
if [ ! -f "bin/campushub/trace/Traces.class" ]; then
    echo "Project not compiled yet. Running compile.sh first..."
    ./compile.sh
fi
java -cp "bin" campushub.trace.Traces
