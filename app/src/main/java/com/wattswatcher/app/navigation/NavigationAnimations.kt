package com.wattswatcher.app.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.navigation.NavBackStackEntry
import com.wattswatcher.app.ui.animations.WattsWatcherAnimations

/**
 * Navigation animations for smooth screen transitions
 */
object NavigationAnimations {
    
    /**
     * Enter transition when navigating to a new screen
     */
    fun enterTransition(): (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?) = {
        slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tween(WattsWatcherAnimations.NORMAL_ANIMATION)
        ) + fadeIn(
            animationSpec = tween(WattsWatcherAnimations.NORMAL_ANIMATION)
        )
    }
    
    /**
     * Exit transition when leaving current screen
     */
    fun exitTransition(): (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?) = {
        slideOutHorizontally(
            targetOffsetX = { -it / 3 },
            animationSpec = tween(WattsWatcherAnimations.NORMAL_ANIMATION)
        ) + fadeOut(
            animationSpec = tween(WattsWatcherAnimations.FAST_ANIMATION)
        )
    }
    
    /**
     * Pop enter transition when returning to previous screen
     */
    fun popEnterTransition(): (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?) = {
        slideInHorizontally(
            initialOffsetX = { -it / 3 },
            animationSpec = tween(WattsWatcherAnimations.NORMAL_ANIMATION)
        ) + fadeIn(
            animationSpec = tween(WattsWatcherAnimations.NORMAL_ANIMATION)
        )
    }
    
    /**
     * Pop exit transition when going back
     */
    fun popExitTransition(): (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?) = {
        slideOutHorizontally(
            targetOffsetX = { it },
            animationSpec = tween(WattsWatcherAnimations.NORMAL_ANIMATION)
        ) + fadeOut(
            animationSpec = tween(WattsWatcherAnimations.FAST_ANIMATION)
        )
    }
    
    /**
     * Special transition for dashboard to billing (inspired by tapping bill card)
     */
    fun dashboardToBillingTransition(): (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?) = {
        slideInVertically(
            initialOffsetY = { it / 2 },
            animationSpec = tween(
                durationMillis = WattsWatcherAnimations.NORMAL_ANIMATION,
                easing = WattsWatcherAnimations.FastOutSlowInEasing
            )
        ) + fadeIn(
            animationSpec = tween(WattsWatcherAnimations.NORMAL_ANIMATION)
        ) + scaleIn(
            initialScale = 0.9f,
            animationSpec = tween(WattsWatcherAnimations.NORMAL_ANIMATION)
        )
    }
    
    /**
     * Special transition for device control (slide up from bottom)
     */
    fun deviceControlTransition(): (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?) = {
        slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(
                durationMillis = WattsWatcherAnimations.NORMAL_ANIMATION,
                easing = WattsWatcherAnimations.FastOutSlowInEasing
            )
        ) + fadeIn(
            animationSpec = tween(WattsWatcherAnimations.NORMAL_ANIMATION)
        )
    }
    
    /**
     * Analytics screen transition (slide from right with scale)
     */
    fun analyticsTransition(): (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?) = {
        slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tween(WattsWatcherAnimations.NORMAL_ANIMATION)
        ) + fadeIn(
            animationSpec = tween(WattsWatcherAnimations.NORMAL_ANIMATION)
        ) + scaleIn(
            initialScale = 0.95f,
            animationSpec = tween(WattsWatcherAnimations.NORMAL_ANIMATION)
        )
    }
}