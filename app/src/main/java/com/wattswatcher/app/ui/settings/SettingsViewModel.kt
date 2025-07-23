package com.wattswatcher.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wattswatcher.app.data.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {
    
    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            combine(
                userPreferences.themeMode,
                userPreferences.notificationsEnabled,
                userPreferences.anomalyAlerts,
                userPreferences.dailyReports,
                userPreferences.autoRefresh,
                userPreferences.refreshInterval,
                userPreferences.currency,
                userPreferences.language,
                userPreferences.energySavingMode,
                userPreferences.biometricAuth
            ) { values: Array<Any?> ->
                val themeMode = values[0] as String
                val notifications = values[1] as Boolean
                val anomaly = values[2] as Boolean
                val daily = values[3] as Boolean
                val autoRefresh = values[4] as Boolean
                val interval = values[5] as Int
                val currency = values[6] as String
                val language = values[7] as String
                val energySaving = values[8] as Boolean
                val biometric = values[9] as Boolean
                SettingsState(
                    themeMode = themeMode,
                    notificationsEnabled = notifications,
                    anomalyAlerts = anomaly,
                    dailyReports = daily,
                    autoRefresh = autoRefresh,
                    refreshInterval = interval,
                    currency = currency,
                    language = language,
                    energySavingMode = energySaving,
                    biometricAuth = biometric
                )
            }.collect { newState ->
                _state.value = newState
            }
        }
    }
    
    fun setThemeMode(themeMode: String) {
        viewModelScope.launch {
            userPreferences.setThemeMode(themeMode)
        }
    }
    
    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setNotificationsEnabled(enabled)
        }
    }
    
    fun setAnomalyAlerts(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setAnomalyAlerts(enabled)
        }
    }
    
    fun setDailyReports(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setDailyReports(enabled)
        }
    }
    
    fun setAutoRefresh(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setAutoRefresh(enabled)
        }
    }
    
    fun setRefreshInterval(interval: Int) {
        viewModelScope.launch {
            userPreferences.setRefreshInterval(interval)
        }
    }
    
    fun setCurrency(currency: String) {
        viewModelScope.launch {
            userPreferences.setCurrency(currency)
        }
    }
    
    fun setLanguage(language: String) {
        viewModelScope.launch {
            userPreferences.setLanguage(language)
        }
    }
    
    fun setEnergySavingMode(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setEnergySavingMode(enabled)
        }
    }
    
    fun setBiometricAuth(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setBiometricAuth(enabled)
        }
    }
}