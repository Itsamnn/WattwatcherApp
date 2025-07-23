package com.wattswatcher.app.ui.settings

data class SettingsState(
    val themeMode: String = "system",
    val notificationsEnabled: Boolean = true,
    val anomalyAlerts: Boolean = true,
    val dailyReports: Boolean = false,
    val autoRefresh: Boolean = true,
    val refreshInterval: Int = 10,
    val currency: String = "INR",
    val language: String = "en",
    val energySavingMode: Boolean = false,
    val biometricAuth: Boolean = false,
    val isLoading: Boolean = false
)