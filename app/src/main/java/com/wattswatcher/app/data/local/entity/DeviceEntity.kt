package com.wattswatcher.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.wattswatcher.app.data.model.Device

@Entity(tableName = "devices")
data class DeviceEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val wattage: Int,
    val isOn: Boolean,
    val timeTodayH: Double,
    val energyTodayKWh: Double,
    val lastUpdated: Long = System.currentTimeMillis()
)

fun DeviceEntity.toDevice(): Device {
    return Device(
        id = id,
        name = name,
        wattage = wattage,
        isOn = isOn,
        timeTodayH = timeTodayH,
        energyTodayKWh = energyTodayKWh
    )
}

fun Device.toEntity(): DeviceEntity {
    return DeviceEntity(
        id = id,
        name = name,
        wattage = wattage,
        isOn = isOn,
        timeTodayH = timeTodayH,
        energyTodayKWh = energyTodayKWh
    )
}