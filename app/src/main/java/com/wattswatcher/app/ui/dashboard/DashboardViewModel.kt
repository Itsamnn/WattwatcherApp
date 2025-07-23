package com.wattswatcher.app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wattswatcher.app.data.repository.WattsWatcherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: WattsWatcherRepository,
    private val userPreferences: com.wattswatcher.app.data.preferences.UserPreferences
) : ViewModel() {
    
    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()
    
    init {
        startLiveDataUpdates()
    }
    
    private fun startLiveDataUpdates() {
        viewModelScope.launch {
            userPreferences.autoRefresh.combine(userPreferences.refreshInterval) { autoRefresh, interval ->
                autoRefresh to interval
            }.collect { (autoRefresh, interval) ->
                if (autoRefresh) {
                    while (true) {
                        fetchDashboardData()
                        delay(interval * 1000L) // Convert seconds to milliseconds
                    }
                }
            }
        }
    }
    
    private fun fetchDashboardData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            
            repository.getDashboardData()
                .catch { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { dashboardResponse ->
                            _state.value = _state.value.copy(
                                isLoading = false,
                                liveData = dashboardResponse.liveData,
                                billSummary = dashboardResponse.billSummary,
                                activeDevices = dashboardResponse.activeDevices,
                                monthlyUsage = dashboardResponse.billSummary.unitsConsumed,
                                estimatedBill = dashboardResponse.billSummary.amount,
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
    
    fun refreshData() {
        fetchDashboardData()
    }
}