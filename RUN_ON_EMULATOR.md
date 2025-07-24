# Running WattsWatcher on Android Emulator

## Method 1: Android Studio GUI
1. Open Android Studio
2. Open project: `F:\project\flutter app`
3. Tools → AVD Manager → Create Virtual Device
4. Choose Pixel 6/7 with API 34 (Android 14)
5. Click Run button

## Method 2: Command Line

### Start Emulator
```bash
# List available AVDs
emulator -list-avds

# Start specific AVD (replace 'Pixel_6_API_34' with your AVD name)
emulator -avd Pixel_6_API_34
```

### Install APK
```bash
# Build debug APK
./gradlew assembleDebug

# Install on running emulator
adb install app/build/outputs/apk/debug/app-debug.apk

# Or build and install in one step
./gradlew installDebug
```

## Method 3: Direct Run from Command Line
```bash
# This will build, install, and launch the app
./gradlew installDebug
adb shell am start -n com.wattswatcher.app/.MainActivity
```

## Troubleshooting

### If emulator is not detected:
```bash
# Check connected devices
adb devices

# Restart ADB if needed
adb kill-server
adb start-server
```

### If build fails:
```bash
# Clean and rebuild
./gradlew clean
./gradlew assembleDebug
```

## Expected App Features

Once running, you should see:

🏠 **Dashboard Screen**
- Live power gauge showing real-time usage
- Bill summary card
- Active devices list
- Anomaly alerts (if any)

⚡ **Device Control Screen**  
- List of devices with on/off toggles
- Power consumption stats
- Master control buttons

📊 **Analytics Screen**
- Usage charts and trends
- Device breakdown
- Period filters (Day/Week/Month)

💳 **Billing Screen**
- Current bill summary
- Payment simulation
- Tariff structure
- Payment history

⚙️ **Settings Screen**
- Theme selection (Light/Dark/System)
- Notification preferences
- App settings

## Performance Notes

The app includes a **SimulationEngine** that:
- Updates live data every 2 seconds
- Simulates realistic power fluctuations
- Detects usage anomalies
- Tracks device usage statistics

This makes the app feel alive and interactive even without real hardware!