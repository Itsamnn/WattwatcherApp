#!/bin/bash

echo "🚀 WattsWatcher APK Builder (Command Line)"
echo "=========================================="
echo

# Check Java
echo "📋 Checking Java installation..."
if ! command -v java &> /dev/null; then
    echo "❌ Java not found!"
    echo
    echo "💡 Please install Java JDK 8 or higher:"
    echo "   Ubuntu/Debian: sudo apt install openjdk-11-jdk"
    echo "   macOS: brew install openjdk@11"
    echo "   Or download from: https://adoptium.net/"
    echo
    exit 1
else
    echo "✅ Java found"
    java -version
fi

echo

# Check Android SDK
echo "📋 Checking Android SDK..."
if [ -z "$ANDROID_HOME" ]; then
    echo "❌ ANDROID_HOME not set"
    echo
    echo "💡 Quick Setup Options:"
    echo
    echo "🎯 Option 1: Download Android Command Line Tools"
    echo "   1. Go to: https://developer.android.com/studio#command-tools"
    echo "   2. Download 'Command line tools only'"
    echo "   3. Extract to ~/android-sdk/"
    echo "   4. export ANDROID_HOME=~/android-sdk"
    echo "   5. export PATH=\$PATH:\$ANDROID_HOME/tools/bin"
    echo
    echo "🎯 Option 2: Use GitHub Actions (Recommended)"
    echo "   1. Push this project to GitHub"
    echo "   2. GitHub builds APK automatically"
    echo "   3. Download from Actions tab"
    echo
    echo "🎯 Option 3: Docker Build"
    echo "   docker run --rm -v \$(pwd):/project -w /project mingc/android-build-box ./gradlew assembleDebug"
    echo
    exit 1
else
    echo "✅ ANDROID_HOME found: $ANDROID_HOME"
fi

# Make gradlew executable
chmod +x gradlew

echo
echo "🔨 Building WattsWatcher APK..."
echo

# Build the APK
./gradlew assembleDebug

if [ $? -eq 0 ]; then
    echo
    echo "🎉 BUILD SUCCESSFUL!"
    echo
    echo "📱 Your APK is ready:"
    echo "   📁 Location: app/build/outputs/apk/debug/app-debug.apk"
    echo "   📏 Size: ~15-20 MB"
    echo "   📱 Min Android: 7.0 (API 24)"
    echo "   🎯 Features: All 5 screens + theme switching"
    echo
    echo "🚀 Ready to install and test!"
    echo
    
    # Show APK info
    if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
        ls -lh app/build/outputs/apk/debug/app-debug.apk
    fi
else
    echo
    echo "❌ Build failed!"
    echo
    echo "💡 Try using GitHub Actions instead:"
    echo "   1. Push this project to GitHub"
    echo "   2. APK builds automatically"
    echo "   3. Download from Actions tab"
    echo
fi