package com.wattswatcher.app.data.api

import com.wattswatcher.app.data.model.*
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.*

interface WattsWatcherApi {
    
    @GET("api/v1/dashboard")
    suspend fun getDashboardData(): Response<DashboardResponse>
    
    @GET("api/v1/devices")
    suspend fun getDevices(): Response<List<Device>>
    
    @POST("api/v1/devices/{id}/toggle")
    suspend fun toggleDevice(
        @Path("id") deviceId: String,
        @Body request: DeviceToggleRequest
    ): Response<Device>
    
    @POST("api/v1/payment/initiate")
    suspend fun initiatePayment(
        @Body request: PaymentRequest
    ): Response<PaymentResponse>
    
    @GET("api/v1/analytics")
    suspend fun getAnalytics(
        @Query("period") period: String
    ): Response<AnalyticsResponse>
    
    @GET("api/v1/billing")
    suspend fun getBillingData(): Response<BillingResponse>
}

// API Response Models
@Serializable
data class DashboardResponse(
    val liveData: LiveData,
    val billSummary: BillSummary,
    val activeDevices: List<Device>,
    val anomalies: List<String> = emptyList()
)

@Serializable
data class LiveData(
    val currentUsage: Double, // Current power consumption in watts
    val voltage: Double = 230.0,
    val frequency: Double = 50.0,
    val powerFactor: Double = 0.95,
    val timestamp: Long = System.currentTimeMillis()
)

// BillSummary moved to data.model package

@Serializable
data class AnalyticsResponse(
    val period: String,
    val totalConsumption: Double,
    val averageDaily: Double,
    val peakUsage: Double,
    val costAnalysis: CostAnalysis,
    val deviceBreakdown: List<DeviceUsage>,
    val hourlyData: List<HourlyUsage>,
    val trends: UsageTrends
)

@Serializable
data class CostAnalysis(
    val totalCost: Double,
    val averageDailyCost: Double,
    val projectedMonthlyCost: Double,
    val savingsOpportunity: Double
)

@Serializable
data class DeviceUsage(
    val deviceId: String,
    val deviceName: String,
    val consumption: Double,
    val cost: Double,
    val percentage: Double
)

@Serializable
data class HourlyUsage(
    val hour: Int,
    val usage: Double,
    val cost: Double
)

@Serializable
data class UsageTrends(
    val weekOverWeek: Double,
    val monthOverMonth: Double,
    val yearOverYear: Double,
    val efficiency: Double
)

@Serializable
data class BillingResponse(
    val currentBill: BillSummary,
    val tariffStructure: List<TariffSlab>,
    val paymentHistory: List<PaymentRecord>,
    val usageHistory: List<UsageRecord>
)

@Serializable
data class TariffSlab(
    val minUnits: Double,
    val maxUnits: Double,
    val rate: Double,
    val description: String
)

@Serializable
data class PaymentRecord(
    val period: String,
    val amount: Double,
    val status: String,
    val date: String
)

@Serializable
data class UsageRecord(
    val period: String,
    val units: Double,
    val cost: Double
)

// API Request Models
@Serializable
data class DeviceToggleRequest(
    val state: Boolean
)

@Serializable
data class PaymentRequest(
    val amount: Double,
    val method: String
)

@Serializable
data class PaymentResponse(
    val success: Boolean,
    val transactionId: String?,
    val message: String,
    val paymentUrl: String? = null
)

// Legacy models for backward compatibility
@Serializable
data class HistoricalDataPoint(
    val timestamp: String,
    val consumption: Double
)

@Serializable
data class DeviceConsumption(
    val deviceName: String,
    val consumption: Double,
    val percentage: Float
)

@Serializable
data class Payment(
    val id: String,
    val amount: Double,
    val date: String,
    val status: String
)

@Serializable
data class Tariff(
    val slab: String,
    val rate: Double,
    val units: String
)