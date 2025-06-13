@echo off
echo ========================================
echo    TeamLogger Backend Startup Script
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
if not exist "teamlogger-backend.jar" (
    echo ERROR: teamlogger-backend.jar not found
    echo Please ensure the JAR file is in the same directory as this script
    pause
    exit /b 1
)

REM Create logs directory if it doesn't exist
if not exist "logs" mkdir logs

echo Starting TeamLogger Backend...
echo.
echo Configuration:
echo - Port: 8080
echo - Logs: logs/teamlogger.log
echo - Health Check: http://localhost:8080/actuator/health
echo.

REM Start the application with optimized settings
java -Xmx2g -Xms512m ^
     -Dspring.profiles.active=prod ^
     -Dlogging.file.name=logs/teamlogger.log ^
     -jar teamlogger-backend.jar

echo.
echo Backend stopped.
pause 