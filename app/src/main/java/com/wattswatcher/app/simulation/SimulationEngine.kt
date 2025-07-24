package com.wattswatcher.app.simulation

import com.wattswatcher.app.data.model.Device
import com.wattswatcher.app.data.api.LiveData
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.math.abs
import kotlin.random.Random

/**
 * The heart of the WattsWatcher app - makes everything feel alive and interactive
 * This engine simulates real-time energy consumption, device behavior, and anomaly detection
 */
class SimulationEngine {
    
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    // Core simulation state
    private val _devices = MutableStateFlow<List<Device>>(emptyList())
    val devices: StateFlow<List<Device>> = _devices.asStateFlow()
    
    private val _liveData = MutableStateFlow(
        LiveData(
            currentUsage = 0.0,
            voltage = 230.0,
            frequency = 50.0,
            powerFactor = 0.95,
            timestamp = System.currentTimeMillis()
        )
    )
    val liveData: StateFlow<LiveData> = _liveData.asStateFlow()
    
    private val _anomalies = MutableStateFlow<List<String>>(emptyList())
    val anomalies: StateFlow<List<String>> = _anomalies.asStateFlow()
    
    // Simulation parameters
    private var baseLoad = 200.0 // Base household load in watts
    private var simulationSpeed = 1.0 // 1.0 = real time, 2.0 = 2x speed
    private var monthlyBudget = 1500.0 // User's monthly budget
    private var currentMonthUsage = 0.0 // kWh consumed this month
    private var electricityRate = 4.5 // Rate per kWh
    
    // Anomaly detection state
    private var highUsageStartTime = 0L
    private var lastAnomalyCheck = 0L
    
    init {
        startSimulation()
    }
    
    /**
     * Initialize the simulation with user preferences
     */
    fun initializeSimulation(
        monthlyBudget: Double,
        electricityRate: Double,
        initialDevices: List<Device> = getDefaultDevices()
    ) {
        this.monthlyBudget = monthlyBudget
        this.electricityRate = electricityRate
        _devices.value = initialDevices
    }
    
    /**
     * Main simulation loop - runs every 2 seconds to update live data
     */
    private fun startSimulation() {
        scope.launch {
            while (true) {
                delay(2000) // Update every 2 seconds
                updateLiveData()
                checkForAnomalies()
                updateDeviceUsage()
                delay(2000 * (1.0 / simulationSpeed).toLong())
            }
        }
    }
    
    /**
     * Calculate and update live power consumption
     */
    private fun updateLiveData() {
        val activeDevices = _devices.value.filter { it.isOn }
        val deviceLoad = activeDevices.sumOf { it.wattage }
        
        // Add realistic fluctuations (±5%)
        val fluctuation = Random.nextDouble(-0.05, 0.05)
        val totalLoad = (baseLoad + deviceLoad) * (1 + fluctuation)
        
        // Add small voltage and frequency variations for realism
        val voltage = 230.0 + Random.nextDouble(-2.0, 2.0)
        val frequency = 50.0 + Random.nextDouble(-0.1, 0.1)
        val powerFactor = 0.95 + Random.nextDouble(-0.05, 0.05)
        
        _liveData.value = LiveData(
            currentUsage = totalLoad,
            voltage = voltage,
            frequency = frequency,
            powerFactor = powerFactor,
            timestamp = System.currentTimeMillis()
        )
        
        // Update monthly consumption (simplified: assume constant usage)
        val hoursElapsed = 2.0 / 3600.0 // 2 seconds in hours
        currentMonthUsage += (totalLoad / 1000.0) * hoursElapsed
    }
    
    /**
     * Intelligent anomaly detection
     */
    private fun checkForAnomalies() {
        val currentTime = System.currentTimeMillis()
        val currentUsage = _liveData.value.currentUsage
        val currentAnomalies = _anomalies.value.toMutableList()
        
        // High usage detection (>3000W for >10 minutes)
        if (currentUsage > 3000.0) {
            if (highUsageStartTime == 0L) {
                highUsageStartTime = currentTime
            } else if (currentTime - highUsageStartTime > 600_000) { // 10 minutes
                val anomaly = "High overall load detected: ${currentUsage.toInt()}W for ${(currentTime - highUsageStartTime) / 60000} minutes"
                if (!currentAnomalies.contains(anomaly)) {
                    currentAnomalies.add(anomaly)
                }
            }
        } else {
            highUsageStartTime = 0L
        }
        
        // Budget warning (80% of monthly budget reached)
        val budgetUsed = (currentMonthUsage * electricityRate) / monthlyBudget
        if (budgetUsed > 0.8 && currentTime - lastAnomalyCheck > 3600_000) { // Check once per hour
            val anomaly = "Budget Alert: ${(budgetUsed * 100).toInt()}% of monthly budget used"
            if (!currentAnomalies.any { it.contains("Budget Alert") }) {
                currentAnomalies.add(anomaly)
            }
            lastAnomalyCheck = currentTime
        }
        
        // Unusual device patterns
        val activeDevices = _devices.value.filter { it.isOn }
        val acDevices = activeDevices.filter { it.name.contains("AC", ignoreCase = true) }
        if (acDevices.isNotEmpty() && isNightTime()) {
            val anomaly = "AC running during night hours - consider scheduling"
            if (!currentAnomalies.contains(anomaly)) {
                currentAnomalies.add(anomaly)
            }
        }
        
        // Keep only recent anomalies (last 24 hours)
        val recentAnomalies = currentAnomalies.takeLast(5)
        _anomalies.value = recentAnomalies
    }
    
