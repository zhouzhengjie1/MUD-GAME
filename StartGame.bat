@echo off
setlocal enabledelayedexpansion

REM Set UTF-8 encoding for proper Chinese character display
chcp 65001 >nul

set "JAVA_CMD=java"
where !JAVA_CMD! >nul 2>nul
if !errorlevel! neq 0 (
    echo Java not found. Please install Java 8 or higher.
    pause
    exit /b 1
)

set "GAME_TITLE=Wuxia World RPG Game"
title !GAME_TITLE!

echo Initializing game...
!JAVA_CMD! -cp . com.mud.game.MudGame

set "EXIT_CODE=!errorlevel!"
if !EXIT_CODE! equ 0 (
    echo.
    echo Game finished. Press any key to exit...
) else (
    echo.
    echo Game error [!EXIT_CODE!]. Press any key to exit...
)
pause >nul
endlocal