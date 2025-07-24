package com.wattswatcher.app.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wattswatcher.app.data.api.*
import com.wattswatcher.app.data.repository.WattsWatcherRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AnalyticsState(
    val isLoading: Boolean = false,
    val selectedPeriod: String = "month",
    val analyticsData: AnalyticsResponse? = null,
    val error: String? = null
)

class AnalyticsViewModel(
    private val repository: WattsWatcherRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(AnalyticsState())
    val state: StateFlow<AnalyticsState> = _state.asStateFlow()
    
    init {
        loadAnalytics("month")
    }
    
    fun loadAnalytics(period: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true, 
                selectedPeriod = period,
                error = null
            )
            
            try {
                repository.getAnalytics(period).collect { result ->
                    result.fold(
                        onSuccess = { analyticsData ->
                            _state.value = _state.value.copy(
                                isLoading = false,
                                analyticsData = analyticsData,
                                error = null
                            )
                        },
                        onFailure = { exception ->
                            _state.value = _state.value.copy(
                                isLoading = false,
                                error = exception.message ?: "Failed to load analytics"
                            )
                        }
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load analytics data"
                )
            }
        }
    }
    
    fun selectPeriod(period: String) {
        if (period != _state.value.selectedPeriod) {
            loadAnalytics(period)
        }
    }
    
    fun refresh() {
        loadAnalytics(_state.value.selectedPeriod)
    }
    
    fun dismissError() {
        _state.value = _state.value.copy(error = null)
    }
}