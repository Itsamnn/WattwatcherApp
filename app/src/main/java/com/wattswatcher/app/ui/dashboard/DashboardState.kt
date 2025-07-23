package com.wattswatcher.app.ui.dashboard

import com.wattswatcher.app.data.model.*

data class DashboardState(
    val isLoading: Boolean = false,
    val liveData: LiveData? = null,
    val billSummary: BillSummary? = null,
    val activeDevices: List<Device> = emptyList(),
    val error: String? = null,
    val monthlyUsage: Double = 0.0,
    val estimatedBill: Double = 0.0,
    val anomalyDetected: Boolean = false
)