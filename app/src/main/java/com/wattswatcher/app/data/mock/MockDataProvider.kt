package com.wattswatcher.app.data.mock

import com.wattswatcher.app.data.api.*
import com.wattswatcher.app.data.model.*
import com.wattswatcher.app.simulation.SimulationEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * Enhanced MockDataProvider that works with SimulationEngine
 * Provides realistic, dynamic data for the WattsWatcher app
 */
object MockDataProvider {
    
    // Simulation engine instance
    private val simulationEngine = SimulationEngine()
    
    // Initialize with realistic data
    init {
        simulationEngine.initializeSimulation(
            monthlyBudget = 1500.0,
            electricityRate = 4.5,
            initialDevices = getInitialDevices()
        )
    }
    
    /**
     * Get live data stream from simulation engine
     */
    fun getLiveDataStream(): Flow<LiveData> = simulationEngine.liveData
    
    /**
     * Get devices stream from simulation engine
     */
    fun getDevicesStream(): Flow<List<Device>> = simulationEngine.devices
    
    /**
     * Get mock dashboard data with live updates
     */
    fun getMockDashboardData(): DashboardResponse {
        val currentLiveData = simulationEngine.liveData.value
        val currentDevices = simulationEngine.devices.value
        val activeDevices = currentDevices.filter { it.isOn }
        
        return DashboardResponse(
            liveData = currentLiveData,
            billSummary = com.wattswatcher.app.data.model.BillSummary(
                amount = simulationEngine.getCurrentBillEstimate(),
                unitsConsumed = simulationEngine.getMonthlyUsage(),
                rate = 4.5,
                dueDate = getNextBillDueDate()
            ),
            activeDevices = activeDevices,
            anomalies = simulationEngine.anomalies.value
        )
    }
    
    /**
     * Get mock devices with live data
     */
    fun getMockDevices(): List<Device> {
        return simulationEngine.devices.value
    }
    
    /**
     * Toggle device through simulation engine
     */
    suspend fun toggleDevice(deviceId: String, isOn: Boolean): Boolean {
        return simulationEngine.toggleDevice(deviceId, isOn)
    }
    
    /**
     * Toggle all devices
     */
    suspend fun toggleAllDevices(isOn: Boolean): Boolean {
        val devices = simulationEngine.devices.value
        devices.forEach { device ->
            simulationEngine.toggleDevice(device.id, isOn)
        }
        return true
    }
    
    /**
     * Add new device to simulation
     */
    fun addDevice(device: Device) {
        simulationEngine.addDevice(device)
    }
    
    /**
     * Schedule device through simulation engine
     */
    suspend fun scheduleDevice(deviceId: String, startTime: String, endTime: String): Boolean {
        return simulationEngine.scheduleDevice(deviceId, startTime, endTime)
    }
    
    /**
     * Get device schedules
     */
    fun getDeviceSchedules(deviceId: String): List<String> {
        val device = simulationEngine.devices.value.find { it.id == deviceId }
        return device?.schedules ?: emptyList()
    }
    
    /**
     * Get optimization suggestion for device
     */
    fun getOptimizationSuggestion(deviceId: String): String {
        return simulationEngine.getOptimizationSuggestion(deviceId)
    }
    
