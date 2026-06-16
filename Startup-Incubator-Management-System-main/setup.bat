@echo off
setlocal enabledelayedexpansion

title ==================================================
      🚀 IncubatorX — Windows Full Auto Setup
==================================================

cd /d "%~dp0"
set "PROJECT_DIR=%CD%"
set "BACKEND_DIR=%PROJECT_DIR%\backend"
set "JAVA_MIN_VER=17"

echo ==================================================
echo    🚀 Startup Incubator — Windows Auto Setup
echo    This will install everything needed automatically
echo ==================================================
echo.

REM ── 1. Java ──────────────────────────────────────────
echo [1/5] Checking Java...

where java >nul 2>&1
if !ERRORLEVEL! EQU 0 (
    for /f "tokens=3" %%v in ('java -version 2^>^&1 ^| findstr /i "version"') do set "JAVA_VER=%%v"
    echo    ✅ Java found: !JAVA_VER!
) else (
    echo    ⚠️  Java not found. Installing Java 17 (Temurin)...
    echo    Downloading...
    curl -L -o "%TEMP%\jdk17.msi" "https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.12%2B7/OpenJDK17U-jdk_x64_windows_hotspot_17.0.12_7.msi" --progress-bar
    if !ERRORLEVEL! NEQ 0 (
        echo    ❌ Download failed. Trying alternative link...
        curl -L -o "%TEMP%\jdk17.msi" "https://aka.ms/download-jdk/microsoft-jdk-17.0.12-windows-x64.msi" --progress-bar
    )
    echo    Installing silently...
    msiexec /i "%TEMP%\jdk17.msi" /quiet /norestart INSTALLDIR="C:\Program Files\Java\jdk-17"
    echo    Setting PATH...
    setx JAVA_HOME "C:\Program Files\Java\jdk-17" /M
    set "PATH=C:\Program Files\Java\jdk-17\bin;%PATH%"
    echo    ✅ Java 17 installed. Restart may be needed for PATH.
)

REM ── 2. Maven ────────────────────────────────────────
echo [2/5] Checking Maven...

where mvn >nul 2>&1
if !ERRORLEVEL! EQU 0 (
    echo    ✅ Maven found
) else (
    echo    ⚠️  Maven not found. Installing...
    if not exist "%PROGRAMFILES%\Maven" mkdir "%PROGRAMFILES%\Maven"
    curl -L -o "%TEMP%\maven.zip" "https://dlcdn.apache.org/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.zip" --progress-bar
    echo    Extracting...
    powershell -command "Expand-Archive -Path '%TEMP%\maven.zip' -DestinationPath '%PROGRAMFILES%\Maven\' -Force"
    setx MAVEN_HOME "%PROGRAMFILES%\Maven\apache-maven-3.9.9" /M
    set "PATH=%PROGRAMFILES%\Maven\apache-maven-3.9.9\bin;%PATH%"
    echo    ✅ Maven 3.9.9 installed
)

REM ── 3. XAMPP ────────────────────────────────────────
echo [3/5] Checking XAMPP...

if exist "C:\xampp\mysql\bin\mysqld.exe" (
    echo    ✅ XAMPP found at C:\xampp
) else (
    echo.
    echo    ⚠️  XAMPP not found at C:\xampp
    echo.
    echo    Please download and install XAMPP from:
    echo    👉 https://www.apachefriends.org/download.html
    echo.
    echo    After installing, run this script again.
    echo    OR: Press any key to open the download page now...
    pause >nul
    start https://www.apachefriends.org/download.html
    echo.
    echo    Press any key AFTER installing XAMPP to continue setup...
    pause >nul
)

REM ── 4. Database ─────────────────────────────────────
echo [4/5] Setting up database...

echo    Starting MySQL (if not already running)...
tasklist /FI "IMAGENAME eq mysqld.exe" 2>NUL | find /I "mysqld.exe" >NUL
if !ERRORLEVEL! NEQ 0 (
    start /B "" "C:\xampp\mysql\bin\mysqld.exe" --defaults-file="C:\xampp\mysql\bin\my.ini"
    timeout /t 5 /nobreak >NUL
)

echo    Creating database...
"C:\xampp\mysql\bin\mysql" -u root -e "CREATE DATABASE IF NOT EXISTS incubator_db;" 2>nul
if !ERRORLEVEL! EQU 0 (
    echo    ✅ Database 'incubator_db' ready
) else (
    echo    ⚠️  Could not create database. Check if MySQL is running.
)

REM ── 5. Build JAR ────────────────────────────────────
echo [5/5] Building Spring Boot backend...

cd /d "%BACKEND_DIR%"
call mvn clean package -DskipTests -q
if !ERRORLEVEL! EQU 0 (
    echo    ✅ Backend JAR built successfully!
) else (
    echo    ❌ Build failed. Check errors above.
    pause
    exit /b 1
)

echo.
echo ==================================================
echo    ✅ Setup Complete!
echo ==================================================
echo.
echo    Next steps:
echo    1. Make sure MySQL is running (XAMPP Control Panel)
echo    2. Double-click 'start.bat' to launch the system
echo    3. Or run:  java -jar "%BACKEND_DIR%\target\management-0.0.1-SNAPSHOT.jar"
echo.
echo    Then open: http://localhost/Startup-Incubator-Management-System-main/index.html
echo.

pause
