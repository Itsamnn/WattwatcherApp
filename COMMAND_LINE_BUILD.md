# 🚀 Build WattsWatcher APK Without Android Studio

## ✅ Yes, You Can Build Without Android Studio!

You only need:
1. **Java JDK 8+**
2. **Android SDK Command Line Tools**
3. **This project** (already complete!)

## 📋 Setup Requirements

### 1. Install Java JDK
```bash
# Check if Java is installed
java -version

# If not installed, download from:
# https://adoptium.net/ (recommended)
# or https://www.oracle.com/java/technologies/downloads/
```

### 2. Download Android SDK Command Line Tools
```bash
# Download from: https://developer.android.com/studio#command-tools
# Extract to a folder like: C:\android-sdk\
```

### 3. Set Environment Variables
```bash
# Windows (add to System Environment Variables):
ANDROID_HOME=C:\android-sdk
JAVA_HOME=C:\Program Files\Java\jdk-11

# Add to PATH:
%ANDROID_HOME%\tools\bin
%ANDROID_HOME%\platform-tools
%JAVA_HOME%\bin
```

### 4. Install Required SDK Components
```bash
# Run these commands:
sdkmanager "platform-tools"
sdkmanager "platforms;android-34"
sdkmanager "build-tools;34.0.0"
```

## 🔨 Build Commands

### Quick Build (Debug APK)
```bash
# Windows
.\gradlew.bat assembleDebug

# Linux/Mac
./gradlew assembleDebug
```

### Release Build (Optimized APK)
```bash
# Windows
.\gradlew.bat assembleRelease

# Linux/Mac
./gradlew assembleRelease
```

## 📁 APK Output Locations

### Debug APK
```
app\build\outputs\apk\debug\app-debug.apk
```

### Release APK
```
app\build\outputs\apk\release\app-release.apk
```

## 🎯 Alternative: Minimal Setup

If you don't want to install full Android SDK, you can use **GitHub Actions** or **Docker**:

### GitHub Actions (Free)
1. Push this project to GitHub
2. GitHub will build APK automatically
3. Download from Actions tab

### Docker Build
```bash
# Use Android build container
docker run --rm -v ${PWD}:/project -w /project \
  mingc/android-build-box:latest \
  ./gradlew assembleDebug
```

## 🚀 Ready-to-Use Build Script

I'll create a complete setup script for you!