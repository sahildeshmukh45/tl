@echo off
echo ========================================
echo    TeamLogger Frontend Startup Script
echo ========================================
echo.

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 17+ from https://adoptium.net/
    pause
    exit /b 1
)

echo Java version:
java -version
echo.

REM Check if JAR file exists
if not exist "teamlogger-frontend.jar" (
    echo ERROR: teamlogger-frontend.jar not found
    echo Please ensure the JAR file is in the same directory as this script
    pause
    exit /b 1
)

REM Create logs directory if it doesn't exist
if not exist "logs" mkdir logs

echo Starting TeamLogger Frontend...
echo.
echo Configuration:
echo - Backend URL: http://localhost:8080/api
echo - Logs: logs/frontend.log
echo - Window Size: 1200x800
echo.

REM Start the application with optimized settings
java -Xmx1g -Xms256m ^
     -Djavafx.application.platform=desktop ^
     -Dlogging.file.name=logs/frontend.log ^
     -jar teamlogger-frontend.jar

echo.
echo Frontend stopped.
pause 