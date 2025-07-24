package com.wattswatcher.app.data.repository

import com.wattswatcher.app.data.api.*
import com.wattswatcher.app.data.model.*
import com.wattswatcher.app.data.mock.MockDataProvider
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.delay

/**
 * Repository that integrates with the SimulationEngine for realistic data
 */
class WattsWatcherRepository {
    
    // Dashboard data with live updates from simulation
    fun getDashboardData(): Flow<Result<DashboardResponse>> = flow {
        try {
            delay(300) // Simulate network delay
            val dashboardData = MockDataProvider.getMockDashboardData()
            emit(Result.success(dashboardData))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    // Live data stream - real-time updates from simulation engine
    fun getLiveDataStream(): Flow<LiveData> = MockDataProvider.getLiveDataStream()
    
    // Device management with real-time updates from simulation
    fun getDevices(): Flow<Result<List<Device>>> = flow {
        try {
            MockDataProvider.getDevicesStream().collect { devices ->
                emit(Result.success(devices))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    suspend fun toggleDevice(deviceId: String, isOn: Boolean): Result<Device> {
        return try {
            val success = MockDataProvider.toggleDevice(deviceId, isOn)
            if (success) {
                // Find and return the updated device
                val updatedDevice = MockDataProvider.getMockDevices().find { it.id == deviceId }
                if (updatedDevice != null) {
                    Result.success(updatedDevice)
                } else {
                    Result.failure(Exception("Device not found"))
                }
            } else {
                Result.failure(Exception("Failed to toggle device"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun toggleAllDevices(isOn: Boolean): Result<Boolean> {
        return try {
            val success = MockDataProvider.toggleAllDevices(isOn)
            Result.success(success)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Add new device to simulation
    fun addDevice(device: Device) {
        MockDataProvider.addDevice(device)
    }
    
    // Analytics with dynamic data from simulation
    fun getAnalytics(period: String): Flow<Result<AnalyticsResponse>> = flow {
        try {
            delay(300)
            val analyticsData = MockDataProvider.getMockAnalyticsData(period)
            emit(Result.success(analyticsData))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    // Billing with working payment simulation
    fun getBillingData(): Flow<Result<BillingResponse>> = flow {
        try {
            delay(250)
            val billingData = MockDataProvider.getBillingData()
            emit(Result.success(billingData))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    suspend fun initiatePayment(amount: Double, method: String): Result<PaymentResponse> {
        return try {
            delay(1500) // Simulate payment processing
            val success = MockDataProvider.processPayment(amount, method)
            
            if (success) {
                Result.success(PaymentResponse(
                    success = true,
                    transactionId = "TXN${System.currentTimeMillis()}",
                    message = "Payment processed successfully"
                ))
            } else {
                Result.success(PaymentResponse(
                    success = false,
                    transactionId = null,
                    message = "Payment failed. Please try again."
                ))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Device scheduling through simulation engine
    suspend fun scheduleDevice(deviceId: String, startTime: String, endTime: String): Result<Boolean> {
        return try {
            val success = MockDataProvider.scheduleDevice(deviceId, startTime, endTime)
            Result.success(success)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getDeviceSchedules(deviceId: String): Result<List<String>> {
        return try {
            val schedules = MockDataProvider.getDeviceSchedules(deviceId)
            Result.success(schedules)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Get optimization suggestions from simulation engine
    fun getOptimizationSuggestion(deviceId: String): String {
        return MockDataProvider.getOptimizationSuggestion(deviceId)
    }
    
    // Get simulation engine for direct access if needed
    fun getSimulationEngine() = MockDataProvider.getSimulationEngine()
    
    // Get real-time bill updates
    fun getRealTimeBillUpdates(): Flow<Pair<Double, Double>> = flow {
        val simulationEngine = getSimulationEngine()
        combine(
            simulationEngine.liveData,
            simulationEngine.devices
        ) { _, _ ->
            val currentBill = simulationEngine.getCurrentBillEstimate()
            val currentUsage = simulationEngine.getMonthlyUsage()
            Pair(currentBill, currentUsage)
        }.collect { billData ->
            emit(billData)
        }
    }
}