package com.wattswatcher.app.data.mock

import com.wattswatcher.app.data.api.*
import com.wattswatcher.app.data.model.*

object MockDataProvider {
    
    fun getMockDashboardData(): DashboardResponse {
        return DashboardResponse(
            liveData = LiveData(
                watts = 2450.0,
                voltage = 230.0,
                current = 10.65
            ),
            billSummary = BillSummary(
                amount = 1250.75,
                unitsConsumed = 285.5,
                rate = 4.38,
                dueDate = "2024-02-15"
            ),
            activeDevices = getMockActiveDevices()
        )
    }
    
    fun getMockDevices(): List<Device> {
        return listOf(
            Device("1", "Air Conditioner", 1500, true, 6.5, 9.75),
            Device("2", "Water Heater", 2000, false, 2.0, 4.0),
            Device("3", "Refrigerator", 150, true, 24.0, 3.6),
            Device("4", "Washing Machine", 800, false, 1.5, 1.2),
            Device("5", "TV", 120, true, 5.0, 0.6),
            Device("6", "Microwave", 1000, false, 0.5, 0.5),
            Device("7", "Lights", 200, true, 8.0, 1.6),
            Device("8", "Fan", 75, true, 10.0, 0.75),
            Device("9", "Computer", 300, true, 8.0, 2.4),
            Device("10", "Router", 15, true, 24.0, 0.36)
        )
    }
    
    private fun getMockActiveDevices(): List<Device> {
        return getMockDevices().filter { it.isOn }
    }
    
    fun getMockAnalyticsData(period: String): AnalyticsResponse {
        val historicalData = when (period) {
            "day" -> (0..23).map { hour ->
                HistoricalDataPoint("${hour}:00", (15..45).random().toDouble())
            }
            "week" -> (0..6).map { day ->
                val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                HistoricalDataPoint(days[day], (200..400).random().toDouble())
            }
            "month" -> (1..30).map { day ->
                HistoricalDataPoint("$day", (180..420).random().toDouble())
            }
            else -> (1..12).map { month ->
                HistoricalDataPoint("Month $month", (5000..8000).random().toDouble())
            }
        }
        
        val deviceBreakdown = listOf(
            DeviceConsumption("Air Conditioner", 45.2, 35.5f),
            DeviceConsumption("Water Heater", 28.7, 22.5f),
            DeviceConsumption("Refrigerator", 18.3, 14.4f),
            DeviceConsumption("Washing Machine", 12.1, 9.5f),
            DeviceConsumption("TV", 8.9, 7.0f),
            DeviceConsumption("Lights", 7.2, 5.7f),
            DeviceConsumption("Others", 6.8, 5.4f)
        )
        
        return AnalyticsResponse(historicalData, deviceBreakdown)
    }
}