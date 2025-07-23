package com.wattswatcher.app.ui.analytics

import com.wattswatcher.app.data.api.DeviceConsumption
import com.wattswatcher.app.data.api.HistoricalDataPoint

data class AnalyticsState(
    val isLoading: Boolean = false,
    val selectedPeriod: String = "week",
    val historicalData: List<HistoricalDataPoint> = emptyList(),
    val deviceBreakdown: List<DeviceConsumption> = emptyList(),
    val error: String? = null
)