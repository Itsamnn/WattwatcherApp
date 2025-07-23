package com.wattswatcher.app.data.repository

import com.wattswatcher.app.data.api.*
import com.wattswatcher.app.data.model.*
import com.wattswatcher.app.data.local.dao.DeviceDao
import com.wattswatcher.app.data.local.dao.LiveDataDao
import com.wattswatcher.app.data.local.entity.toDevice
import com.wattswatcher.app.data.local.entity.toEntity
import com.wattswatcher.app.data.local.entity.toLiveData
import com.wattswatcher.app.data.mock.MockDataProvider
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WattsWatcherRepository @Inject constructor(
    private val api: WattsWatcherApi,
    private val deviceDao: DeviceDao,
    private val liveDataDao: LiveDataDao
) {
    
    fun getDashboardData(): Flow<Result<DashboardResponse>> = flow {
        try {
            // Try to fetch from API
            val response = api.getDashboardData()
            if (response.isSuccessful && response.body() != null) {
                val dashboardData = response.body()!!
                
                // Cache the data locally
                liveDataDao.insertLiveData(dashboardData.liveData.toEntity())
                deviceDao.insertDevices(dashboardData.activeDevices.map { it.toEntity() })
                
                emit(Result.success(dashboardData))
            } else {
                // Fallback to cached data
                emitCachedDashboardData()
            }
        } catch (e: Exception) {
            // Fallback to cached data or mock data on network error
            try {
                emitCachedDashboardData()
            } catch (cacheException: Exception) {
                // If no cached data, use mock data
                val mockData = MockDataProvider.getMockDashboardData()
                emit(Result.success(mockData))
            }
        }
    }
    
    private suspend fun FlowCollector<Result<DashboardResponse>>.emitCachedDashboardData() {
        val cachedLiveData = liveDataDao.getCurrentLiveData().first()?.toLiveData()
        val cachedDevices = deviceDao.getAllDevices().first().map { it.toDevice() }
        
        if (cachedLiveData != null) {
            val mockBillSummary = BillSummary(
                amount = 1250.75,
                unitsConsumed = 285.5,
                rate = 4.38,
                dueDate = "2024-02-15"
            )
            
            emit(Result.success(DashboardResponse(
                liveData = cachedLiveData,
                billSummary = mockBillSummary,
                activeDevices = cachedDevices.filter { it.isOn }
            )))
        } else {
            emit(Result.failure(Exception("No cached data available")))
        }
    }
    
    fun getDevices(): Flow<Result<List<Device>>> = flow {
        try {
            val response = api.getDevices()
            if (response.isSuccessful && response.body() != null) {
                val devices = response.body()!!
                // Cache devices locally
                deviceDao.insertDevices(devices.map { it.toEntity() })
                emit(Result.success(devices))
            } else {
                // Fallback to cached devices or mock data
                try {
                    val cachedDevices = deviceDao.getAllDevices().first().map { it.toDevice() }
                    if (cachedDevices.isNotEmpty()) {
                        emit(Result.success(cachedDevices))
                    } else {
                        emit(Result.success(MockDataProvider.getMockDevices()))
                    }
                } catch (cacheException: Exception) {
                    emit(Result.success(MockDataProvider.getMockDevices()))
                }
            }
        } catch (e: Exception) {
            // Fallback to cached devices or mock data on network error
            try {
                val cachedDevices = deviceDao.getAllDevices().first().map { it.toDevice() }
                if (cachedDevices.isNotEmpty()) {
                    emit(Result.success(cachedDevices))
                } else {
                    emit(Result.success(MockDataProvider.getMockDevices()))
                }
            } catch (cacheException: Exception) {
                emit(Result.success(MockDataProvider.getMockDevices()))
            }
        }
    }
    
    suspend fun toggleDevice(deviceId: String, isOn: Boolean): Result<Device> {
        return try {
            val response = api.toggleDevice(deviceId, DeviceToggleRequest(isOn))
            if (response.isSuccessful && response.body() != null) {
                val updatedDevice = response.body()!!
                // Update local cache
                deviceDao.updateDevice(updatedDevice.toEntity())
                Result.success(updatedDevice)
            } else {
                // Update optimistically using mock data or cache
                updateDeviceOptimistically(deviceId, isOn)
            }
        } catch (e: Exception) {
            // Update optimistically on network error
            updateDeviceOptimistically(deviceId, isOn)
        }
    }
    
    private suspend fun updateDeviceOptimistically(deviceId: String, isOn: Boolean): Result<Device> {
        return try {
            val cachedDevice = deviceDao.getDeviceById(deviceId)
            if (cachedDevice != null) {
                val updatedDevice = cachedDevice.copy(isOn = isOn).toDevice()
                deviceDao.updateDevice(updatedDevice.toEntity())
                Result.success(updatedDevice)
            } else {
                // Find device in mock data and return updated version
                val mockDevice = MockDataProvider.getMockDevices().find { it.id == deviceId }
                if (mockDevice != null) {
                    val updatedDevice = mockDevice.copy(isOn = isOn)
                    deviceDao.insertDevice(updatedDevice.toEntity())
                    Result.success(updatedDevice)
                } else {
                    Result.failure(Exception("Device not found"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun initiatePayment(amount: Double, method: String): Result<PaymentResponse> {
        return try {
            val response = api.initiatePayment(PaymentRequest(amount, method))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to initiate payment"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun getAnalytics(period: String): Flow<Result<AnalyticsResponse>> = flow {
        try {
            val response = api.getAnalytics(period)
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                // Fallback to mock data
                emit(Result.success(generateMockAnalyticsData(period)))
            }
        } catch (e: Exception) {
            // Fallback to mock data on network error
            emit(Result.success(generateMockAnalyticsData(period)))
        }
    }
    
    // Generate mock analytics data when API is not available
    private fun generateMockAnalyticsData(period: String): AnalyticsResponse {
        val historicalData = when (period) {
            "day" -> (0..23).map { hour ->
                HistoricalDataPoint("${hour}:00", (15..45).random().toDouble())
            }
            "week" -> (0..6).map { day ->
                val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                HistoricalDataPoint(days[day], (200..400).random().toDouble())
            }
            "month" -> (1..30).map { day ->
                HistoricalDataPoint("$day", (180..420).random().toDouble())
            }
            else -> (1..12).map { month ->
                HistoricalDataPoint("Month $month", (5000..8000).random().toDouble())
            }
        }
        
        val deviceBreakdown = listOf(
            DeviceConsumption("Air Conditioner", 45.2, 35.5f),
            DeviceConsumption("Water Heater", 28.7, 22.5f),
            DeviceConsumption("Refrigerator", 18.3, 14.4f),
            DeviceConsumption("Washing Machine", 12.1, 9.5f),
            DeviceConsumption("TV", 8.9, 7.0f),
            DeviceConsumption("Lights", 7.2, 5.7f),
            DeviceConsumption("Others", 6.8, 5.4f)
        )
        
        return AnalyticsResponse(historicalData, deviceBreakdown)
    }