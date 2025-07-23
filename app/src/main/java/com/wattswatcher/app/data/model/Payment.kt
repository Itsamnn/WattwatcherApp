package com.wattswatcher.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Payment(
    val date: String,
    val amount: Double,
    val status: String
)