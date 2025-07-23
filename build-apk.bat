@echo off
echo 🚀 WattsWatcher APK Build Script
echo ================================

echo.
echo 📋 Checking build requirements...
echo.

REM Check if Android SDK is available
if not defined ANDROID_HOME (
    echo ❌ ANDROID_HOME not set
    echo.
    echo 💡 To build APK, you need:
    echo    1. Android Studio installed
    echo    2. Android SDK configured
    echo    3. ANDROID_HOME environment variable set
    echo.
    echo 🎯 Recommended: Open this project in Android Studio and build from there
    echo.
    goto :android_studio_instructions
)

echo ✅ ANDROID_HOME found: %ANDROID_HOME%
echo.

REM Check if Java is available
java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Java not found in PATH
    echo 💡 Please install Java JDK 8 or higher
    goto :end
)

echo ✅ Java found
echo.

echo 🔨 Building WattsWatcher APK...
echo.

REM Build the APK
call gradlew.bat assembleDebug

if errorlevel 1 (
    echo.
    echo ❌ Build failed!
    echo 💡 Try opening the project in Android Studio instead
    goto :android_studio_instructions
)

echo.
echo 🎉 Build successful!
echo.
echo 📱 Your APK is ready at:
echo    app\build\outputs\apk\debug\app-debug.apk
echo.
echo 📋 APK Details:
echo    • Name: WattsWatcher
echo    • Version: 1.0
echo    • Size: ~15-20 MB
echo    • Min Android: 7.0 (API 24)
echo.
echo 🚀 Ready to install and test!
goto :end

:android_studio_instructions
echo.
echo 📱 Android Studio Build Instructions:
echo =====================================
echo.
echo 1. Open Android Studio
echo 2. Open this project folder
echo 3. Wait for Gradle sync
echo 4. Go to: Build → Build Bundle(s) / APK(s) → Build APK(s)
echo 5. APK will be at: app\build\outputs\apk\debug\app-debug.apk
echo.

:end
echo.
echo Press any key to exit...
pause >nul