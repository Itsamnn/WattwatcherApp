package com.wattswatcher.app.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wattswatcher.app.data.model.Device
import com.wattswatcher.app.ui.animations.WattsWatcherAnimations
import com.wattswatcher.app.ui.theme.WattsWatcherColors
import kotlin.math.roundToInt

/**
 * Enhanced device card with smooth animations and modern design
 */
@Composable
fun EnhancedDeviceCard(
    device: Device,
    onToggle: (Boolean) -> Unit,
    onSchedule: () -> Unit = {},
    onOptimize: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    Card(
        modifier = modifier
            .scale(scale)
            .clickable {
                isPressed = true
                onToggle(!device.isOn)
            },
        colors = CardDefaults.cardColors(
            containerColor = if (device.isOn) {
                WattsWatcherColors.energyGreen.copy(alpha = 0.1f)
            } else {
                WattsWatcherColors.cardBackground
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (device.isOn) 8.dp else 4.dp
        ),
        border = if (device.isOn) {
            BorderStroke(1.dp, WattsWatcherColors.energyGreen.copy(alpha = 0.3f))
        } else null
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = if (device.isOn) {
                        Brush.radialGradient(
                            colors = listOf(
                                WattsWatcherColors.energyGreen.copy(alpha = 0.05f),
                                Color.Transparent
                            ),
                            radius = 200f
                        )
                    } else {
                        Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                    }
                )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Header with device info and toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Device icon with animation
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    color = if (device.isOn) {
                                        WattsWatcherColors.energyGreen.copy(alpha = 0.2f)
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    },
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (device.isOn) {
                                WattsWatcherAnimations.PulsingIndicator {
                                    Text(
                                        text = device.icon,
                                        fontSize = 24.sp
                                    )
                                }
                            } else {
                                Text(
                                    text = device.icon,
                                    fontSize = 24.sp
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column {
                            Text(
                                text = device.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = device.room,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                    
                    // Animated toggle switch
                    Switch(
                        checked = device.isOn,
                        onCheckedChange = onToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = WattsWatcherColors.energyGreen,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Power consumption with animation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Power",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        WattsWatcherAnimations.AnimatedCounter(
                            targetValue = if (device.isOn) device.wattage.toFloat() else 0f
                        ) { animatedValue ->
                            Text(
                                text = "${animatedValue.roundToInt()}W",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (device.isOn) {
                                    WattsWatcherColors.energyGreen
                                } else {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                }
                            )
                        }
                    }
                    
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "Today",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "${String.format("%.1f", device.dailyUsage)} kWh",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                
                // Action buttons (if device is on)
                AnimatedVisibility(
                    visible = device.isOn,
                    enter = slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = tween(300)
                    ) + fadeIn(animationSpec = tween(300)),
                    exit = slideOutVertically(
                        targetOffsetY = { it / 2 },
                        animationSpec = tween(200)
                    ) + fadeOut(animationSpec = tween(200))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onSchedule,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = WattsWatcherColors.energyBlue
                            )
                        ) {
                            Icon(
                                Icons.Default.Schedule,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Schedule", fontSize = 12.sp)
                        }
                        
                        OutlinedButton(
                            onClick = onOptimize,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = WattsWatcherColors.energyAmber
                            )
                        ) {
                            Icon(
                                Icons.Default.TipsAndUpdates,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Optimize", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
    
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(100)
            isPressed = false
        }
    }
}

/**
 * Enhanced live power card with gradient and animations
 */
@Composable
fun EnhancedLivePowerCard(
    currentUsage: Double,
    voltage: Double,
    frequency: Double,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = WattsWatcherColors.cardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            WattsWatcherColors.gradientStart.copy(alpha = 0.1f),
                            WattsWatcherColors.gradientEnd.copy(alpha = 0.05f),
                            Color.Transparent
                        ),
                        radius = 500f
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
                // Live indicator with pulsing animation
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 20.dp)
                ) {
                    WattsWatcherAnimations.PulsingIndicator {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(
                                    WattsWatcherColors.energyGreen,
                                    CircleShape
                                )
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "LIVE MONITORING",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = WattsWatcherColors.energyGreen,
                        letterSpacing = 1.sp
                    )
                }
                
                // Main power reading with smooth animation
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(60.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 4.dp
                    )
                } else {
                    WattsWatcherAnimations.AnimatedCounter(
                        targetValue = currentUsage.toFloat(),
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    ) { animatedValue ->
                        Text(
                            text = "${animatedValue.roundToInt()}",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 64.sp
                            ),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    Text(
                        text = "WATTS",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        letterSpacing = 2.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Additional metrics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MetricItem(
                        label = "Voltage",
                        value = "${voltage.roundToInt()}V",
                        icon = Icons.Default.ElectricBolt,
                        color = WattsWatcherColors.energyAmber
                    )
                    
                    MetricItem(
                        label = "Frequency",
                        value = "${frequency.roundToInt()}Hz",
                        icon = Icons.Default.Waves,
                        color = WattsWatcherColors.energyBlue
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricItem(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
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