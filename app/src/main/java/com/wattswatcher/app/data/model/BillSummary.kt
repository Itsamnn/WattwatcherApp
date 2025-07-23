package com.wattswatcher.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class BillSummary(
    val amount: Double,
    val unitsConsumed: Double,
    val rate: Double,
    val dueDate: String
)