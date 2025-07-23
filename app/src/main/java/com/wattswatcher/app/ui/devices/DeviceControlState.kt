package com.wattswatcher.app.ui.devices

import com.wattswatcher.app.data.model.Device

data class DeviceControlState(
    val isLoading: Boolean = false,
    val devices: List<Device> = emptyList(),
    val error: String? = null
) {
    val activeDevicesCount: Int
        get() = devices.count { it.isOn }
    
    val totalPower: Int
        get() = devices.filter { it.isOn }.sumOf { it.wattage }
    
    val totalRegisteredDevices: Int
        get() = devices.size
}