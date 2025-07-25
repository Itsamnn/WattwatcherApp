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
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Welcome Header
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
        
        // Anomaly Alerts
        if (state.anomalies.isNotEmpty()) {
            item {
                WattsWatcherAnimations.StaggeredAnimation(
                    visible = true,
                    index = 3
                ) {
                    AnomalyAlertCard(
                        anomalies = state.anomalies,
                        onDismiss = { anomaly -> viewModel.dismissAnomaly(anomaly) }
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
                        onViewAllClick = onNavigateToDevices
                    )
                }
            }
        }
        
        // Energy Insights Card
        item {
            WattsWatcherAnimations.StaggeredAnimation(
                visible = true,
                index = 5
            ) {
                EnergyInsightsCard(
                    monthlyUsage = state.monthlyUsage,
                    estimatedBill = state.estimatedBill
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
            text = "Monitor and control your home energy",
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
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            WattsWatcherColors.energyGreen.copy(alpha = 0.1f),
                            Color.Transparent
                        ),
                        radius = 300f
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
                // Live Indicator
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
                        text = "LIVE POWER",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = WattsWatcherColors.energyGreen
                    )
                }
                
                // Power Value with Animation
                WattsWatcherAnimations.AnimatedCounter(
                    targetValue = currentUsage.toFloat()
                ) { animatedValue ->
                    Text(
                        text = "${animatedValue.roundToInt()}W",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 48.sp
                    )
                }
                
                Text(
                    text = "Current Usage",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                
                // Voltage and Frequency
                Row(
                    horizontalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    PowerMetric(
                        label = "Voltage",
                        value = "${voltage.roundToInt()}V",
                        icon = Icons.Default.ElectricBolt
                    )
                    PowerMetric(
                        label = "Frequency",
                        value = "50Hz",
                        icon = Icons.Default.Waves
                    )
                }
            }
        }
    }
}

@Composable
private fun PowerMetric(
    label: String,
    value: String,
    icon: ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = WattsWatcherColors.energyBlue,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
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
        // Monthly Usage Card
        QuickStatCard(
            modifier = Modifier.weight(1f),
            title = "Usage",
            value = "${String.format("%.1f", monthlyUsage)} kWh",
            subtitle = "This month",
            icon = Icons.Default.Analytics,
            color = WattsWatcherColors.energyBlue,
            onClick = onAnalyticsClick
        )
        
        // Estimated Bill Card
        QuickStatCard(
            modifier = Modifier.weight(1f),
            title = "Bill",
            value = "₹${String.format("%.0f", estimatedBill)}",
            subtitle = "Estimated",
            icon = Icons.Default.Receipt,
            color = WattsWatcherColors.energyAmber,
            onClick = onBillingClick
        )
        
        // Active Devices Card
        QuickStatCard(
            modifier = Modifier.weight(1f),
            title = "Active",
            value = "$activeDevicesCount",
            subtitle = "Devices",
            icon = Icons.Default.DeviceHub,
            color = WattsWatcherColors.energyGreen,
            onClick = { }
        )
    }
}

@Composable
private fun QuickStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = WattsWatcherColors.cardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
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
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = WattsWatcherColors.energyAmber,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Energy Alerts",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
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
                            imageVector = Icons.Default.Close,
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
    onViewAllClick: () -> Unit
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
                TextButton(onClick = onViewAllClick) {
                    Text(
                        text = "View All",
                        color = WattsWatcherColors.energyBlue
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            devices.take(3).forEach { device ->
                ActiveDeviceItem(device = device)
                if (device != devices.take(3).last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun ActiveDeviceItem(device: Device) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        WattsWatcherColors.energyGreen.copy(alpha = 0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = device.icon,
                    fontSize = 18.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = device.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${device.wattage.toInt()}W • ${device.room}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
        
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
    }
}

@Composable
private fun EnergyInsightsCard(
    monthlyUsage: Double,
    estimatedBill: Double
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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = WattsWatcherColors.energyAmber,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Energy Insights",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val insight = when {
                monthlyUsage > 300 -> "Your usage is higher than average. Consider optimizing device schedules."
                estimatedBill > 1500 -> "Your bill is trending high. Check for energy-hungry devices."
                else -> "Great job! Your energy usage is within normal range."
            }
            
            Text(
                text = insight,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                lineHeight = 20.sp
            )
        }
    }
}