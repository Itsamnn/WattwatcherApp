package com.wattswatcher.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.wattswatcher.app.data.model.LiveData

@Entity(tableName = "live_data")
data class LiveDataEntity(
    @PrimaryKey
    val id: Int = 1, // Single row for current live data
    val watts: Double,
    val voltage: Double,
    val current: Double,
    val timestamp: Long = System.currentTimeMillis()
)

fun LiveDataEntity.toLiveData(): LiveData {
    return LiveData(
        watts = watts,
        voltage = voltage,
        current = current
    )
}

fun LiveData.toEntity(): LiveDataEntity {
    return LiveDataEntity(
        watts = watts,
        voltage = voltage,
        current = current
    )
}