    /**
     * Update individual device usage statistics
     */
    private fun updateDeviceUsage() {
        val updatedDevices = _devices.value.map { device ->
            if (device.isOn) {
                val hoursElapsed = 2.0 / 3600.0 // 2 seconds in hours
                val energyConsumed = (device.wattage / 1000.0) * hoursElapsed
                
                device.copy(
                    dailyUsage = device.dailyUsage + energyConsumed,
                    monthlyUsage = device.monthlyUsage + energyConsumed,
                    lastUsed = System.currentTimeMillis()
                )
            } else {
                device
            }
        }
        _devices.value = updatedDevices
    }
    
    /**
     * Toggle a device on/off with realistic delay
     */
    suspend fun toggleDevice(deviceId: String, isOn: Boolean): Boolean {
        delay(100) // Simulate network/hardware delay
        
        val updatedDevices = _devices.value.map { device ->
            if (device.id == deviceId) {
                device.copy(
                    isOn = isOn,
                    lastUsed = if (isOn) System.currentTimeMillis() else device.lastUsed
                )
            } else {
                device
            }
        }
        
        _devices.value = updatedDevices
        return true
    }
    
    /**
     * Add a new device to the simulation
     */
    fun addDevice(device: Device) {
        val currentDevices = _devices.value.toMutableList()
        currentDevices.add(device)
        _devices.value = currentDevices
    }
    
    /**
     * Remove a device from the simulation
     */
    fun removeDevice(deviceId: String) {
        val updatedDevices = _devices.value.filter { it.id != deviceId }
        _devices.value = updatedDevices
    }
    
    /**
     * Schedule a device to turn on/off at specific times
     */
    suspend fun scheduleDevice(deviceId: String, startTime: String, endTime: String): Boolean {
        delay(200) // Simulate processing
        
        val updatedDevices = _devices.value.map { device ->
            if (device.id == deviceId) {
                val schedules = device.schedules.toMutableList()
                schedules.add("$startTime - $endTime")
                device.copy(schedules = schedules)
            } else {
                device
            }
        }
        
        _devices.value = updatedDevices
        return true
    }
    
    /**
     * Get optimization suggestions for a device
     */
    fun getOptimizationSuggestion(deviceId: String): String {
        val device = _devices.value.find { it.id == deviceId }
        return when {
            device == null -> "Device not found"
            device.name.contains("AC", ignoreCase = true) -> 
                "Your AC is consuming ${device.wattage}W. Setting temperature 1°C higher could save ₹${(device.wattage * 0.1 * electricityRate / 1000 * 24 * 30).toInt()}/month"
            device.name.contains("Refrigerator", ignoreCase = true) -> 
                "Your refrigerator runs efficiently. Ensure door seals are tight to maintain optimal performance"
            device.wattage > 2000 -> 
                "This high-power device consumes ${device.wattage}W. Consider using during off-peak hours to save on electricity costs"
            else -> 
                "Device is operating normally. Consider scheduling to optimize usage patterns"
        }
    }
    
    /**
     * Get current bill estimation
     */
    fun getCurrentBillEstimate(): Double {
        return currentMonthUsage * electricityRate
    }
    
    /**
     * Get monthly usage in kWh
     */
    fun getMonthlyUsage(): Double {
        return currentMonthUsage
    }
    
    /**
     * Set simulation speed (for testing/demo purposes)
     */
    fun setSimulationSpeed(speed: Double) {
        simulationSpeed = speed.coerceIn(0.1, 10.0)
    }
    
    /**
     * Reset monthly usage (for new billing cycle)
     */
    fun resetMonthlyUsage() {
        currentMonthUsage = 0.0
        val resetDevices = _devices.value.map { it.copy(monthlyUsage = 0.0) }
        _devices.value = resetDevices
    }
    
    /**
     * Check if it's currently night time (for anomaly detection)
     */
    private fun isNightTime(): Boolean {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        return hour < 6 || hour > 22
    }
    
    /**
     * Get default devices for initial setup
     */
    private fun getDefaultDevices(): List<Device> {
        return listOf(
            Device(
                id = "ac_living_room",
                name = "Living Room AC",
                type = "Air Conditioner",
                wattage = 1500.0,
                isOn = false,
                room = "Living Room",
                icon = "❄️"
            ),
            Device(
                id = "refrigerator_kitchen",
                name = "Kitchen Refrigerator",
                type = "Refrigerator",
                wattage = 200.0,
                isOn = true,
                room = "Kitchen",
                icon = "🧊"
            ),
            Device(
                id = "tv_living_room",
                name = "Living Room TV",
                type = "Television",
                wattage = 150.0,
                isOn = false,
                room = "Living Room",
                icon = "📺"
            ),
            Device(
                id = "water_heater",
                name = "Water Heater",
                type = "Water Heater",
                wattage = 2000.0,
                isOn = false,
                room = "Bathroom",
                icon = "🚿"
            )
        )
    }
    
    /**
     * Clean up resources
     */
    fun cleanup() {
        scope.cancel()
    }
}