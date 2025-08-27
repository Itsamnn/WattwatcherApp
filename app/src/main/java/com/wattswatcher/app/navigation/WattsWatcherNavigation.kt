package com.wattswatcher.app.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.wattswatcher.app.ui.dashboard.DashboardScreen
import com.wattswatcher.app.ui.devices.DeviceControlScreen
import com.wattswatcher.app.ui.billing.BillingScreen
import com.wattswatcher.app.ui.analytics.AnalyticsScreen
import com.wattswatcher.app.ui.settings.SettingsScreen
import com.wattswatcher.app.ui.animations.WattsWatcherAnimations

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
    object Devices : Screen("devices", "Devices", Icons.Default.DeviceHub)
    object Billing : Screen("billing", "Billing", Icons.Default.Receipt)
    object Analytics : Screen("analytics", "Analytics", Icons.Default.Analytics)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WattsWatcherNavigation() {
    val navController = rememberNavController()
    val items = listOf(
        Screen.Dashboard,
        Screen.Devices,
        Screen.Billing,
        Screen.Analytics,
        Screen.Settings
    )
    
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { 
                            Icon(
                                screen.icon, 
                                contentDescription = null,
                                modifier = Modifier.animateContentSize()
                            ) 
                        },
                        label = { 
                            Text(
                                screen.title,
                                style = MaterialTheme.typography.labelSmall
                            ) 
                        },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Clear back stack to prevent navigation issues
                                popUpTo(Screen.Dashboard.route) {
                                    inclusive = screen.route == Screen.Dashboard.route
                                }
                                launchSingleTop = true
                                restoreState = false // Disable to prevent stuck navigation
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onNavigateToBilling = {
                        navController.navigate(Screen.Billing.route) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToDevices = {
                        navController.navigate(Screen.Devices.route) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToAnalytics = {
                        navController.navigate(Screen.Analytics.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            
            composable(Screen.Devices.route) {
                DeviceControlScreen(
                    onNavigateToDashboard = {
                        navController.navigate(Screen.Dashboard.route) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToAnalytics = {
                        navController.navigate(Screen.Analytics.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            
            composable(Screen.Billing.route) {
                BillingScreen(
                    onNavigateToDashboard = {
                        navController.navigate(Screen.Dashboard.route) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToDevices = {
                        navController.navigate(Screen.Devices.route) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToAnalytics = {
                        navController.navigate(Screen.Analytics.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            
            composable(Screen.Analytics.route) {
                AnalyticsScreen(
                    onNavigateToDashboard = {
                        navController.navigate(Screen.Dashboard.route) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToDevices = {
                        navController.navigate(Screen.Devices.route) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToBilling = {
                        navController.navigate(Screen.Billing.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
        }
    }
}