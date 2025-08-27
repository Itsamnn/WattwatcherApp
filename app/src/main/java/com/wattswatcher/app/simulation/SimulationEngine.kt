package com.wattswatcher.app.simulation

import com.wattswatcher.app.data.model.Device
import com.wattswatcher.app.data.model.DevicePriority
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
    
    // New simulation features
    private val _detectedAppliance = MutableStateFlow<DetectedAppliance?>(null)
    val detectedAppliance: StateFlow<DetectedAppliance?> = _detectedAppliance.asStateFlow()
    
    private val _gridStatus = MutableStateFlow(GridStatus.STABLE)
    val gridStatus: StateFlow<GridStatus> = _gridStatus.asStateFlow()
    
    private val _gsmMessages = MutableStateFlow<List<GsmMessage>>(emptyList())
    val gsmMessages: StateFlow<List<GsmMessage>> = _gsmMessages.asStateFlow()
    
    // Simulation parameters
    private var baseLoad = 200.0 // Base household load in watts
    private var simulationSpeed = 10.0 // 10x speed for demo - bills update faster
    private var monthlyBudget = 1500.0 // User's monthly budget
    private var currentMonthUsage = 0.0 // kWh consumed this month
    private var electricityRate = 4.5 // Rate per kWh
    private var prepaidBalance = 450.0 // Prepaid balance in rupees
    private var isPrepaidMode = false // Billing mode
    
    // Anomaly detection state
    private var highUsageStartTime = 0L
    private var lastAnomalyCheck = 0L
    private var lastApplianceDetection = 0L
    private var lastGsmMessageTime = 0L
    
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
                simulateApplianceDetection()
                simulateGsmCommunication()
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
     * Get billing information including prepaid balance and mode
     */
    fun getBillingInfo(): BillingInfo {
        return BillingInfo(
            currentBill = getCurrentBillEstimate(),
            monthlyUsage = currentMonthUsage,
            prepaidBalance = prepaidBalance,
            isPrepaidMode = isPrepaidMode,
            dueDate = getNextDueDate()
        )
    }
    
    /**
     * Set billing mode (prepaid or postpaid)
     */
    fun setBillingMode(isPrepaid: Boolean) {
        isPrepaidMode = isPrepaid
        
        // Simulate GSM message for billing mode change
        val message = if (isPrepaid) {
            GsmMessage(
                sender = "UTILITY",
                command = "CMD:BILLING:PREPAID",
                description = "Your account has been switched to PREPAID mode. Current balance: ₹${String.format("%.2f", prepaidBalance)}",
                timestamp = System.currentTimeMillis(),
                isIncoming = true
            )
        } else {
            GsmMessage(
                sender = "UTILITY",
                command = "CMD:BILLING:POSTPAID",
                description = "Your account has been switched to POSTPAID mode. Bill due on ${getNextDueDate()}",
                timestamp = System.currentTimeMillis(),
                isIncoming = true
            )
        }
        
        val currentMessages = _gsmMessages.value.toMutableList()
        currentMessages.add(message)
        _gsmMessages.value = currentMessages.takeLast(10)
    }
    
    /**
     * Get next due date for postpaid billing
     */
    private fun getNextDueDate(): String {
        val calendar = java.util.Calendar.getInstance()
        calendar.add(java.util.Calendar.DAY_OF_MONTH, 15) // Due in 15 days
        return java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
            .format(calendar.time)
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
     * Simulate appliance detection (NILM-lite)
     */
    private fun simulateApplianceDetection() {
        val currentTime = System.currentTimeMillis()
        
        // Simulate detection every 20-30 seconds
        if (currentTime - lastApplianceDetection > Random.nextLong(20000, 30000)) {
            lastApplianceDetection = currentTime
            
            // 30% chance of detecting a new appliance
            if (Random.nextDouble() < 0.3) {
                val powerDelta = Random.nextDouble(0.8, 2.5) * 1000 // 800W to 2500W
                val possibleAppliances = listOf(
                    "AC" to 1500.0,
                    "Water Heater" to 2000.0,
                    "Iron" to 1000.0,
                    "Microwave" to 1200.0,
                    "Washing Machine" to 500.0
                )
                
                // Find closest match based on power signature
                val (applianceName, _) = possibleAppliances.minByOrNull { 
                    Math.abs(it.second - powerDelta) 
                } ?: ("Unknown Appliance" to powerDelta)
                
                _detectedAppliance.value = DetectedAppliance(
                    name = applianceName,
                    powerDelta = powerDelta,
                    timestamp = currentTime,
                    confidence = Random.nextDouble(0.7, 0.95)
                )
            } else {
                // Clear previous detection
                _detectedAppliance.value = null
            }
        }
    }
    
    /**
     * Simulate GSM communication with utility
     */
    private fun simulateGsmCommunication() {
        val currentTime = System.currentTimeMillis()
        
        // Simulate GSM messages every 45-60 seconds
        if (currentTime - lastGsmMessageTime > Random.nextLong(45000, 60000)) {
            lastGsmMessageTime = currentTime
            
            // 20% chance of receiving a message
            if (Random.nextDouble() < 0.2) {
                val messages = _gsmMessages.value.toMutableList()
                val messageTypes = listOf(
                    "CMD:LOAD_SHED:PREPARE" to "Preparing for load shedding in 5 minutes",
                    "CMD:LOAD_SHED:START" to "Starting load shedding protocol",
                    "CMD:LOAD_SHED:END" to "Ending load shedding, restoring power",
                    "CMD:TARIFF:UPDATE" to "Tariff updated to peak hours (₹5.2/kWh)",
                    "CMD:TARIFF:NORMAL" to "Tariff returned to normal (₹4.5/kWh)",
                    "CMD:BALANCE:LOW" to "Prepaid balance low, please recharge"
                )
                
                val (command, description) = messageTypes.random()
                
                val newMessage = GsmMessage(
                    sender = "UTILITY",
                    command = command,
                    description = description,
                    timestamp = currentTime,
                    isIncoming = true
                )
                
                messages.add(newMessage)
                
                // Process command
                when {
                    command.contains("LOAD_SHED:START") -> {
                        _gridStatus.value = GridStatus.LOAD_SHEDDING
                        handleLoadShedding()
                    }
                    command.contains("LOAD_SHED:END") -> {
                        _gridStatus.value = GridStatus.STABLE
                    }
                }
                
                // Auto-respond after a short delay
                scope.launch {
                    delay(2000)
                    val response = GsmMessage(
                        sender = "DEVICE",
                        command = "ACK:${command}",
                        description = "Acknowledged: ${description}",
                        timestamp = System.currentTimeMillis(),
                        isIncoming = false
                    )
                    val updatedMessages = _gsmMessages.value.toMutableList()
                    updatedMessages.add(response)
                    _gsmMessages.value = updatedMessages
                }
                
                // Keep only last 10 messages
                _gsmMessages.value = messages.takeLast(10)
            }
        }
    }
    
    /**
     * Handle load shedding based on device priorities
     */
    private fun handleLoadShedding() {
        val currentDevices = _devices.value
        val updatedDevices = currentDevices.map { device ->
            // Turn off non-essential devices
            if (device.priority == DevicePriority.LOW && device.isOn) {
                device.copy(isOn = false)
            } else {
                device
            }
        }
        _devices.value = updatedDevices
        
        // Add anomaly alert
        val anomalies = _anomalies.value.toMutableList()
        anomalies.add("Load shedding activated. Non-essential devices turned off.")
        _anomalies.value = anomalies.takeLast(5)
    }
    
    /**
     * Manually trigger load shedding (for demo purposes)
     */
    fun triggerLoadShedding() {
        _gridStatus.value = GridStatus.LOAD_SHEDDING
        handleLoadShedding()
        
        // Simulate GSM message
        val messages = _gsmMessages.value.toMutableList()
        val incomingMessage = GsmMessage(
            sender = "UTILITY",
            command = "CMD:LOAD_SHED:START",
            description = "Starting load shedding protocol",
            timestamp = System.currentTimeMillis(),
            isIncoming = true
        )
        messages.add(incomingMessage)
        _gsmMessages.value = messages.takeLast(10)
    }
    
    /**
     * End load shedding (for demo purposes)
     */
    fun endLoadShedding() {
        _gridStatus.value = GridStatus.STABLE
        
        // Simulate GSM message
        val messages = _gsmMessages.value.toMutableList()
        val incomingMessage = GsmMessage(
            sender = "UTILITY",
            command = "CMD:LOAD_SHED:END",
            description = "Ending load shedding, restoring power",
            timestamp = System.currentTimeMillis(),
            isIncoming = true
        )
        messages.add(incomingMessage)
        _gsmMessages.value = messages.takeLast(10)
    }
    
    /**
     * Label a detected appliance and add it to devices
     */
    fun labelDetectedAppliance(name: String, type: String, room: String): Device? {
        val detection = _detectedAppliance.value ?: return null
        
        val newDevice = Device(
            id = "${type.lowercase()}_${room.lowercase().replace(" ", "_")}_${System.currentTimeMillis()}",
            name = name,
            type = type,
            wattage = detection.powerDelta,
            isOn = true,
            room = room,
            icon = getIconForDeviceType(type),
            priority = DevicePriority.MEDIUM
        )
        
        // Add to devices
        val updatedDevices = _devices.value.toMutableList()
        updatedDevices.add(newDevice)
        _devices.value = updatedDevices
        
        // Clear detection
        _detectedAppliance.value = null
        
        return newDevice
    }
    
    /**
     * Update device priority
     */
    fun updateDevicePriority(deviceId: String, priority: DevicePriority) {
        val updatedDevices = _devices.value.map { device ->
            if (device.id == deviceId) {
                device.copy(priority = priority)
            } else {
                device
            }
        }
        _devices.value = updatedDevices
    }
    
    /**
     * Get icon for device type
     */
    private fun getIconForDeviceType(type: String): String {
        return when {
            type.contains("AC", ignoreCase = true) -> "❄️"
            type.contains("Heat", ignoreCase = true) -> "🔥"
            type.contains("Water", ignoreCase = true) -> "💧"
            type.contains("Fridge", ignoreCase = true) -> "🧊"
            type.contains("TV", ignoreCase = true) -> "📺"
            type.contains("Light", ignoreCase = true) -> "💡"
            type.contains("Fan", ignoreCase = true) -> "🌀"
            type.contains("Iron", ignoreCase = true) -> "👔"
            type.contains("Wash", ignoreCase = true) -> "👕"
            else -> "⚡"
        }
    }
    
    // Removed duplicate getBillingInfo method
    
    /**
     * Add prepaid balance
     */
    fun addPrepaidBalance(amount: Double) {
        prepaidBalance += amount
        
        // Simulate GSM confirmation message
        val message = GsmMessage(
            sender = "UTILITY",
            command = "CMD:BILLING:RECHARGE",
            description = "Your account has been recharged with ₹${String.format("%.2f", amount)}. New balance: ₹${String.format("%.2f", prepaidBalance)}",
            timestamp = System.currentTimeMillis(),
            isIncoming = true
        )
        
        val currentMessages = _gsmMessages.value.toMutableList()
        currentMessages.add(message)
        _gsmMessages.value = currentMessages.takeLast(10)
    }
    
    /**
     * Toggle prepaid/postpaid mode
     */
    fun togglePrepaidMode(isPrepaid: Boolean) {
        isPrepaidMode = isPrepaid
    }
    
    /**
     * Get grid status description
     */
    fun getGridStatusDescription(): String {
        return when (_gridStatus.value) {
            GridStatus.STABLE -> "Grid Stable"
            GridStatus.LOAD_SHEDDING -> "Load Shedding Active"
            GridStatus.PEAK_HOURS -> "Peak Hours - High Tariff"
            GridStatus.MAINTENANCE -> "Grid Maintenance"
        }
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
                priority = DevicePriority.LOW,
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
                icon = "🧊",
                priority = DevicePriority.HIGH
            ),
            Device(
                id = "tv_living_room",
                name = "Living Room TV",
                type = "Television",
                wattage = 150.0,
                isOn = false,
                room = "Living Room",
                icon = "📺",
                priority = DevicePriority.MEDIUM
            ),
            Device(
                id = "water_heater",
                name = "Water Heater",
                type = "Water Heater",
                wattage = 2000.0,
                isOn = false,
                room = "Bathroom",
                icon = "🚿",
                priority = DevicePriority.LOW
            )
        )
    }
    
    /**
     * Reset monthly bill after successful payment
     */
    fun resetMonthlyBill() {
        currentMonthUsage = 0.0
        
        // Reset device monthly usage as well
        val resetDevices = _devices.value.map { device ->
            device.copy(monthlyUsage = 0.0)
        }
        _devices.value = resetDevices
        
        // Simulate GSM message for payment confirmation
        val message = GsmMessage(
            sender = "UTILITY",
            command = "CMD:PAYMENT:SUCCESS",
            description = "Payment successful! Your bill has been reset. New billing cycle started.",
            timestamp = System.currentTimeMillis(),
            isIncoming = true
        )
        
        val currentMessages = _gsmMessages.value.toMutableList()
        currentMessages.add(0, message) // Add to beginning
        if (currentMessages.size > 10) {
            currentMessages.removeAt(currentMessages.size - 1) // Keep only last 10
        }
        _gsmMessages.value = currentMessages
    }
    
    /**
     * Clean up resources
     */
    fun cleanup() {
        scope.cancel()
    }
}

/**
 * Data class for detected appliance
 */
data class DetectedAppliance(
    val name: String,
    val powerDelta: Double,
    val timestamp: Long,
    val confidence: Double
)

/**
 * Data class for GSM message
 */
data class GsmMessage(
    val sender: String,
    val command: String,
    val description: String,
    val timestamp: Long,
    val isIncoming: Boolean
)

/**
 * Data class for billing information
 */
data class BillingInfo(
    val currentBill: Double,
    val monthlyUsage: Double,
    val prepaidBalance: Double,
    val isPrepaidMode: Boolean,
    val dueDate: String
)

/**
 * Enum for grid status
 */
enum class GridStatus {
    STABLE,
    LOAD_SHEDDING,
    PEAK_HOURS,
    MAINTENANCE
}