package com.wattswatcher.app.ui.theme

import androidx.compose.ui.graphics.Color

// Primary Brand Colors - Vibrant Purple as the main accent
val PrimaryPurple = Color(0xFFA020F0)
val PrimaryPurpleLight = Color(0xFFB84FFF)
val PrimaryPurpleDark = Color(0xFF8B00E6)

// Secondary Colors - Energetic Green for positive states
val EnergyGreen = Color(0xFF00D100)
val EnergyGreenLight = Color(0xFF4AFF4A)
val EnergyGreenDark = Color(0xFF00A000)

// Alert Colors - Clear Red for warnings and alerts
val AlertRed = Color(0xFFFF4500)
val AlertRedLight = Color(0xFFFF6B3D)
val AlertRedDark = Color(0xFFE63900)

// Additional Accent Colors
val WarningOrange = Color(0xFFFF8C00)
val InfoBlue = Color(0xFF007AFF)
val SuccessGreen = Color(0xFF34C759)

// Light Theme Colors
val LightBackground = Color(0xFFFFFFFF)
val LightSurface = Color(0xFFF5F5F5)
val LightSurfaceVariant = Color(0xFFEFEFEF)
val LightCardSurface = Color(0xFFFAFAFA)
val LightPrimaryText = Color(0xFF1E1E1E)
val LightSecondaryText = Color(0xFF606060)
val LightTertiaryText = Color(0xFF8A8A8A)
val LightDivider = Color(0xFFE0E0E0)

// Dark Theme Colors
val DarkBackground = Color(0xFF121212)
val DarkSurface = Color(0xFF1E1E1E)
val DarkSurfaceVariant = Color(0xFF2A2A2A)
val DarkCardSurface = Color(0xFF252525)
val DarkPrimaryText = Color(0xFFE0E0E0)
val DarkSecondaryText = Color(0xFFA0A0A0)
val DarkTertiaryText = Color(0xFF707070)
val DarkDivider = Color(0xFF3A3A3A)

// Gauge Colors - For the live power gauge
val GaugeGreen = Color(0xFF4CAF50)    // Low usage (0-30%)
val GaugeYellow = Color(0xFFFFEB3B)   // Medium usage (30-70%)
val GaugeOrange = Color(0xFFFF9800)   // High usage (70-90%)
val GaugeRed = Color(0xFFF44336)      // Critical usage (90%+)

// Chart Colors - For analytics charts
val ChartBlue = Color(0xFF2196F3)
val ChartTeal = Color(0xFF009688)
val ChartIndigo = Color(0xFF3F51B5)
val ChartDeepPurple = Color(0xFF673AB7)
val ChartPink = Color(0xFFE91E63)
val ChartCyan = Color(0xFF00BCD4)
val ChartLime = Color(0xFFCDDC39)
val ChartAmber = Color(0xFFFFC107)

// Device Status Colors
val DeviceOnGreen = EnergyGreen
val DeviceOffGray = Color(0xFF9E9E9E)
val DeviceScheduledBlue = Color(0xFF2196F3)
val DeviceErrorRed = AlertRed

// Bill Status Colors
val BillPaidGreen = SuccessGreen
val BillPendingOrange = WarningOrange
val BillOverdueRed = AlertRed

// Legacy colors for compatibility
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Existing colors for backward compatibility
val ElectricBlue = Color(0xFF2196F3)
val ElectricBlueVariant = Color(0xFF1976D2)
val WarningOrangeLegacy = Color(0xFFFF9800)
val DangerRed = Color(0xFFF44336)

val ElectricBlueDark = Color(0xFF90CAF9)
val ElectricBlueVariantDark = Color(0xFF64B5F6)
val EnergyGreenDarkLegacy = Color(0xFF81C784)
val WarningOrangeDark = Color(0xFFFFB74D)
val DangerRedDark = Color(0xFFEF5350)

val SurfaceLight = Color(0xFFFFFBFE)
val SurfaceDark = Color(0xFF1C1B1F)
val SurfaceVariantLight = Color(0xFFE7E0EC)
val SurfaceVariantDark = Color(0xFF49454F)