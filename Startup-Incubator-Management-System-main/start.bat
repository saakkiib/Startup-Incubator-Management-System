@echo off
setlocal enabledelayedexpansion

title ===================================================
title    🚀 Startup Incubator Management System
title    One-Click Launcher (Windows)
title ===================================================
echo.
echo ==================================================
echo    🚀 Startup Incubator Management System
echo    One-Click Launcher for Windows
echo ==================================================
echo.

set "PROJECT_DIR=%~dp0"
set "PROJECT_DIR=%PROJECT_DIR:~0,-1%"
set "BACKEND_JAR=%PROJECT_DIR%\backend\target\management-0.0.1-SNAPSHOT.jar"
set "BACKEND_PORT=8085"
set "FRONTEND_URL=http://localhost/Startup-Incubator-Management-System-main/index.html"
set "XAMPP_DIR=C:\xampp"

REM ----- Step 1: Check MySQL -----
echo [1/4] Checking XAMPP MySQL...

tasklist /FI "IMAGENAME eq mysqld.exe" 2>NUL | find /I "mysqld.exe" >NUL
if "%ERRORLEVEL%"=="0" (
    echo    ✅ MySQL is running
) else (
    echo    ⚠️  MySQL is NOT running. Starting XAMPP MySQL...
    if exist "%XAMPP_DIR%\mysql\bin\mysqld.exe" (
        start /B "" "%XAMPP_DIR%\mysql\bin\mysqld.exe" --defaults-file="%XAMPP_DIR%\mysql\bin\my.ini"
        echo    ⏳ Waiting for MySQL to start...
        timeout /t 5 /nobreak >NUL
        
        tasklist /FI "IMAGENAME eq mysqld.exe" 2>NUL | find /I "mysqld.exe" >NUL
        if "!ERRORLEVEL!"=="0" (
            echo    ✅ MySQL started successfully
        ) else (
            echo    ❌ Failed to start MySQL.
            echo    Please open XAMPP Control Panel and start MySQL manually.
            pause
            exit /b 1
        )
    ) else (
        echo    ❌ XAMPP not found at %XAMPP_DIR%
        echo    Please start MySQL via XAMPP Control Panel first.
        pause
        exit /b 1
    )
)

REM ----- Step 2: Build JAR if needed -----
echo [2/4] Checking backend JAR...

if not exist "%BACKEND_JAR%" (
    echo    ⚠️  JAR not found. Building backend...
    cd /d "%PROJECT_DIR%\backend"
    call mvn clean package -DskipTests -q
    if errorlevel 1 (
        echo    ❌ Build failed. Make sure Maven (mvn) is installed.
        pause
        exit /b 1
    )
    echo    ✅ Build complete
) else (
    echo    ✅ Backend JAR found
)

REM ----- Step 3: Start Backend -----
echo [3/4] Starting backend server (port %BACKEND_PORT%)...

REM Kill any old Java process on our port
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :%BACKEND_PORT%') do (
    if "%%a" NEQ "" (
        echo    ⚠️  Old backend detected (PID: %%a). Restarting...
        taskkill /F /PID %%a >NUL 2>&1
        timeout /t 2 /nobreak >NUL
    )
)

REM Start backend
cd /d "%PROJECT_DIR%\backend"
start "Incubator-Backend" /B java -jar "%BACKEND_JAR%" > "%TEMP%\incubator-backend.log" 2>&1
echo    ✅ Backend started
echo    📄 Logs: %TEMP%\incubator-backend.log

REM Wait for backend to be ready
echo    ⏳ Waiting for backend to be ready...
set "READY="
for /l %%i in (1,1,30) do (
    >nul 2>&1 curl -s http://localhost:%BACKEND_PORT%/api/users && (
        echo    ✅ Backend is READY!
        set "READY=1"
        goto :ready
    )
    timeout /t 2 /nobreak >NUL
)
:ready
if not defined READY (
    echo    ⚠️  Backend may still be starting. Check logs.
)

REM ----- Step 4: Open Frontend -----
echo [4/4] Opening frontend...
start "" "%FRONTEND_URL%"
echo    ✅ Browser opened

echo.
echo ==================================================
echo    ✅ System is RUNNING!
echo.
echo    Frontend: %FRONTEND_URL%
echo    Backend:  http://localhost:%BACKEND_PORT%
echo    Logs:     %TEMP%\incubator-backend.log
echo.
echo    To STOP: Close this window or run:
echo      taskkill /F /IM java.exe
echo ==================================================
echo.

pause
