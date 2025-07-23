package com.wattswatcher.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LiveData(
    val watts: Double,
    val voltage: Double,
    val current: Double
)