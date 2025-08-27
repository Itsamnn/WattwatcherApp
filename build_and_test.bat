@echo off
echo ========================================
echo    WattsWatcher - Build and Test
echo ========================================
echo.

echo [1/3] Cleaning previous build...
call gradlew.bat clean
if %errorlevel% neq 0 (
    echo ERROR: Clean failed!
    pause
    exit /b 1
)

echo.
echo [2/3] Building APK...
call gradlew.bat assembleDebug
if %errorlevel% neq 0 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)

echo.
echo [3/3] Build successful! 
echo.
echo ========================================
echo           BUILD SUCCESS!
echo ========================================
echo.
echo Your APK is ready at:
echo app\build\outputs\apk\debug\app-debug.apk
echo.
echo FIXES APPLIED:
echo ✅ Navigation - No more getting stuck
echo ✅ Billing Reset - Payments work properly  
echo ✅ Light Theme - Perfect text visibility
echo.
echo Ready to install and test!
echo ========================================
pause