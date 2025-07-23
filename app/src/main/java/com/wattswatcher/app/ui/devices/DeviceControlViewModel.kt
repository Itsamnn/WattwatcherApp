package com.wattswatcher.app.ui.devices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wattswatcher.app.data.repository.WattsWatcherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceControlViewModel @Inject constructor(
    private val repository: WattsWatcherRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(DeviceControlState())
    val state: StateFlow<DeviceControlState> = _state.asStateFlow()
    
    init {
        fetchDevices()
    }
    
    private fun fetchDevices() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            
            repository.getDevices()
                .catch { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { devices ->
                            _state.value = _state.value.copy(
                                isLoading = false,
                                devices = devices,
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
    
    fun toggleDevice(deviceId: String, isOn: Boolean) {
        viewModelScope.launch {
            val result = repository.toggleDevice(deviceId, isOn)
            result.fold(
                onSuccess = { updatedDevice ->
                    val updatedDevices = _state.value.devices.map { device ->
                        if (device.id == deviceId) updatedDevice else device
                    }
                    _state.value = _state.value.copy(devices = updatedDevices)
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(error = error.message)
                }
            )
        }
    }
    
    fun toggleAllDevices(turnOn: Boolean) {
        viewModelScope.launch {
            _state.value.devices.forEach { device ->
                if (device.isOn != turnOn) {
                    toggleDevice(device.id, turnOn)
                }
            }
        }
    }
    
    fun refreshDevices() {
        fetchDevices()
    }
}