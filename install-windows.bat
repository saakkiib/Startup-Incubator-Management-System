@echo off
:: ============================================================
:: install-windows.bat — One-time setup for Windows
:: Run as Administrator: Right-click → "Run as administrator"
:: After this, everything starts automatically.
:: ============================================================

title IncubatorX — Windows Auto Setup
color 0A
echo.
echo ================================================
echo    IncubatorX — Windows Auto Setup
echo ================================================
echo.

SET "PROJECT_DIR=C:\xampp\htdocs\Startup-Incubator-Management-System-main"
SET "BACKEND_DIR=%PROJECT_DIR%\backend"
SET "JAR_PATH=%BACKEND_DIR%\target\management-0.0.1-SNAPSHOT.jar"

:: ── Check Admin ───────────────────────────────────────────────────────────────
net session >nul 2>&1
if %errorLevel% neq 0 (
    echo [ERROR] Please run this script as Administrator!
    echo Right-click the file and choose "Run as administrator"
    pause
    exit /b 1
)

:: ── 1. Chocolatey ─────────────────────────────────────────────────────────────
echo [1/5] Checking Chocolatey...
where choco >nul 2>&1
if %errorLevel% neq 0 (
    echo      Installing Chocolatey...
    powershell -NoProfile -ExecutionPolicy Bypass -Command "Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.SecurityProtocolType]::Tls12; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))"
    echo      [OK] Chocolatey installed.
) else (
    echo      [OK] Chocolatey already installed.
)

:: Refresh PATH
call refreshenv >nul 2>&1

:: ── 2. Java ────────────────────────────────────────────────────────────────────
echo [2/5] Checking Java...
where java >nul 2>&1
if %errorLevel% neq 0 (
    echo      Installing Java 17...
    choco install openjdk17 -y
    call refreshenv >nul 2>&1
    echo      [OK] Java 17 installed.
) else (
    echo      [OK] Java already installed.
)

:: ── 3. Maven ───────────────────────────────────────────────────────────────────
echo [3/5] Checking Maven...
where mvn >nul 2>&1
if %errorLevel% neq 0 (
    echo      Installing Maven...
    choco install maven -y
    call refreshenv >nul 2>&1
    echo      [OK] Maven installed.
) else (
    echo      [OK] Maven already installed.
)

:: ── 4. XAMPP check ─────────────────────────────────────────────────────────────
echo [4/5] Checking XAMPP...
if not exist "C:\xampp\mysql\bin\mysql.exe" (
    echo      [WARNING] XAMPP not found at C:\xampp
    echo      Trying D:\xampp...
    if not exist "D:\xampp\mysql\bin\mysql.exe" (
        echo      [ERROR] XAMPP not found!
        echo      Please download from: https://www.apachefriends.org/download.html
        echo      Install to C:\xampp then re-run this script.
        pause
        exit /b 1
    ) else (
        SET "PROJECT_DIR=D:\xampp\htdocs\Startup-Incubator-Management-System-main"
        SET "BACKEND_DIR=%PROJECT_DIR%\backend"
        SET "JAR_PATH=%BACKEND_DIR%\target\management-0.0.1-SNAPSHOT.jar"
        SET "MYSQL=D:\xampp\mysql\bin\mysql.exe"
    )
) else (
    SET "MYSQL=C:\xampp\mysql\bin\mysql.exe"
    echo      [OK] XAMPP found.
)

:: ── 5. Build JAR ───────────────────────────────────────────────────────────────
echo [5/5] Building Spring Boot backend...
cd /d "%BACKEND_DIR%"
call mvn package -DskipTests -q
if %errorLevel% neq 0 (
    echo      [ERROR] Build failed! Check Maven output above.
    pause
    exit /b 1
)
echo      [OK] Backend JAR built successfully.

:: ── Setup auto-start as Windows Task ──────────────────────────────────────────
echo.
echo Setting up auto-start on Windows login...

:: Create a startup batch file
SET "STARTUP_SCRIPT=%APPDATA%\Microsoft\Windows\Start Menu\Programs\Startup\incubator-backend.bat"

(
echo @echo off
echo start "" /B javaw -jar "%JAR_PATH%"
) > "%STARTUP_SCRIPT%"

echo [OK] Auto-start configured (runs on every Windows login).

:: Start it now
echo Starting backend now...
start "" /B javaw -jar "%JAR_PATH%"

echo.
echo ================================================
echo    Setup Complete!
echo ================================================
echo.
echo Open your browser and go to:
echo    http://localhost/Startup-Incubator-Management-System-main/index.html
echo.
echo Everything will start automatically from now on.
echo.
pause
