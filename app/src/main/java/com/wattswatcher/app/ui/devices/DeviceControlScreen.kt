package com.wattswatcher.app.ui.devices

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.wattswatcher.app.WattsWatcherApplication
import com.wattswatcher.app.data.model.Device

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceControlScreen() {
    val app = LocalContext.current.applicationContext as WattsWatcherApplication
    val viewModel: DeviceControlViewModel = viewModel {
        DeviceControlViewModel(app.repository)
    }
    val state by viewModel.state.collectAsState()
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            DeviceControlHeader()
        }
        
        // Stats Overview
        item {
            DeviceStatsCard(
                activeCount = state.activeDevicesCount,
                totalPower = state.totalPower.toInt(),
                totalDevices = state.totalRegisteredDevices
            )
        }
        
        // Master Control
        item {
            MasterControlCard(
                onTurnOnAll = { viewModel.toggleAllDevices(true) },
                onTurnOffAll = { viewModel.toggleAllDevices(false) }
            )
        }
        
        // Devices List Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "All Devices (${state.devices.size})",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                IconButton(onClick = { viewModel.refreshDevices() }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        
        // Loading State
        if (state.isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
        
        // Devices List
        items(state.devices) { device ->
            ModernDeviceCard(
                device = device,
                onToggle = { isOn ->
                    viewModel.toggleDevice(device.id, isOn)
                }
            )
        }
        
        // Error handling
        state.error?.let { error ->
            item {
                ErrorCard(
                    error = error,
                    onRetry = { viewModel.refreshDevices() }
                )
            }
        }
    }
}

@Composable
private fun DeviceControlHeader() {
    Column {
        Text(
            text = "Device Control",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Manage your smart devices",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DeviceStatsCard(
    activeCount: Int,
    totalPower: Int,
    totalDevices: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                label = "Active",
                value = activeCount.toString(),
                icon = Icons.Default.PowerSettingsNew,
                color = Color.Green
            )
            StatItem(
                label = "Total Power",
                value = "${totalPower}W",
                icon = Icons.Default.ElectricBolt,
                color = MaterialTheme.colorScheme.primary
            )
            StatItem(
                label = "Devices",
                value = totalDevices.toString(),
                icon = Icons.Default.DeviceHub,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun MasterControlCard(
    onTurnOnAll: () -> Unit,
    onTurnOffAll: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Master Control",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Control all devices at once",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onTurnOnAll,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Green
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PowerSettingsNew,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Turn On All")
                }
                
                OutlinedButton(
                    onClick = onTurnOffAll,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.PowerOff,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Turn Off All")
                }
            }
        }
    }
}

@Composable
private fun ModernDeviceCard(
    device: Device,
    onToggle: (Boolean) -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (device.isOn) 
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        else 
            MaterialTheme.colorScheme.surface,
        animationSpec = tween(300),
        label = "background"
    )
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (device.isOn) 4.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Device Icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (device.isOn) Color.Green.copy(alpha = 0.2f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getDeviceIcon(device.name),
                        contentDescription = null,
                        tint = if (device.isOn) Color.Green else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Device Info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = device.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${device.wattage}W • ${if (device.isOn) "ON" else "OFF"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (device.isOn) Color.Green else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Toggle Switch
                Switch(
                    checked = device.isOn,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color.Green,
                        uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
            
            if (device.isOn) {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Usage Stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    UsageItem(
                        label = "Today",
                        value = "${String.format("%.1f", device.timeTodayH)}h"
                    )
                    UsageItem(
                        label = "Energy",
                        value = "${String.format("%.2f", device.energyTodayKWh)} kWh"
                    )
                    UsageItem(
                        label = "Cost",
                        value = "₹${String.format("%.0f", device.energyTodayKWh * 5.5)}"
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Quick Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { /* TODO: Schedule */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Schedule", style = MaterialTheme.typography.bodySmall)
                    }
                    
                    OutlinedButton(
                        onClick = { /* TODO: Optimize */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Optimize", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun UsageItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ErrorCard(
    error: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Error",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            TextButton(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

private fun getDeviceIcon(deviceName: String): ImageVector {
    return when {
        deviceName.contains("AC", ignoreCase = true) -> Icons.Default.AcUnit
        deviceName.contains("Fan", ignoreCase = true) -> Icons.Default.Air
        deviceName.contains("TV", ignoreCase = true) -> Icons.Default.Tv
        deviceName.contains("Refrigerator", ignoreCase = true) -> Icons.Default.Kitchen
        deviceName.contains("Heater", ignoreCase = true) -> Icons.Default.LocalFireDepartment
        deviceName.contains("Washing", ignoreCase = true) -> Icons.Default.LocalLaundryService
        deviceName.contains("Microwave", ignoreCase = true) -> Icons.Default.Microwave
        deviceName.contains("Router", ignoreCase = true) -> Icons.Default.Router
        deviceName.contains("Light", ignoreCase = true) -> Icons.Default.Lightbulb
        deviceName.contains("Gaming", ignoreCase = true) -> Icons.Default.SportsEsports
        else -> Icons.Default.DeviceHub
    }
}