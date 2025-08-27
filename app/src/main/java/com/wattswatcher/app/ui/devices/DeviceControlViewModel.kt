package com.wattswatcher.app.ui.devices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wattswatcher.app.data.model.Device
import com.wattswatcher.app.data.model.DevicePriority
import com.wattswatcher.app.data.repository.WattsWatcherRepository
import com.wattswatcher.app.simulation.GridStatus
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DeviceControlState(
    val isLoading: Boolean = false,
    val devices: List<Device> = emptyList(),
    val activeDevicesCount: Int = 0,
    val totalPower: Double = 0.0,
    val totalRegisteredDevices: Int = 0,
    val error: String? = null,
    val gridStatus: GridStatus = GridStatus.STABLE
)

class DeviceControlViewModel(
    private val repository: WattsWatcherRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(DeviceControlState())
    val state: StateFlow<DeviceControlState> = _state.asStateFlow()
    
    init {
        fetchDevices()
        monitorGridStatus()
    }
    
    private fun fetchDevices() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            try {
                // Monitor grid status changes
                repository.getDevices().collect { result ->
                    result.fold(
                        onSuccess = { devices ->
                            val activeDevices = devices.filter { it.isOn }
                            val totalPower = activeDevices.sumOf { it.wattage }
                            
                            _state.value = _state.value.copy(
                                isLoading = false,
                                devices = devices,
                                activeDevicesCount = activeDevices.size,
                                totalPower = totalPower,
                                totalRegisteredDevices = devices.size,
                                error = null
                            )
                        },
                        onFailure = { exception ->
                            _state.value = _state.value.copy(
                                isLoading = false,
                                error = exception.message ?: "Failed to load devices"
                            )
                        }
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to fetch devices"
                )
            }
        }
    }
    
    fun toggleDevice(deviceId: String, isOn: Boolean) {
        viewModelScope.launch {
            try {
                val result = repository.toggleDevice(deviceId, isOn)
                result.fold(
                    onSuccess = {
                        // Device state will be updated through the flow
                    },
                    onFailure = { error ->
                        _state.value = _state.value.copy(error = error.message)
                    }
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }
    
    fun toggleAllDevices(turnOn: Boolean) {
        viewModelScope.launch {
            val result = repository.toggleAllDevices(turnOn)
            result.fold(
                onSuccess = {
                    // Refresh devices to get updated state
                    fetchDevices()
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(error = error.message)
                }
            )
        }
    }
    
    fun refreshDevices() {
        fetchDevices()
    }
    
    fun addDevice(device: Device) {
        repository.addDevice(device)
        // The device list will be updated through the flow
    }
    
    fun scheduleDevice(deviceId: String, startTime: String, endTime: String) {
        viewModelScope.launch {
            try {
                val result = repository.scheduleDevice(deviceId, startTime, endTime)
                result.fold(
                    onSuccess = {
                        // Device schedules updated
                    },
                    onFailure = { error ->
                        _state.value = _state.value.copy(error = error.message)
                    }
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }
    
    fun getOptimizationSuggestion(deviceId: String): String {
        return repository.getOptimizationSuggestion(deviceId)
    }
    
    fun dismissError() {
        _state.value = _state.value.copy(error = null)
    }
    
    /**
     * Monitor grid status changes from the simulation engine
     */
    private fun monitorGridStatus() {
        viewModelScope.launch {
            try {
                val simulationEngine = repository.getSimulationEngine()
                simulationEngine.gridStatus.collect { gridStatus ->
                    _state.value = _state.value.copy(gridStatus = gridStatus)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message ?: "Failed to monitor grid status")
            }
        }
    }
    
    /**
     * Update device priority
     */
    fun updateDevicePriority(deviceId: String, priority: DevicePriority) {
        viewModelScope.launch {
            try {
                val simulationEngine = repository.getSimulationEngine()
                simulationEngine.updateDevicePriority(deviceId, priority)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message ?: "Failed to update device priority")
            }
        }
    }
    
    /**
     * Trigger load shedding simulation
     */
    fun triggerLoadShedding() {
        viewModelScope.launch {
            try {
                val simulationEngine = repository.getSimulationEngine()
                simulationEngine.triggerLoadShedding()
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message ?: "Failed to trigger load shedding")
            }
        }
    }
    
    /**
     * End load shedding simulation
     */
    fun endLoadShedding() {
        viewModelScope.launch {
            try {
                val simulationEngine = repository.getSimulationEngine()
                simulationEngine.endLoadShedding()
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message ?: "Failed to end load shedding")
            }
        }
    }
}