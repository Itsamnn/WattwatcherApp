package com.wattswatcher.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Device(
    val id: String,
    val name: String,
    val type: String = "Unknown",
    val wattage: Double,
    var isOn: Boolean = false,
    val room: String = "Unknown",
    val icon: String = "⚡",
    
    // Usage tracking
    val dailyUsage: Double = 0.0, // kWh consumed today
    val monthlyUsage: Double = 0.0, // kWh consumed this month
    val lastUsed: Long = 0L, // Timestamp of last usage
    
    // Time tracking
    val timeTodayH: Double = 0.0, // Hours used today (for backward compatibility)
    val energyTodayKWh: Double = dailyUsage, // Energy consumed today (for backward compatibility)
    
    // Scheduling and optimization
    val schedules: List<String> = emptyList(), // List of scheduled on/off times
    val isScheduled: Boolean = schedules.isNotEmpty(),
    val efficiency: Double = 1.0, // Efficiency rating (0.0 to 1.0)
    
    // Status and metadata
    val status: DeviceStatus = if (isOn) DeviceStatus.ON else DeviceStatus.OFF,
    val priority: DevicePriority = DevicePriority.MEDIUM,
    val installationDate: Long = System.currentTimeMillis(),
    val lastMaintenance: Long = 0L,
    
    // Cost tracking
    val estimatedMonthlyCost: Double = 0.0,
    val actualMonthlyCost: Double = 0.0
) {
    // Computed properties
    val isHighPower: Boolean get() = wattage > 1000.0
    val isEssential: Boolean get() = priority == DevicePriority.HIGH
    val hoursUsedToday: Double get() = timeTodayH
    val costPerHour: Double get() = (wattage / 1000.0) * 4.5 // Assuming ₹4.5 per kWh
    
    // Helper methods
    fun getStatusColor(): String = when (status) {
        DeviceStatus.ON -> "#00D100"
        DeviceStatus.OFF -> "#9E9E9E"
        DeviceStatus.SCHEDULED -> "#2196F3"
        DeviceStatus.ERROR -> "#FF4500"
    }
    
    fun getEfficiencyRating(): String = when {
        efficiency >= 0.9 -> "Excellent"
        efficiency >= 0.8 -> "Good"
        efficiency >= 0.7 -> "Average"
        else -> "Poor"
    }
    
    fun getDailyEstimatedCost(): Double = dailyUsage * 4.5
    
    fun getMonthlyEstimatedCost(): Double = monthlyUsage * 4.5
}

@Serializable
enum class DeviceStatus {
    ON,
    OFF,
    SCHEDULED,
    ERROR
}

@Serializable
enum class DevicePriority {
    LOW,     // Can be turned off anytime (decorative lights, etc.)
    MEDIUM,  // Normal appliances (TV, AC, etc.)
    HIGH     // Essential devices (refrigerator, security systems, etc.)
}

@Serializable
enum class DeviceType {
    AIR_CONDITIONER,
    REFRIGERATOR,
    TELEVISION,
    WATER_HEATER,
    WASHING_MACHINE,
    MICROWAVE,
    LIGHTS,
    FAN,
    COMPUTER,
    ROUTER,
    OTHER
}

// Extension functions for easy device creation
fun createDevice(
    name: String,
    type: DeviceType,
    wattage: Double,
    room: String = "Unknown",
    priority: DevicePriority = DevicePriority.MEDIUM
): Device {
    val icon = when (type) {
        DeviceType.AIR_CONDITIONER -> "❄️"
        DeviceType.REFRIGERATOR -> "🧊"
        DeviceType.TELEVISION -> "📺"
        DeviceType.WATER_HEATER -> "🚿"
        DeviceType.WASHING_MACHINE -> "👕"
        DeviceType.MICROWAVE -> "🍽️"
        DeviceType.LIGHTS -> "💡"
        DeviceType.FAN -> "🌀"
        DeviceType.COMPUTER -> "💻"
        DeviceType.ROUTER -> "📡"
        DeviceType.OTHER -> "⚡"
    }
    
    return Device(
        id = "${type.name.lowercase()}_${room.lowercase().replace(" ", "_")}_${System.currentTimeMillis()}",
        name = name,
        type = type.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
        wattage = wattage,
        room = room,
        icon = icon,
        priority = priority
    )
}