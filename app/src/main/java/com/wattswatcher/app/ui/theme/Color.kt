package com.wattswatcher.app.ui.theme

import androidx.compose.ui.graphics.Color

// Light Theme Colors - Inspired by modern energy apps
val LightBackground = Color(0xFFF9FAFB)        // Soft Pearl White
val LightCardBackground = Color(0xFFE0E0E0)     // Light Gray
val LightPrimaryAccent = Color(0xFF2979FF)      // Electric Blue
val LightSecondaryAccent = Color(0xFF00C853)    // Emerald Green
val LightWarningColor = Color(0xFFFF6F00)       // Bright Amber
val LightMainText = Color(0xFF212121)           // Rich Charcoal
val LightSubText = Color(0xFF757575)            // Muted Gray

// Dark Theme Colors - Premium dark energy dashboard
val DarkBackground = Color(0xFF0B0F1A)          // Gunmetal Dark
val DarkCardBackground = Color(0xFF1C2331)      // Charcoal Slate
val DarkPrimaryAccent = Color(0xFF00FFAA)       // Neon Green
val DarkSecondaryAccent = Color(0xFF003F5C)     // Midnight Blue
val DarkHighlightColor = Color(0xFFFFA600)      // Soft Orange
val DarkNeutralText = Color(0xFFB0BEC5)         // Soft Gray
val DarkMainText = Color(0xFFECEFF1)            // White Smoke

// Energy-specific colors
val EnergyGreen = Color(0xFF00C853)             // Active/On state
val EnergyRed = Color(0xFFFF1744)               // Critical/Off state
val EnergyAmber = Color(0xFFFF6F00)             // Warning state
val EnergyBlue = Color(0xFF2979FF)              // Information state

// Gauge colors for power consumption
val GaugeGreen = Color(0xFF4CAF50)              // Low usage (0-30%)
val GaugeYellow = Color(0xFFFFEB3B)             // Medium usage (30-70%)
val GaugeOrange = Color(0xFFFF9800)             // High usage (70-90%)
val GaugeRed = Color(0xFFF44336)                // Critical usage (90%+)

// Chart colors for analytics
val ChartColors = listOf(
    Color(0xFF2979FF),  // Electric Blue
    Color(0xFF00C853),  // Emerald Green
    Color(0xFFFF6F00),  // Bright Amber
    Color(0xFF9C27B0),  // Purple
    Color(0xFF00BCD4),  // Cyan
    Color(0xFFFF5722),  // Deep Orange
    Color(0xFF607D8B),  // Blue Grey
    Color(0xFF795548)   // Brown
)

// Device status colors
val DeviceOnColor = Color(0xFF00FFAA)           // Neon Green for dark theme
val DeviceOffColor = Color(0xFF757575)          // Muted Gray
val DeviceScheduledColor = Color(0xFF2979FF)    // Electric Blue
val DeviceErrorColor = Color(0xFFFF1744)        // Energy Red

// Bill status colors
val BillPaidColor = Color(0xFF00C853)           // Emerald Green
val BillPendingColor = Color(0xFFFF6F00)        // Bright Amber
val BillOverdueColor = Color(0xFFFF1744)        // Energy Red

// Gradient colors for modern cards
val GradientStart = Color(0xFF2979FF)
val GradientEnd = Color(0xFF00C853)

// Surface variations
val SurfaceVariant = Color(0xFFF5F5F5)
val SurfaceVariantDark = Color(0xFF2A2A2A)

// Legacy compatibility colors
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Additional utility colors
val TransparentWhite = Color(0x80FFFFFF)
val TransparentBlack = Color(0x80000000)
val DividerColor = Color(0x1F000000)
val DividerColorDark = Color(0x1FFFFFFF)