    /**
     * Get analytics data with dynamic calculations
     */
    fun getMockAnalyticsData(period: String): AnalyticsResponse {
        val devices = simulationEngine.devices.value
        val currentUsage = simulationEngine.getMonthlyUsage()
        
        return AnalyticsResponse(
            period = period,
            totalConsumption = when (period) {
                "day" -> devices.sumOf { it.dailyUsage }
                "week" -> devices.sumOf { it.dailyUsage * 7 }
                "month" -> currentUsage
                "year" -> currentUsage * 12
                else -> currentUsage
            },
            averageDaily = devices.sumOf { it.dailyUsage },
            peakUsage = devices.maxOfOrNull { it.wattage } ?: 0.0,
            costAnalysis = CostAnalysis(
                totalCost = simulationEngine.getCurrentBillEstimate(),
                averageDailyCost = simulationEngine.getCurrentBillEstimate() / 30,
                projectedMonthlyCost = simulationEngine.getCurrentBillEstimate() * 1.1,
                savingsOpportunity = calculateSavingsOpportunity(devices)
            ),
            deviceBreakdown = devices.map { device ->
                DeviceUsage(
                    deviceId = device.id,
                    deviceName = device.name,
                    consumption = device.monthlyUsage,
                    cost = device.getMonthlyEstimatedCost(),
                    percentage = if (currentUsage > 0) (device.monthlyUsage / currentUsage * 100) else 0.0
                )
            },
            hourlyData = generateHourlyData(period),
            trends = generateTrends(devices)
        )
    }
    
    /**
     * Get billing data with live calculations
     */
    fun getBillingData(): BillingResponse {
        val currentBill = simulationEngine.getCurrentBillEstimate()
        val monthlyUsage = simulationEngine.getMonthlyUsage()
        
        return BillingResponse(
            currentBill = com.wattswatcher.app.data.model.BillSummary(
                amount = currentBill,
                unitsConsumed = monthlyUsage,
                rate = 4.5,
                dueDate = getNextBillDueDate()
            ),
            tariffStructure = getTariffStructure(),
            paymentHistory = getPaymentHistory(),
            usageHistory = getUsageHistory(monthlyUsage)
        )
    }
    
    /**
     * Process payment simulation
     */
    fun processPayment(amount: Double, method: String): Boolean {
        // Simulate payment processing with 95% success rate
        return Random.nextDouble() < 0.95
    }
    
    /**
     * Get initial devices for simulation
     */
    private fun getInitialDevices(): List<Device> {
        return listOf(
            Device(
                id = "ac_living_room",
                name = "Living Room AC",
                type = "Air Conditioner",
                wattage = 1500.0,
                isOn = false,
                room = "Living Room",
                icon = "❄️",
                priority = DevicePriority.MEDIUM,
                efficiency = 0.85
            ),
            Device(
                id = "refrigerator_kitchen",
                name = "Kitchen Refrigerator",
                type = "Refrigerator",
                wattage = 200.0,
                isOn = true,
                room = "Kitchen",
                icon = "🧊",
                priority = DevicePriority.HIGH,
                efficiency = 0.92
            ),
            Device(
                id = "tv_living_room",
                name = "Living Room TV",
                type = "Television",
                wattage = 150.0,
                isOn = false,
                room = "Living Room",
                icon = "📺",
                priority = DevicePriority.MEDIUM,
                efficiency = 0.88
            ),
            Device(
                id = "water_heater",
                name = "Water Heater",
                type = "Water Heater",
                wattage = 2000.0,
                isOn = false,
                room = "Bathroom",
                icon = "🚿",
                priority = DevicePriority.MEDIUM,
                efficiency = 0.75
            ),
            Device(
                id = "washing_machine",
                name = "Washing Machine",
                type = "Washing Machine",
                wattage = 800.0,
                isOn = false,
                room = "Utility",
                icon = "👕",
                priority = DevicePriority.LOW,
                efficiency = 0.80
            ),
            Device(
                id = "microwave_kitchen",
                name = "Kitchen Microwave",
                type = "Microwave",
                wattage = 900.0,
                isOn = false,
                room = "Kitchen",
                icon = "🍽️",
                priority = DevicePriority.LOW,
                efficiency = 0.85
            ),
            Device(
                id = "router_office",
                name = "WiFi Router",
                type = "Router",
                wattage = 25.0,
                isOn = true,
                room = "Office",
                icon = "📡",
                priority = DevicePriority.HIGH,
                efficiency = 0.95
            ),
            Device(
                id = "lights_living_room",
                name = "Living Room Lights",
                type = "Lights",
                wattage = 60.0,
                isOn = true,
                room = "Living Room",
                icon = "💡",
                priority = DevicePriority.MEDIUM,
                efficiency = 0.90
            )
        )
    }
    
