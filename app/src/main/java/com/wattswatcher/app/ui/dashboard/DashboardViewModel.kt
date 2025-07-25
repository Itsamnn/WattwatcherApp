package com.wattswatcher.app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wattswatcher.app.data.api.LiveData
import com.wattswatcher.app.data.model.BillSummary
import com.wattswatcher.app.data.model.Device
import com.wattswatcher.app.data.repository.WattsWatcherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class DashboardState(
    val isLoading: Boolean = false,
    val liveData: LiveData? = null,
    val billSummary: BillSummary? = null,
    val activeDevices: List<Device> = emptyList(),
    val monthlyUsage: Double = 0.0,
    val estimatedBill: Double = 0.0,
    val anomalyDetected: Boolean = false,
    val anomalies: List<String> = emptyList(),
    val error: String? = null
)

class DashboardViewModel(
    private val repository: WattsWatcherRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()
    
    init {
        loadDashboardData()
        startLiveDataStream()
    }
    
    private fun loadDashboardData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            try {
                repository.getDashboardData().collect { result ->
                    result.fold(
                        onSuccess = { dashboardData ->
                            _state.value = _state.value.copy(
                                isLoading = false,
                                billSummary = dashboardData.billSummary,
                                monthlyUsage = dashboardData.billSummary.unitsConsumed,
                                estimatedBill = dashboardData.billSummary.amount,
                                activeDevices = dashboardData.activeDevices,
                                anomalies = dashboardData.anomalies,
                                anomalyDetected = dashboardData.anomalies.isNotEmpty(),
                                error = null
                            )
                        },
                        onFailure = { exception ->
                            _state.value = _state.value.copy(
                                isLoading = false,
                                error = exception.message ?: "Unknown error occurred"
                            )
                        }
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load dashboard data"
                )
            }
        }
    }
    
    private fun startLiveDataStream() {
        viewModelScope.launch {
            try {
                // Get simulation engine for real-time bill updates
                val simulationEngine = repository.getSimulationEngine()
                
                // Combine live data stream with device updates for real-time bill calculation
                combine(
                    repository.getLiveDataStream(),
                    repository.getDevices(),
                    simulationEngine.liveData,
                    simulationEngine.devices
                ) { liveData, devicesResult, _, _ ->
                    val devices = devicesResult.getOrNull() ?: emptyList()
                    val activeDevices = devices.filter { it.isOn }
                    
                    // Get real-time bill and usage from simulation engine
                    val currentBill = simulationEngine.getCurrentBillEstimate()
                    val currentUsage = simulationEngine.getMonthlyUsage()
                    
                    _state.value = _state.value.copy(
                        liveData = liveData,
                        activeDevices = activeDevices,
                        estimatedBill = currentBill,
                        monthlyUsage = currentUsage
                    )
                }.collect { }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "Failed to start live data stream"
                )
            }
        }
    }
    
    fun refresh() {
        loadDashboardData()
    }
    
    fun refreshData() {
        loadDashboardData()
    }
    
    fun dismissError() {
        _state.value = _state.value.copy(error = null)
    }
    
    fun dismissAnomaly(anomaly: String) {
        val updatedAnomalies = _state.value.anomalies.filter { it != anomaly }
        _state.value = _state.value.copy(
            anomalies = updatedAnomalies,
            anomalyDetected = updatedAnomalies.isNotEmpty()
        )
    }
}