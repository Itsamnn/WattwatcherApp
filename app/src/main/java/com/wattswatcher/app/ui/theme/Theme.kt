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

// Premium Dark Color Scheme - Industry-ready energy dashboard
private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimaryAccent,                    // Neon Green
    onPrimary = Color.Black,
    primaryContainer = DarkSecondaryAccent,         // Midnight Blue
    onPrimaryContainer = DarkMainText,              // White Smoke
    
    secondary = DarkHighlightColor,                 // Soft Orange
    onSecondary = Color.Black,
    secondaryContainer = DarkCardBackground,        // Charcoal Slate
    onSecondaryContainer = DarkMainText,
    
    tertiary = EnergyBlue,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF001D36),
    onTertiaryContainer = Color(0xFFD1E4FF),
    
    error = EnergyRed,
    onError = Color.White,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    
    background = DarkBackground,                    // Gunmetal Dark
    onBackground = DarkMainText,                    // White Smoke
    
    surface = DarkCardBackground,                   // Charcoal Slate
    onSurface = DarkMainText,                       // White Smoke
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = DarkNeutralText,             // Soft Gray
    
    outline = Color(0xFF3A3A3A),
    outlineVariant = Color(0xFF2A2A2A),
    
    inverseSurface = LightBackground,
    inverseOnSurface = LightMainText,
    inversePrimary = LightPrimaryAccent
)

// Clean Light Color Scheme - High contrast for readability
private val LightColorScheme = lightColorScheme(
    primary = LightPrimaryAccent,                   // Darker Electric Blue
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE3F2FD),
    onPrimaryContainer = Color(0xFF0D47A1),
    
    secondary = LightSecondaryAccent,               // Darker Emerald Green
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8F5E8),
    onSecondaryContainer = Color(0xFF1B5E20),
    
    tertiary = LightWarningColor,                   // Darker Amber
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFF3E0),
    onTertiaryContainer = Color(0xFFE65100),
    
    error = Color(0xFFD32F2F),
    onError = Color.White,
    errorContainer = Color(0xFFFFEBEE),
    onErrorContainer = Color(0xFFB71C1C),
    
    background = LightBackground,                   // Pure White
    onBackground = LightMainText,                   // Pure Black
    
    surface = Color.White,
    onSurface = LightMainText,                      // Pure Black
    surfaceVariant = LightCardBackground,           // Very Light Gray
    onSurfaceVariant = LightSubText,                // Dark Gray
    
    outline = Color(0xFF9E9E9E),
    outlineVariant = Color(0xFFE0E0E0),
    
    inverseSurface = Color(0xFF121212),
    inverseOnSurface = Color(0xFFFFFFFF),
    inversePrimary = DarkPrimaryAccent
)

@Composable
fun WattsWatcherTheme(
    themeMode: String = "system",
    dynamicColor: Boolean = false, // Disabled to maintain brand consistency
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
            // Use background color for status bar for seamless integration
            window.statusBarColor = colorScheme.background.toArgb()
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
    val energyGreen @Composable get() = if (isSystemInDarkTheme()) DarkPrimaryAccent else LightSecondaryAccent
    val energyRed @Composable get() = EnergyRed
    val energyAmber @Composable get() = if (isSystemInDarkTheme()) DarkHighlightColor else LightWarningColor
    val energyBlue @Composable get() = if (isSystemInDarkTheme()) DarkSecondaryAccent else LightPrimaryAccent
    
    val cardBackground @Composable get() = if (isSystemInDarkTheme()) DarkCardBackground else Color.White
    val surfaceVariant @Composable get() = if (isSystemInDarkTheme()) SurfaceVariantDark else SurfaceVariant
    
    val deviceOn @Composable get() = if (isSystemInDarkTheme()) DarkPrimaryAccent else LightSecondaryAccent
    val deviceOff @Composable get() = if (isSystemInDarkTheme()) DarkNeutralText else LightSubText
    val deviceScheduled @Composable get() = if (isSystemInDarkTheme()) DarkSecondaryAccent else LightPrimaryAccent
    val deviceError @Composable get() = EnergyRed
    
    val billPaid @Composable get() = BillPaidColor
    val billPending @Composable get() = BillPendingColor
    val billOverdue @Composable get() = BillOverdueColor
    
    val chartColors @Composable get() = ChartColors
    
    val gradientStart @Composable get() = GradientStart
    val gradientEnd @Composable get() = GradientEnd
}