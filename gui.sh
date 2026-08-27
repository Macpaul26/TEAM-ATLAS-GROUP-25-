#!/bin/bash
# Launches the graphical (Swing) version of the program.
# This is a second way to run the project alongside run.sh (console menu) -
# both call the exact same backend code, just with a different front end.
if [ ! -f "bin/campushub/RunGui.class" ]; then
    echo "Project not compiled yet. Running compile.sh first..."
    ./compile.sh
fi
# On Windows (even inside Git Bash), the underlying java.exe is still the
# Windows build and needs ; between classpath entries, not : - detect that
# here so this script works correctly on Windows, Mac, and Linux alike.
case "$OSTYPE" in
  msys*|cygwin*|win32*) SEP=";" ;;
  *) SEP=":" ;;
esac
java -cp "bin${SEP}lib/sqlite-jdbc.jar" campushub.RunGui
