@echo off
setlocal enabledelayedexpansion

echo.
echo 🚀 WattsWatcher APK Builder (No Android Studio Required!)
echo =========================================================
echo.

REM Check Java
echo 📋 Checking Java installation...
java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Java not found!
    echo.
    echo 💡 Please install Java JDK 8 or higher:
    echo    https://adoptium.net/
    echo.
    goto :end
) else (
    echo ✅ Java found
    java -version 2>&1 | findstr "version"
)

echo.

REM Check Android SDK
echo 📋 Checking Android SDK...
if not defined ANDROID_HOME (
    echo ❌ ANDROID_HOME not set
    echo.
    echo 💡 Quick Setup Options:
    echo.
    echo 🎯 Option 1: Download Android Command Line Tools
    echo    1. Go to: https://developer.android.com/studio#command-tools
    echo    2. Download "Command line tools only"
    echo    3. Extract to C:\android-sdk\
    echo    4. Set ANDROID_HOME=C:\android-sdk
    echo    5. Add to PATH: %%ANDROID_HOME%%\tools\bin
    echo.
    echo 🎯 Option 2: Use Online Build Service
    echo    1. Push this project to GitHub
    echo    2. Use GitHub Actions to build APK
    echo    3. Download APK from Actions tab
    echo.
    echo 🎯 Option 3: Docker Build (if you have Docker)
    echo    docker run --rm -v %%CD%%:/project -w /project mingc/android-build-box ./gradlew assembleDebug
    echo.
    goto :manual_build
) else (
    echo ✅ ANDROID_HOME found: %ANDROID_HOME%
)

REM Check if SDK components are installed
echo.
echo 📋 Checking SDK components...
if not exist "%ANDROID_HOME%\platforms\android-34" (
    echo ❌ Android SDK 34 not found
    echo 💡 Installing required components...
    
    echo Installing platform-tools...
    call "%ANDROID_HOME%\tools\bin\sdkmanager.bat" "platform-tools"
    
    echo Installing Android 34 platform...
    call "%ANDROID_HOME%\tools\bin\sdkmanager.bat" "platforms;android-34"
    
    echo Installing build tools...
    call "%ANDROID_HOME%\tools\bin\sdkmanager.bat" "build-tools;34.0.0"
) else (
    echo ✅ SDK components found
)

echo.
echo 🔨 Building WattsWatcher APK...
echo.

REM Build the APK
call gradlew.bat assembleDebug

if errorlevel 1 (
    echo.
    echo ❌ Build failed!
    echo.
    goto :manual_build
)

echo.
echo 🎉 BUILD SUCCESSFUL!
echo.
echo 📱 Your APK is ready:
echo    📁 Location: app\build\outputs\apk\debug\app-debug.apk
echo    📏 Size: ~15-20 MB
echo    📱 Min Android: 7.0 (API 24)
echo    🎯 Features: All 5 screens + theme switching
echo.
echo 🚀 Ready to install and test!
echo.

REM Open APK folder
if exist "app\build\outputs\apk\debug\app-debug.apk" (
    echo 📂 Opening APK folder...
    start "" "app\build\outputs\apk\debug\"
)

goto :end

:manual_build
echo.
echo 🛠️  Manual Build Instructions:
echo ===============================
echo.
echo If automatic build doesn't work, you can:
echo.
echo 1️⃣ Use GitHub Actions (Easiest):
echo    • Push this project to GitHub
echo    • GitHub builds APK automatically
echo    • Download from Actions tab
echo.
echo 2️⃣ Use Online Build Services:
echo    • Appetize.io
echo    • CodeMagic
echo    • Bitrise
echo.
echo 3️⃣ Install Android Studio (Full featured):
echo    • Download from: https://developer.android.com/studio
echo    • Open project and build
echo.
echo 4️⃣ Use Docker (if available):
echo    docker run --rm -v %%CD%%:/project -w /project mingc/android-build-box ./gradlew assembleDebug
echo.

:end
echo.
echo Press any key to exit...
pause >nul