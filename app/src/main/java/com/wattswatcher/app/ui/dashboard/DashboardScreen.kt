package com.wattswatcher.app.ui.dashboard

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wattswatcher.app.WattsWatcherApplication
import com.wattswatcher.app.data.model.Device
import com.wattswatcher.app.ui.animations.WattsWatcherAnimations
import com.wattswatcher.app.ui.theme.WattsWatcherColors
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToBilling: () -> Unit = {},
    onNavigateToDevices: () -> Unit = {},
    onNavigateToAnalytics: () -> Unit = {}
) {
    val app = LocalContext.current.applicationContext as WattsWatcherApplication
    val viewModel: DashboardViewModel = viewModel {
        DashboardViewModel(app.repository)
    }
    val state by viewModel.state.collectAsState()
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Welcome Header with Animation
        item {
            WattsWatcherAnimations.StaggeredAnimation(
                visible = true,
                index = 0
            ) {
                WelcomeHeader()
            }
        }
        
        // Live Power Gauge - Hero Section
        item {
            WattsWatcherAnimations.StaggeredAnimation(
                visible = true,
                index = 1
            ) {
                LivePowerGaugeCard(
                    currentUsage = state.liveData?.currentUsage ?: 0.0,
                    voltage = state.liveData?.voltage ?: 230.0,
                    isLoading = state.isLoading
                )
            }
        }
        
        // Quick Stats Row
        item {
            WattsWatcherAnimations.StaggeredAnimation(
                visible = true,
                index = 2
            ) {
                QuickStatsRow(
                    monthlyUsage = state.monthlyUsage,
                    estimatedBill = state.estimatedBill,
                    activeDevicesCount = state.activeDevices.size,
                    onBillingClick = onNavigateToBilling,
                    onAnalyticsClick = onNavigateToAnalytics
                )
            }
        }
        
        // Anomaly Alert (if any)
        if (state.anomalyDetected && state.anomalies.isNotEmpty()) {
            item {
                WattsWatcherAnimations.StaggeredAnimation(
                    visible = true,
                    index = 3
                ) {
                    AnomalyAlertCard(
                        anomalies = state.anomalies,
                        onDismiss = { anomaly ->
                            viewModel.dismissAnomaly(anomaly)
                        }
                    )
                }
            }
        }
        
        // Active Devices Section
        if (state.activeDevices.isNotEmpty()) {
            item {
                WattsWatcherAnimations.StaggeredAnimation(
                    visible = true,
                    index = 4
                ) {
                    ActiveDevicesSection(
                        devices = state.activeDevices,
                        onDevicesClick = onNavigateToDevices
                    )
                }
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
}

@Composable
private fun WelcomeHeader() {
    Column {
        Text(
            text = "Hi, Welcome back! 👋",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Monitor and control your energy usage",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun LivePowerGaugeCard(
    currentUsage: Double,
    voltage: Double,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
        colors = CardDefaults.cardColors(
            containerColor = WattsWatcherColors.cardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            WattsWatcherColors.gradientStart.copy(alpha = 0.1f),
                            Color.Transparent
                        ),
                        radius = 400f
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Live indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    WattsWatcherAnimations.PulsingIndicator {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    WattsWatcherColors.energyGreen,
                                    CircleShape
                                )
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "LIVE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = WattsWatcherColors.energyGreen
                    )
                }
                
                // Main power reading
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    WattsWatcherAnimations.AnimatedCounter(
                        targetValue = currentUsage.toFloat()
                    ) { animatedValue ->
                        Text(
                            text = "${animatedValue.roundToInt()}W",
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                
                Text(
                    text = "Current Usage",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                // Voltage info
                Row(
                    modifier = Modifier.padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.ElectricBolt,
                        contentDescription = null,
                        tint = WattsWatcherColors.energyAmber,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${voltage.roundToInt()}V",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickStatsRow(
    monthlyUsage: Double,
    estimatedBill: Double,
    activeDevicesCount: Int,
    onBillingClick: () -> Unit,
    onAnalyticsClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Monthly Usage
        QuickStatCard(
            modifier = Modifier.weight(1f),
            title = "Usage",
            value = "${String.format("%.1f", monthlyUsage)} kWh",
            icon = Icons.Default.TrendingUp,
            color = WattsWatcherColors.energyBlue,
            onClick = onAnalyticsClick
        )
        
        // Estimated Bill
        QuickStatCard(
            modifier = Modifier.weight(1f),
            title = "Bill",
            value = "₹${String.format("%.0f", estimatedBill)}",
            icon = Icons.Default.Receipt,
            color = WattsWatcherColors.energyAmber,
            onClick = onBillingClick
        )
        
        // Active Devices
        QuickStatCard(
            modifier = Modifier.weight(1f),
            title = "Active",
            value = "$activeDevicesCount devices",
            icon = Icons.Default.DeviceHub,
            color = WattsWatcherColors.energyGreen,
            onClick = {}
        )
    }
}

@Composable
private fun QuickStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = WattsWatcherColors.cardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun AnomalyAlertCard(
    anomalies: List<String>,
    onDismiss: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = WattsWatcherColors.energyAmber.copy(alpha = 0.1f)
        ),
        border = BorderStroke(1.dp, WattsWatcherColors.energyAmber.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = WattsWatcherColors.energyAmber,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Energy Alert",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            anomalies.forEach { anomaly ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = anomaly,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { onDismiss(anomaly) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActiveDevicesSection(
    devices: List<Device>,
    onDevicesClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDevicesClick() },
        colors = CardDefaults.cardColors(
            containerColor = WattsWatcherColors.cardBackground
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Active Devices",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            devices.take(3).forEach { device ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = device.icon,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = device.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "${device.wattage.toInt()}W",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = WattsWatcherColors.energyGreen
                    )
                }
            }
            
            if (devices.size > 3) {
                Text(
                    text = "+${devices.size - 3} more devices",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
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
            TextButton(
                onClick = onDismiss
            ) {
                Text("Dismiss")
            }
        }
    }
}