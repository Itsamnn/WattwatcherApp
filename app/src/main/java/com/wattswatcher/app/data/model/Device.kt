package com.wattswatcher.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Device(
    val id: String,
    val name: String,
    val wattage: Int,
    var isOn: Boolean,
    val timeTodayH: Double,
    val energyTodayKWh: Double
)