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
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { 
                            Icon(
                                screen.icon, 
                                contentDescription = null,
                                tint = if (currentDestination?.hierarchy?.any { it.route == screen.route } == true) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            ) 
                        },
                        label = { 
                            Text(
                                screen.title,
                                color = if (currentDestination?.hierarchy?.any { it.route == screen.route } == true) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            ) 
                        },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
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
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it / 3 },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(200))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it / 3 },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(200))
            }
        ) {
            composable(
                Screen.Dashboard.route,
                enterTransition = {
                    slideInVertically(
                        initialOffsetY = { it / 4 },
                        animationSpec = tween(400)
                    ) + fadeIn(animationSpec = tween(400))
                }
            ) {
                DashboardScreen(
                    onNavigateToBilling = {
                        navController.navigate(Screen.Billing.route)
                    },
                    onNavigateToDevices = {
                        navController.navigate(Screen.Devices.route)
                    },
                    onNavigateToAnalytics = {
                        navController.navigate(Screen.Analytics.route)
                    }
                )
            }
            
            composable(
                Screen.Devices.route,
                enterTransition = {
                    slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(350)
                    ) + fadeIn(animationSpec = tween(350))
                }
            ) {
                DeviceControlScreen()
            }
            
            composable(
                Screen.Billing.route,
                enterTransition = {
                    slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = tween(350)
                    ) + fadeIn(animationSpec = tween(350)) + scaleIn(
                        initialScale = 0.9f,
                        animationSpec = tween(350)
                    )
                }
            ) {
                BillingScreen()
            }
            
            composable(
                Screen.Analytics.route,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(350)
                    ) + fadeIn(animationSpec = tween(350)) + scaleIn(
                        initialScale = 0.95f,
                        animationSpec = tween(350)
                    )
                }
            ) {
                AnalyticsScreen()
            }
            
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
        }
    }
}