package com.wattswatcher.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Tariff(
    val slab: String,
    val rate: Double
)