#!/bin/bash
# Runs the interactive console menu (the examiner entry point).
if [ ! -f "bin/campushub/Main.class" ]; then
    echo "Project not compiled yet. Running compile.sh first..."
    ./compile.sh
fi
java -cp "bin:lib/sqlite-jdbc.jar" campushub.Main
