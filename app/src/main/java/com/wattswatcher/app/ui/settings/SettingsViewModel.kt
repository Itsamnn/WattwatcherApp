package com.wattswatcher.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wattswatcher.app.data.preferences.UserPreferences
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

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
    val isLoading: Boolean = false,
    val error: String? = null
)

class SettingsViewModel(
    private val userPreferences: UserPreferences
) : ViewModel() {
    
    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            
            try {
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
                ) { values ->
                    val themeMode = values[0] as String
                    val notifications = values[1] as Boolean
                    val anomalies = values[2] as Boolean
                    val reports = values[3] as Boolean
                    val autoRefresh = values[4] as Boolean
                    val interval = values[5] as Int
                    val currency = values[6] as String
                    val language = values[7] as String
                    val energySaving = values[8] as Boolean
                    val biometric = values[9] as Boolean
                    
                    SettingsState(
                        themeMode = themeMode,
                        notificationsEnabled = notifications,
                        anomalyAlerts = anomalies,
                        dailyReports = reports,
                        autoRefresh = autoRefresh,
                        refreshInterval = interval,
                        currency = currency,
                        language = language,
                        energySavingMode = energySaving,
                        biometricAuth = biometric,
                        isLoading = false
                    )
                }.collect { newState ->
                    _state.value = newState
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load settings"
                )
            }
        }
    }
    
    fun setThemeMode(themeMode: String) {
        viewModelScope.launch {
            try {
                userPreferences.setThemeMode(themeMode)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }
    
    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                userPreferences.setNotificationsEnabled(enabled)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }
    
    fun setAnomalyAlerts(enabled: Boolean) {
        viewModelScope.launch {
            try {
                userPreferences.setAnomalyAlerts(enabled)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }
    
    fun setDailyReports(enabled: Boolean) {
        viewModelScope.launch {
            try {
                userPreferences.setDailyReports(enabled)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }
    
    fun setAutoRefresh(enabled: Boolean) {
        viewModelScope.launch {
            try {
                userPreferences.setAutoRefresh(enabled)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }
    
    fun setRefreshInterval(interval: Int) {
        viewModelScope.launch {
            try {
                userPreferences.setRefreshInterval(interval)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }
    
    fun setCurrency(currency: String) {
        viewModelScope.launch {
            try {
                userPreferences.setCurrency(currency)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }
    
    fun setLanguage(language: String) {
        viewModelScope.launch {
            try {
                userPreferences.setLanguage(language)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }
    
    fun setEnergySavingMode(enabled: Boolean) {
        viewModelScope.launch {
            try {
                userPreferences.setEnergySavingMode(enabled)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }
    
    fun setBiometricAuth(enabled: Boolean) {
        viewModelScope.launch {
            try {
                userPreferences.setBiometricAuth(enabled)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }
    
    fun dismissError() {
        _state.value = _state.value.copy(error = null)
    }
}