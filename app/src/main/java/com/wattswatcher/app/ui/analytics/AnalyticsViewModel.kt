package com.wattswatcher.app.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wattswatcher.app.data.repository.WattsWatcherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val repository: WattsWatcherRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(AnalyticsState())
    val state: StateFlow<AnalyticsState> = _state.asStateFlow()
    
    init {
        fetchAnalytics("week")
    }
    
    fun fetchAnalytics(period: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                selectedPeriod = period
            )
            
            repository.getAnalytics(period)
                .catch { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { analyticsResponse ->
                            _state.value = _state.value.copy(
                                isLoading = false,
                                historicalData = analyticsResponse.historicalData,
                                deviceBreakdown = analyticsResponse.deviceBreakdown,
                                error = null
                            )
                        },
                        onFailure = { error ->
                            _state.value = _state.value.copy(
                                isLoading = false,
                                error = error.message
                            )
                        }
                    )
                }
        }
    }
    
    fun selectPeriod(period: String) {
        if (period != _state.value.selectedPeriod) {
            fetchAnalytics(period)
        }
    }
}