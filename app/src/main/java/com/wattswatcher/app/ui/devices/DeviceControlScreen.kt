package com.wattswatcher.app.ui.devices

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wattswatcher.app.WattsWatcherApplication
import com.wattswatcher.app.data.model.Device
import com.wattswatcher.app.data.model.DevicePriority
import com.wattswatcher.app.data.model.DeviceType
import com.wattswatcher.app.data.model.createDevice
import com.wattswatcher.app.ui.animations.WattsWatcherAnimations
import com.wattswatcher.app.ui.theme.WattsWatcherColors
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceControlScreen(
    onNavigateToDashboard: () -> Unit = {},
    onNavigateToAnalytics: () -> Unit = {}
) {
    val app = LocalContext.current.applicationContext as WattsWatcherApplication
    val viewModel: DeviceControlViewModel = viewModel {
        DeviceControlViewModel(app.repository)
    }
    val state by viewModel.state.collectAsState()
    var showAddDeviceDialog by remember { mutableStateOf(false) }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 20.dp)
    ) {
        // Header with Add Device Button
        item {
            WattsWatcherAnimations.StaggeredAnimation(
                visible = true,
                index = 0
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Device Control",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Manage your smart devices",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }
                    
                    FloatingActionButton(
                        onClick = { showAddDeviceDialog = true },
                        containerColor = WattsWatcherColors.energyGreen,
                        contentColor = Color.White,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Device",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
        
        // Power Summary Card
        item {
            WattsWatcherAnimations.StaggeredAnimation(
                visible = true,
                index = 1
            ) {
                PowerSummaryCard(
                    activeDevices = state.activeDevicesCount,
                    totalPower = state.totalPower,
                    totalDevices = state.totalRegisteredDevices
                )
            }
        }
        
        // Master Control Card
        item {
            WattsWatcherAnimations.StaggeredAnimation(
                visible = true,
                index = 2
            ) {
                MasterControlCard(
                    onTurnOnAll = { viewModel.toggleAllDevices(true) },
                    onTurnOffAll = { viewModel.toggleAllDevices(false) }
                )
            }
        }
        
        // Devices List
        if (state.devices.isNotEmpty()) {
            item {
                Text(
                    text = "Your Devices",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            
            itemsIndexed(state.devices) { index, device ->
                WattsWatcherAnimations.StaggeredAnimation(
                    visible = true,
                    index = index + 3
                ) {
                    ModernDeviceCard(
                        device = device,
                        onToggle = { viewModel.toggleDevice(device.id, !device.isOn) },
                        onSchedule = { 
                            // TODO: Implement scheduling dialog
                        },
                        onOptimize = {
                            // Show optimization suggestion
                        }
                    )
                }
            }
        } else {
            item {
                EmptyDevicesCard(
                    onAddDevice = { showAddDeviceDialog = true }
                )
            }
        }
        
        // Error handling
        state.error?.let { error ->
            item {
                ErrorCard(
                    error = error,
                    onDismiss = { viewModel.dismissError() }
                )
            }
        }
    }
    
    // Add Device Dialog
    if (showAddDeviceDialog) {
        AddDeviceDialog(
            onDismiss = { showAddDeviceDialog = false },
            onAddDevice = { device ->
                viewModel.addDevice(device)
                showAddDeviceDialog = false
            }
        )
    }
}

@Composable
private fun PowerSummaryCard(
    activeDevices: Int,
    totalPower: Double,
    totalDevices: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            WattsWatcherColors.gradientStart.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Power Summary",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    // Live indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        WattsWatcherAnimations.PulsingIndicator {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(WattsWatcherColors.energyGreen, CircleShape)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "LIVE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = WattsWatcherColors.energyGreen
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    PowerStat(
                        value = "$activeDevices",
                        label = "Active",
                        color = WattsWatcherColors.energyGreen
                    )
                    PowerStat(
                        value = "${totalPower.roundToInt()}W",
                        label = "Total Power",
                        color = WattsWatcherColors.energyBlue
                    )
                    PowerStat(
                        value = "$totalDevices",
                        label = "Devices",
                        color = WattsWatcherColors.energyAmber
                    )
                }
            }
        }
    }
}

