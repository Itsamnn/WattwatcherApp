package com.wattswatcher.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class BillSummary(
    val amount: Double,
    val unitsConsumed: Double,
    val rate: Double,
    val dueDate: String,
    val status: String = "Pending",
    val period: String = getCurrentMonth(),
    val previousAmount: Double = 0.0,
    val savings: Double = 0.0
) {
    val isOverdue: Boolean get() = status == "Overdue"
    val isPaid: Boolean get() = status == "Paid"
    val isPending: Boolean get() = status == "Pending"
    
    fun getStatusColor(): String = when (status) {
        "Paid" -> "#00D100"
        "Pending" -> "#FF8C00"
        "Overdue" -> "#FF4500"
        else -> "#9E9E9E"
    }
}

private fun getCurrentMonth(): String {
    val calendar = java.util.Calendar.getInstance()
    val format = java.text.SimpleDateFormat("MMM yyyy", java.util.Locale.getDefault())
    return format.format(calendar.time)
}