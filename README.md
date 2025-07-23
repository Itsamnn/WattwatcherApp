# WattsWatcher - Smart Electricity Monitoring App

WattsWatcher is a comprehensive Android application built with Kotlin and Jetpack Compose that provides homeowners with clarity, control, and empowerment regarding their electricity usage. The app eliminates "bill shock" through transparent, data-rich, and intuitive mobile experience.

## 🚀 Features

### 📊 Dashboard
- **Live Power Monitoring**: Real-time wattage display with circular progress indicator
- **Key Metrics**: Monthly usage, estimated bill, voltage, and current readings
- **AI Anomaly Detection**: Smart alerts for unusual consumption patterns
- **Active Device Overview**: Quick view of currently running devices

### 🎛️ Device Control
- **Master Control**: Toggle all devices at once
- **Individual Device Management**: Control each device with detailed usage stats
- **Smart Scheduling**: Schedule devices for optimal energy usage (coming soon)
- **Usage Optimization**: AI-powered recommendations for energy savings

### 💳 Billing & Payments
- **Current Bill Summary**: Detailed breakdown of electricity charges
- **Secure Payment Gateway**: Multiple payment methods (UPI, Cards, Net Banking)
- **Tariff Structure**: Clear display of electricity rates
- **Payment History**: Track all past payments and transactions

### 📈 Analytics & Insights
- **Consumption Trends**: Interactive charts showing usage patterns
- **Device Breakdown**: Pie charts showing energy consumption by device
- **Smart Insights**: AI-powered recommendations for energy optimization
- **Multiple Time Periods**: Day, week, month, and yearly analytics

## 🏗️ Architecture

### Technology Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Async Programming**: Kotlin Coroutines & Flow
- **Dependency Injection**: Hilt
- **Networking**: Retrofit + OkHttp
- **JSON Parsing**: Gson
- **Local Database**: Room
- **Navigation**: Jetpack Navigation Compose

### Project Structure
```
app/
├── src/main/java/com/wattswatcher/app/
│   ├── data/
│   │   ├── api/           # API interfaces and responses
│   │   ├── local/         # Room database, DAOs, entities
│   │   ├── model/         # Data models
│   │   ├── repository/    # Repository pattern implementation
│   │   └── mock/          # Mock data for testing
│   ├── di/                # Dependency injection modules
│   ├── ui/
│   │   ├── components/    # Reusable UI components
│   │   ├── dashboard/     # Dashboard screen and ViewModel
│   │   ├── devices/       # Device control screen and ViewModel
│   │   ├── billing/       # Billing screen and ViewModel
│   │   ├── analytics/     # Analytics screen and ViewModel
│   │   └── theme/         # App theming and colors
│   ├── navigation/        # Navigation setup
│   ├── MainActivity.kt
│   └── WattsWatcherApplication.kt
```

## 🔧 Setup & Installation

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24 or higher
- Kotlin 1.9.10 or later

### Installation Steps
1. Clone the repository
2. Open the project in Android Studio
3. Sync the project with Gradle files
4. Update the API base URL in `NetworkModule.kt`
5. Build and run the application

### API Configuration
Update the base URL in `app/src/main/java/com/wattswatcher/app/di/NetworkModule.kt`:
```kotlin
.baseUrl("https://your-api-endpoint.com/")
```

## 📱 Screens Overview

### Dashboard Screen
- Live power usage with visual indicators
- Key metrics in card format
- Anomaly detection status
- Active devices list

### Device Control Screen
- Device statistics header
- Master control toggle
- Individual device cards with controls
- Usage statistics per device

### Billing Screen
- Current bill summary with due date
- Secure payment modal
- Tariff structure display
- Payment history

### Analytics Screen
- Time period filters (Day/Week/Month/Year)
- Consumption trend charts
- Device breakdown pie chart
- Smart insights and recommendations

## 🔒 Security Features

- Secure API communication with HTTPS
- Local data encryption with Room
- Secure payment processing
- No sensitive data stored in plain text

## 🎨 Design Philosophy

The app follows Material 3 design principles with:
- **Electric Blue** primary color scheme
- **Energy Green** for positive indicators
- **Warning Orange** and **Danger Red** for alerts
- Consistent spacing and typography
- Accessibility-compliant design

## 🚧 Future Enhancements

- [ ] Push notifications for anomaly alerts
- [ ] Device scheduling functionality
- [ ] Energy optimization recommendations
- [ ] Social sharing of energy savings
- [ ] Integration with smart home devices
- [ ] Voice control support
- [ ] Wear OS companion app

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Support

For support and questions, please contact the development team or create an issue in the repository.

---

**WattsWatcher** - Empowering homeowners with smart electricity monitoring and control.