@Composable
private fun PowerStat(
    value: String,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
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
        colors = CardDefaults.cardColors(
            containerColor = WattsWatcherColors.cardBackground
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Master Control",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onTurnOnAll,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WattsWatcherColors.energyGreen
                    )
                ) {
                    Icon(Icons.Default.PowerSettingsNew, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Turn On All")
                }
                
                OutlinedButton(
                    onClick = onTurnOffAll,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.PowerOff, contentDescription = null)
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
    onToggle: () -> Unit,
    onSchedule: () -> Unit,
    onOptimize: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = WattsWatcherColors.cardBackground
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Device Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = device.icon,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = device.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${device.room} • ${device.wattage.toInt()}W",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
                
                Switch(
                    checked = device.isOn,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = WattsWatcherColors.energyGreen,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = MaterialTheme.colorScheme.outline
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
                    UsageStat(
                        label = "Today",
                        value = "${String.format("%.1f", device.dailyUsage)} kWh"
                    )
                    UsageStat(
                        label = "This Month",
                        value = "${String.format("%.1f", device.monthlyUsage)} kWh"
                    )
                    UsageStat(
                        label = "Cost",
                        value = "₹${String.format("%.2f", device.getDailyEstimatedCost())}"
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onSchedule,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Schedule", style = MaterialTheme.typography.bodySmall)
                    }
                    
                    OutlinedButton(
                        onClick = onOptimize,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.TrendingUp,
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
private fun UsageStat(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun EmptyDevicesCard(
    onAddDevice: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = WattsWatcherColors.cardBackground
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.DeviceHub,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "No devices added yet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Add your first smart device to get started",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onAddDevice,
                colors = ButtonDefaults.buttonColors(
                    containerColor = WattsWatcherColors.energyGreen
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Device")
            }
        }
    }
}

@Composable
fun PriorityBadge(priority: DevicePriority) {
    val (color, text) = when (priority) {
        DevicePriority.LOW -> Pair(Color.Red, "Non-Essential")
        DevicePriority.MEDIUM -> Pair(Color(0xFFFFA000), "High")
        DevicePriority.HIGH -> Pair(Color.Green, "Critical")
    }
    
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(color, CircleShape)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
        }
    }
}

@Composable
fun PrioritySelectionDialog(
    device: Device,
    onDismiss: () -> Unit,
    onPrioritySelected: (DevicePriority) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Set Priority for ${device.name}")
        },
        text = {
            Column {
                Text(
                    "Select the priority level for this device. Higher priority devices will remain powered during load shedding events.",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                PriorityOption(
                    priority = DevicePriority.HIGH,
                    isSelected = device.priority == DevicePriority.HIGH,
                    onClick = { onPrioritySelected(DevicePriority.HIGH) }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                PriorityOption(
                    priority = DevicePriority.MEDIUM,
                    isSelected = device.priority == DevicePriority.MEDIUM,
                    onClick = { onPrioritySelected(DevicePriority.MEDIUM) }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                PriorityOption(
                    priority = DevicePriority.LOW,
                    isSelected = device.priority == DevicePriority.LOW,
                    onClick = { onPrioritySelected(DevicePriority.LOW) }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        dismissButton = {}
    )
}

@Composable
fun PriorityOption(
    priority: DevicePriority,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val (color, text, description) = when (priority) {
        DevicePriority.LOW -> Triple(
            Color.Red, 
            "Non-Essential", 
            "First to be turned off during load shedding (e.g., AC, water heater)"
        )
        DevicePriority.MEDIUM -> Triple(
            Color(0xFFFFA000), 
            "High", 
            "Turned off only during critical events (e.g., TV, fan)"
        )
        DevicePriority.HIGH -> Triple(
            Color.Green, 
            "Critical", 
            "Always kept on if possible (e.g., fridge, lights)"
        )
    }
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = if (isSelected) color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = color)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Column {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleSmall,
                    color = color,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun LoadSheddingDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    isLoadSheddingActive: Boolean
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (isLoadSheddingActive) "End Load Shedding" else "Trigger Load Shedding")
        },
        text = {
            if (isLoadSheddingActive) {
                Text("This will end the simulated load shedding event and restore power to all devices based on their previous state.")
            } else {
                Text("This will simulate a grid shortage event. Non-essential devices will be automatically turned off to reduce power consumption. Critical devices will remain powered.")
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isLoadSheddingActive) 
                        WattsWatcherColors.energyGreen else WattsWatcherColors.energyRed
                )
            ) {
                Text(if (isLoadSheddingActive) "End Load Shedding" else "Start Load Shedding")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun AddDeviceDialog(
    onDismiss: () -> Unit,
    onAddDevice: (Device) -> Unit
) {
    var deviceName by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(DeviceType.OTHER) }
    var deviceWattage by remember { mutableStateOf("") }
    var deviceRoom by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add New Device",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = deviceName,
                    onValueChange = { deviceName = it },
                    label = { Text("Device Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = deviceRoom,
                    onValueChange = { deviceRoom = it },
                    label = { Text("Room") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = deviceWattage,
                    onValueChange = { deviceWattage = it },
                    label = { Text("Power (Watts)") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Device Type Selection
                Text(
                    text = "Device Type",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(DeviceType.values()) { type ->
                        FilterChip(
                            onClick = { selectedType = type },
                            label = { 
                                Text(
                                    text = type.name.replace("_", " ").lowercase()
                                        .replaceFirstChar { it.uppercase() }
                                )
                            },
                            selected = selectedType == type
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (deviceName.isNotBlank() && deviceWattage.isNotBlank()) {
                        val device = createDevice(
                            name = deviceName,
                            type = selectedType,
                            wattage = deviceWattage.toDoubleOrNull() ?: 0.0,
                            room = deviceRoom.ifBlank { "Unknown" }
                        )
                        onAddDevice(device)
                    }
                },
                enabled = deviceName.isNotBlank() && deviceWattage.isNotBlank()
            ) {
                Text("Add Device")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ErrorCard(
    error: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            TextButton(onClick = onDismiss) {
                Text("Dismiss")
            }
        }
    }
}