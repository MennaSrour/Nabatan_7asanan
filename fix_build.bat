@echo off
echo ====================================
echo   Fixing Kotlin Daemon Build Error
echo ====================================
echo.

cd /d "D:\the chance\AndroidCompose\Nabata_7asena"

echo Step 1: Stopping all Gradle daemons...
call gradlew --stop
echo.

echo Step 2: Cleaning build directories...
if exist .gradle rmdir /s /q .gradle
if exist build rmdir /s /q build
if exist app\build rmdir /s /q app\build
echo.

echo Step 3: Running clean task...
call gradlew clean --no-daemon
echo.

echo Step 4: Building project...
call gradlew assembleDebug --no-daemon --refresh-dependencies
echo.

echo ====================================
echo   Build process completed!
echo ====================================
pause

