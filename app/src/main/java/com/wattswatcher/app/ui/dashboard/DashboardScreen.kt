package com.wattswatcher.app.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wattswatcher.app.data.model.Device
import com.wattswatcher.app.ui.components.MetricCard
import com.wattswatcher.app.ui.components.DeviceListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "WattsWatcher",
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
            // Live Wattage Display
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Live Power Usage",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(120.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = (state.liveData?.watts ?: 0.0).toFloat() / 5000f,
                            modifier = Modifier.fillMaxSize(),
                            strokeWidth = 8.dp,
                            color = if ((state.liveData?.watts ?: 0.0) > 3000) 
                                MaterialTheme.colorScheme.error 
                            else MaterialTheme.colorScheme.primary
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${state.liveData?.watts?.toInt() ?: 0}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Watts",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            // Metrics Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricCard(
                    title = "Monthly Usage",
                    value = "${state.monthlyUsage.toInt()} kWh",
                    icon = Icons.Default.ElectricBolt,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Est. Bill",
                    value = "₹${state.estimatedBill.toInt()}",
                    icon = Icons.Default.CurrencyRupee,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricCard(
                    title = "Voltage",
                    value = "${state.liveData?.voltage?.toInt() ?: 0}V",
                    icon = Icons.Default.Bolt,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Current",
                    value = "${String.format("%.1f", state.liveData?.current ?: 0.0)}A",
                    icon = Icons.Default.FlashOn,
                    modifier = Modifier.weight(1f)
                )
            }
            
            // AI Anomaly Detection
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (state.anomalyDetected) 
                        MaterialTheme.colorScheme.errorContainer 
                    else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (state.anomalyDetected) Icons.Default.Warning else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (state.anomalyDetected) 
                            MaterialTheme.colorScheme.error 
                        else Color.Green
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (state.anomalyDetected) 
                            "Anomaly Detected!" 
                        else "All Systems Normal",
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
            
            // Active Devices
            Text(
                text = "Active Devices",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            LazyColumn {
                items(state.activeDevices) { device ->
                    DeviceListItem(
                        device = device,
                        modifier = Modifier.padding(bottom = 8.dp)
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