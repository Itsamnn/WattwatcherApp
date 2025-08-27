package com.wattswatcher.app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wattswatcher.app.data.api.LiveData
import com.wattswatcher.app.data.model.BillSummary
import com.wattswatcher.app.data.model.Device
import com.wattswatcher.app.data.model.DevicePriority
import com.wattswatcher.app.data.repository.WattsWatcherRepository
import com.wattswatcher.app.simulation.DetectedAppliance
import com.wattswatcher.app.simulation.GridStatus
import kotlinx.coroutines.delay
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
    val error: String? = null,
    val gridStatus: GridStatus = GridStatus.STABLE,
    val detectedAppliance: DetectedAppliance? = null,
    val gsmMessages: List<String> = emptyList()
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
                
                // Use separate flow collection for simplicity and type safety
                viewModelScope.launch {
                    repository.getLiveDataStream().collect { liveData ->
                        _state.value = _state.value.copy(liveData = liveData)
                    }
                }
                
                viewModelScope.launch {
                    repository.getDevices().collect { devicesResult ->
                        devicesResult.fold(
                            onSuccess = { devices ->
                                val activeDevices = devices.filter { device -> device.isOn }
                                _state.value = _state.value.copy(activeDevices = activeDevices)
                            },
                            onFailure = { exception ->
                                _state.value = _state.value.copy(
                                    error = exception.message ?: "Failed to load devices"
                                )
                            }
                        )
                    }
                }
                
                viewModelScope.launch {
                    simulationEngine.detectedAppliance.collect { detectedAppliance ->
                        _state.value = _state.value.copy(detectedAppliance = detectedAppliance)
                    }
                }
                
                viewModelScope.launch {
                    simulationEngine.anomalies.collect { anomalies ->
                        _state.value = _state.value.copy(
                            anomalies = anomalies,
                            anomalyDetected = anomalies.isNotEmpty()
                        )
                    }
                }
                
                viewModelScope.launch {
                    simulationEngine.gridStatus.collect { gridStatus ->
                        _state.value = _state.value.copy(gridStatus = gridStatus)
                    }
                }
                
                viewModelScope.launch {
                    simulationEngine.gsmMessages.collect { gsmMessages ->
                        _state.value = _state.value.copy(
                            gsmMessages = gsmMessages.map { message -> "${message.sender}: ${message.command}" }
                        )
                    }
                }
                
                // Update bill and usage periodically
                viewModelScope.launch {
                    while(true) {
                        val currentBill = simulationEngine.getCurrentBillEstimate()
                        val currentUsage = simulationEngine.getMonthlyUsage()
                        
                        _state.value = _state.value.copy(
                            estimatedBill = currentBill,
                            monthlyUsage = currentUsage
                        )
                        
                        delay(5000) // Update every 5 seconds
                    }
                }
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
    
    /**
     * Trigger load shedding simulation
     */
    fun triggerLoadShedding() {
        viewModelScope.launch {
            val simulationEngine = repository.getSimulationEngine()
            simulationEngine.triggerLoadShedding()
        }
    }
    
    /**
     * End load shedding simulation
     */
    fun endLoadShedding() {
        viewModelScope.launch {
            val simulationEngine = repository.getSimulationEngine()
            simulationEngine.endLoadShedding()
        }
    }
    
    /**
     * Label a detected appliance
     */
    fun labelDetectedAppliance(name: String, type: String, room: String) {
        viewModelScope.launch {
            val simulationEngine = repository.getSimulationEngine()
            simulationEngine.labelDetectedAppliance(name, type, room)
        }
    }
    
    /**
     * Update device priority
     */
    fun updateDevicePriority(deviceId: String, priority: DevicePriority) {
        viewModelScope.launch {
            val simulationEngine = repository.getSimulationEngine()
            simulationEngine.updateDevicePriority(deviceId, priority)
        }
    }
}