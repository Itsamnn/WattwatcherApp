package com.wattswatcher.app.ui.analytics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.wattswatcher.app.WattsWatcherApplication
import com.wattswatcher.app.data.api.DeviceConsumption
import com.wattswatcher.app.data.api.HistoricalDataPoint
import com.wattswatcher.app.ui.components.ConsumptionChart
import com.wattswatcher.app.ui.components.DeviceBreakdownChart
import com.wattswatcher.app.ui.components.PeriodFilterChip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen() {
    val app = LocalContext.current.applicationContext as WattsWatcherApplication
    val viewModel: AnalyticsViewModel = viewModel {
        AnalyticsViewModel(app.repository)
    }
    val state by viewModel.state.collectAsState()
    val periods = listOf("day", "week", "month", "year")
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Analytics & Insights",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Period Filter
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(periods) { period ->
                            PeriodFilterChip(
                                period = period,
                                isSelected = period == state.selectedPeriod,
                                onSelect = { viewModel.selectPeriod(period) }
                            )
                        }
                    }
                }
                
                // Consumption Chart
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Energy Consumption Trend",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            
                            ConsumptionChart(
                                data = state.analyticsData?.hourlyData?.map { hourlyUsage ->
                                    HistoricalDataPoint(
                                        timestamp = hourlyUsage.hour.toString(),
                                        consumption = hourlyUsage.usage
                                    )
                                } ?: emptyList(),
                                period = state.selectedPeriod,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                        }
                    }
                }
                
                // Device Breakdown Chart
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Device Energy Breakdown",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            
                            DeviceBreakdownChart(
                                data = state.analyticsData?.deviceBreakdown?.map { deviceUsage ->
                                    DeviceConsumption(
                                        deviceName = deviceUsage.deviceName,
                                        consumption = deviceUsage.consumption,
                                        percentage = deviceUsage.percentage.toFloat()
                                    )
                                } ?: emptyList(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp)
                            )
                        }
                    }
                }
                
                // Insights & Recommendations
                item {
                    InsightsCard(
                        totalConsumption = state.analyticsData?.totalConsumption ?: 0.0,
                        topDevice = state.analyticsData?.deviceBreakdown?.maxByOrNull { it.consumption }?.deviceName ?: "N/A",
                        period = state.selectedPeriod
                    )
                }
                
                // Device Breakdown List
                item {
                    Text(
                        text = "Device Consumption Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                items(state.analyticsData?.deviceBreakdown ?: emptyList()) { deviceUsage ->
                    DeviceConsumptionItem(
                        device = DeviceConsumption(
                            deviceName = deviceUsage.deviceName,
                            consumption = deviceUsage.consumption,
                            percentage = deviceUsage.percentage.toFloat()
                        )
                    )
                }
            }
        }
        
        state.error?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = "Error: $error",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsCard(
    totalConsumption: Double,
    topDevice: String,
    period: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Smart Insights",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "• Total consumption this $period: ${String.format("%.1f", totalConsumption)} kWh",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Text(
                text = "• Highest consuming device: $topDevice",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Text(
                text = "• Consider scheduling high-power devices during off-peak hours",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceConsumptionItem(device: DeviceConsumption) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.DeviceHub,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = device.deviceName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${String.format("%.2f", device.consumption)} kWh",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = "${String.format("%.1f", device.percentage)}%",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}