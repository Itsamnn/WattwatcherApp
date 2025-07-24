package com.wattswatcher.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Enhanced Dark Color Scheme with WattsWatcher branding
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryPurple,
    onPrimary = Color.White,
    primaryContainer = PrimaryPurpleDark,
    onPrimaryContainer = Color(0xFFE8D5FF),
    
    secondary = EnergyGreen,
    onSecondary = Color.Black,
    secondaryContainer = EnergyGreenDarkLegacy,
    onSecondaryContainer = Color(0xFFE8F5E8),
    
    tertiary = InfoBlue,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF004A77),
    onTertiaryContainer = Color(0xFFD1E4FF),
    
    error = AlertRed,
    onError = Color.White,
    errorContainer = AlertRedDark,
    onErrorContainer = Color(0xFFFFE6E1),
    
    background = DarkBackground,
    onBackground = DarkPrimaryText,
    
    surface = DarkSurface,
    onSurface = DarkPrimaryText,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkSecondaryText,
    
    outline = DarkDivider,
    outlineVariant = Color(0xFF2A2A2A),
    
    inverseSurface = LightSurface,
    inverseOnSurface = LightPrimaryText,
    inversePrimary = PrimaryPurpleDark
)

// Enhanced Light Color Scheme with WattsWatcher branding
private val LightColorScheme = lightColorScheme(
    primary = PrimaryPurple,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8D5FF),
    onPrimaryContainer = PrimaryPurpleDark,
    
    secondary = EnergyGreen,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8F5E8),
    onSecondaryContainer = EnergyGreenDarkLegacy,
    
    tertiary = InfoBlue,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD1E4FF),
    onTertiaryContainer = Color(0xFF001D36),
    
    error = AlertRed,
    onError = Color.White,
    errorContainer = Color(0xFFFFE6E1),
    onErrorContainer = AlertRedDark,
    
    background = LightBackground,
    onBackground = LightPrimaryText,
    
    surface = LightSurface,
    onSurface = LightPrimaryText,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightSecondaryText,
    
    outline = LightDivider,
    outlineVariant = Color(0xFFE0E0E0),
    
    inverseSurface = DarkSurface,
    inverseOnSurface = DarkPrimaryText,
    inversePrimary = PrimaryPurpleLight
)

@Composable
fun WattsWatcherTheme(
    themeMode: String = "system",
    dynamicColor: Boolean = false, // Disabled by default to maintain brand consistency
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }
    
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Use surface color for status bar for better integration
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Theme extension properties for easy access to custom colors
object WattsWatcherColors {
    val gaugeGreen @Composable get() = GaugeGreen
    val gaugeYellow @Composable get() = GaugeYellow
    val gaugeOrange @Composable get() = GaugeOrange
    val gaugeRed @Composable get() = GaugeRed
    
    val deviceOn @Composable get() = DeviceOnGreen
    val deviceOff @Composable get() = DeviceOffGray
    val deviceScheduled @Composable get() = DeviceScheduledBlue
    val deviceError @Composable get() = DeviceErrorRed
    
    val billPaid @Composable get() = BillPaidGreen
    val billPending @Composable get() = BillPendingOrange
    val billOverdue @Composable get() = BillOverdueRed
    
    val chartColors @Composable get() = listOf(
        ChartBlue, ChartTeal, ChartIndigo, ChartDeepPurple,
        ChartPink, ChartCyan, ChartLime, ChartAmber
    )
}