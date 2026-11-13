@echo off
REM Compiles and runs the News ^& Bookmark Management System GUI prototype.
cd /d "%~dp0"

echo Compiling...
if not exist bin mkdir bin
javac -d bin src\Main.java src\model\*.java src\data\*.java src\ui\*.java
if errorlevel 1 goto :eof

echo Starting application...
java -cp bin Main
pause
