package com.wattswatcher.app.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.wattswatcher.app.WattsWatcherApplication
import com.wattswatcher.app.ui.components.SettingsSection
import com.wattswatcher.app.ui.components.SettingsItem
import com.wattswatcher.app.ui.components.ThemeSelectionDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val app = LocalContext.current.applicationContext as WattsWatcherApplication
    val viewModel: SettingsViewModel = viewModel {
        SettingsViewModel(app.userPreferences)
    }
    val state by viewModel.state.collectAsState()
    var showThemeDialog by remember { mutableStateOf(false) }
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showRefreshIntervalDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Appearance Section
            item {
                SettingsSection(title = "Appearance") {
                    SettingsItem(
                        icon = Icons.Default.Palette,
                        title = "Theme",
                        subtitle = getThemeDisplayName(state.themeMode),
                        onClick = { showThemeDialog = true }
                    )
                    
                    SettingsItem(
                        icon = Icons.Default.Language,
                        title = "Language",
                        subtitle = getLanguageDisplayName(state.language),
                        onClick = { showLanguageDialog = true }
                    )
                    
                    SettingsItem(
                        icon = Icons.Default.CurrencyRupee,
                        title = "Currency",
                        subtitle = state.currency,
                        onClick = { showCurrencyDialog = true }
                    )
                }
            }
            
            // Notifications Section
            item {
                SettingsSection(title = "Notifications") {
                    SettingsItem(
                        icon = Icons.Default.Notifications,
                        title = "Enable Notifications",
                        subtitle = "Receive app notifications",
                        isSwitch = true,
                        switchState = state.notificationsEnabled,
                        onSwitchChange = viewModel::setNotificationsEnabled
                    )
                    
                    SettingsItem(
                        icon = Icons.Default.Warning,
                        title = "Anomaly Alerts",
                        subtitle = "Get alerts for unusual energy usage",
                        isSwitch = true,
                        switchState = state.anomalyAlerts,
                        onSwitchChange = viewModel::setAnomalyAlerts,
                        enabled = state.notificationsEnabled
                    )
                    
                    SettingsItem(
                        icon = Icons.Default.Schedule,
                        title = "Daily Reports",
                        subtitle = "Receive daily energy usage reports",
                        isSwitch = true,
                        switchState = state.dailyReports,
                        onSwitchChange = viewModel::setDailyReports,
                        enabled = state.notificationsEnabled
                    )
                }
            }
            
            // Data & Sync Section
            item {
                SettingsSection(title = "Data & Sync") {
                    SettingsItem(
                        icon = Icons.Default.Refresh,
                        title = "Auto Refresh",
                        subtitle = "Automatically refresh data",
                        isSwitch = true,
                        switchState = state.autoRefresh,
                        onSwitchChange = viewModel::setAutoRefresh
                    )
                    
                    SettingsItem(
                        icon = Icons.Default.Timer,
                        title = "Refresh Interval",
                        subtitle = "${state.refreshInterval} seconds",
                        onClick = { showRefreshIntervalDialog = true },
                        enabled = state.autoRefresh
                    )
                    
                    SettingsItem(
                        icon = Icons.Default.BatteryAlert,
                        title = "Energy Saving Mode",
                        subtitle = "Reduce background activity",
                        isSwitch = true,
                        switchState = state.energySavingMode,
                        onSwitchChange = viewModel::setEnergySavingMode
                    )
                }
            }
            
            // Security Section
            item {
                SettingsSection(title = "Security") {
                    SettingsItem(
                        icon = Icons.Default.Fingerprint,
                        title = "Biometric Authentication",
                        subtitle = "Use fingerprint or face unlock",
                        isSwitch = true,
                        switchState = state.biometricAuth,
                        onSwitchChange = viewModel::setBiometricAuth
                    )
                }
            }
            
            // About Section
            item {
                SettingsSection(title = "About") {
                    SettingsItem(
                        icon = Icons.Default.Info,
                        title = "App Version",
                        subtitle = "1.0.0",
                        onClick = { /* Show version info */ }
                    )
                    
                    SettingsItem(
                        icon = Icons.Default.Help,
                        title = "Help & Support",
                        subtitle = "Get help and contact support",
                        onClick = { /* Open help */ }
                    )
                    
                    SettingsItem(
                        icon = Icons.Default.PrivacyTip,
                        title = "Privacy Policy",
                        subtitle = "View privacy policy",
                        onClick = { /* Open privacy policy */ }
                    )
                    
                    SettingsItem(
                        icon = Icons.Default.Gavel,
                        title = "Terms of Service",
                        subtitle = "View terms and conditions",
                        onClick = { /* Open terms */ }
                    )
                }
            }
        }
    }
    
    // Theme Selection Dialog
    if (showThemeDialog) {
        ThemeSelectionDialog(
            currentTheme = state.themeMode,
            onThemeSelected = { theme ->
                viewModel.setThemeMode(theme)
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }
    
    // Currency Selection Dialog
    if (showCurrencyDialog) {
        CurrencySelectionDialog(
            currentCurrency = state.currency,
            onCurrencySelected = { currency ->
                viewModel.setCurrency(currency)
                showCurrencyDialog = false
            },
            onDismiss = { showCurrencyDialog = false }
        )
    }
    
    // Language Selection Dialog
    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = state.language,
            onLanguageSelected = { language ->
                viewModel.setLanguage(language)
                showLanguageDialog = false
            },
            onDismiss = { showLanguageDialog = false }
        )
    }
    
    // Refresh Interval Dialog
    if (showRefreshIntervalDialog) {
        RefreshIntervalDialog(
            currentInterval = state.refreshInterval,
            onIntervalSelected = { interval ->
                viewModel.setRefreshInterval(interval)
                showRefreshIntervalDialog = false
            },
            onDismiss = { showRefreshIntervalDialog = false }
        )
    }
}

@Composable
private fun getThemeDisplayName(themeMode: String): String {
    return when (themeMode) {
        "light" -> "Light"
        "dark" -> "Dark"
        "system" -> "System Default"
        else -> "System Default"
    }
}

@Composable
private fun getLanguageDisplayName(language: String): String {
    return when (language) {
        "en" -> "English"
        "hi" -> "हिंदी"
        "es" -> "Español"
        "fr" -> "Français"
        else -> "English"
    }
}