    /**
     * Calculate potential savings
     */
    private fun calculateSavingsOpportunity(devices: List<Device>): Double {
        return devices.sumOf { device ->
            val inefficiencyFactor = 1.0 - device.efficiency
            device.getMonthlyEstimatedCost() * inefficiencyFactor * 0.3 // 30% of inefficiency can be saved
        }
    }
    
    /**
     * Generate hourly data for charts
     */
    private fun generateHourlyData(period: String): List<HourlyUsage> {
        val hours = when (period) {
            "day" -> 24
            "week" -> 24 * 7
            "month" -> 24 * 30
            else -> 24
        }
        
        return (0 until hours).map { hour ->
            val baseUsage = when (hour % 24) {
                in 0..5 -> 0.5 // Night time - low usage
                in 6..8 -> 1.2 // Morning peak
                in 9..17 -> 0.8 // Day time
                in 18..22 -> 1.5 // Evening peak
                else -> 0.7 // Late evening
            }
            
            HourlyUsage(
                hour = hour,
                usage = baseUsage + Random.nextDouble(-0.2, 0.2),
                cost = (baseUsage + Random.nextDouble(-0.2, 0.2)) * 4.5
            )
        }
    }
    
    /**
     * Generate usage trends
     */
    private fun generateTrends(devices: List<Device>): UsageTrends {
        val totalUsage = devices.sumOf { it.monthlyUsage }
        val lastMonthUsage = totalUsage * Random.nextDouble(0.8, 1.2)
        
        return UsageTrends(
            weekOverWeek = Random.nextDouble(-15.0, 15.0),
            monthOverMonth = ((totalUsage - lastMonthUsage) / lastMonthUsage * 100),
            yearOverYear = Random.nextDouble(-10.0, 20.0),
            efficiency = devices.map { it.efficiency }.average() * 100
        )
    }
    
    /**
     * Get next bill due date
     */
    private fun getNextBillDueDate(): String {
        val calendar = java.util.Calendar.getInstance()
        calendar.add(java.util.Calendar.DAY_OF_MONTH, 15)
        val format = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return format.format(calendar.time)
    }
    
    /**
     * Get tariff structure
     */
    private fun getTariffStructure(): List<TariffSlab> {
        return listOf(
            TariffSlab(0.0, 100.0, 3.5, "Domestic - First 100 units"),
            TariffSlab(100.0, 200.0, 4.0, "Domestic - Next 100 units"),
            TariffSlab(200.0, 300.0, 4.5, "Domestic - Next 100 units"),
            TariffSlab(300.0, Double.MAX_VALUE, 5.0, "Domestic - Above 300 units")
        )
    }
    
    /**
     * Get payment history
     */
    private fun getPaymentHistory(): List<PaymentRecord> {
        return listOf(
            PaymentRecord("Dec 2024", 1245.50, "Paid", "2024-12-15"),
            PaymentRecord("Nov 2024", 1180.25, "Paid", "2024-11-15"),
            PaymentRecord("Oct 2024", 1320.75, "Paid", "2024-10-15"),
            PaymentRecord("Sep 2024", 1150.00, "Paid", "2024-09-15")
        )
    }
    
    /**
     * Get usage history
     */
    private fun getUsageHistory(currentUsage: Double): List<UsageRecord> {
        return listOf(
            UsageRecord("Jan 2025", currentUsage, simulationEngine.getCurrentBillEstimate()),
            UsageRecord("Dec 2024", 285.5, 1245.50),
            UsageRecord("Nov 2024", 270.2, 1180.25),
            UsageRecord("Oct 2024", 295.8, 1320.75),
            UsageRecord("Sep 2024", 260.1, 1150.00)
        )
    }
    
    /**
     * Get simulation engine instance for direct access
     */
    fun getSimulationEngine(): SimulationEngine = simulationEngine
}