package com.wattswatcher.app.data.api

import com.wattswatcher.app.data.model.*
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
}

data class DashboardResponse(
    val liveData: LiveData,
    val billSummary: BillSummary,
    val activeDevices: List<Device>
)

data class DeviceToggleRequest(
    val state: Boolean
)

data class PaymentRequest(
    val amount: Double,
    val method: String
)

data class PaymentResponse(
    val transactionId: String,
    val paymentUrl: String?
)

data class AnalyticsResponse(
    val historicalData: List<HistoricalDataPoint>,
    val deviceBreakdown: List<DeviceConsumption>
)

data class HistoricalDataPoint(
    val timestamp: String,
    val consumption: Double
)

data class DeviceConsumption(
    val deviceName: String,
    val consumption: Double,
    val percentage: Float
)