#!/bin/bash
# Compiles and runs the News & Bookmark Management System GUI prototype.
cd "$(dirname "$0")" || exit 1

echo "Compiling..."
mkdir -p bin
javac -d bin src/Main.java src/model/*.java src/data/*.java src/ui/*.java || exit 1

echo "Starting application..."
java -cp bin Main
