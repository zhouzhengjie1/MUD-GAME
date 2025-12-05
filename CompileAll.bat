@echo off

REM Batch script to compile all Java files

echo Compiling all Java files...

REM Create temporary file list
set "LIST_FILE=java_files.txt"

REM Find all Java files recursively
dir /s /b *.java > %LIST_FILE%

REM Compile all Java files
javac -d . @%LIST_FILE%

REM Check if compilation was successful
if %ERRORLEVEL% equ 0 (
    echo Compilation successful!
) else (
    echo Compilation failed!
    pause
    exit /b 1
)

REM Clean up temporary file
del %LIST_FILE%

echo Done!
pause
