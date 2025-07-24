package com.wattswatcher.app.ui.animations

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntOffset

/**
 * Smooth animations for WattsWatcher app
 * Industry-ready transitions and micro-interactions
 */
object WattsWatcherAnimations {
    
    // Standard animation durations
    const val FAST_ANIMATION = 200
    const val NORMAL_ANIMATION = 300
    const val SLOW_ANIMATION = 500
    
    // Easing curves
    val FastOutSlowInEasing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
    val FastOutLinearInEasing = CubicBezierEasing(0.4f, 0.0f, 1.0f, 1.0f)
    val LinearOutSlowInEasing = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)
    
    /**
     * Smooth slide transition for screen navigation
     */
    fun slideTransition(
        direction: SlideDirection = SlideDirection.LEFT
    ): ContentTransform {
        val slideDistance = 300
        val animationSpec = tween<IntOffset>(
            durationMillis = NORMAL_ANIMATION,
            easing = FastOutSlowInEasing
        )
        
        return slideInHorizontally(
            animationSpec = animationSpec,
            initialOffsetX = { 
                when (direction) {
                    SlideDirection.LEFT -> slideDistance
                    SlideDirection.RIGHT -> -slideDistance
                }
            }
        ) + fadeIn(
            animationSpec = tween(NORMAL_ANIMATION)
        ) togetherWith slideOutHorizontally(
            animationSpec = animationSpec,
            targetOffsetX = { 
                when (direction) {
                    SlideDirection.LEFT -> -slideDistance
                    SlideDirection.RIGHT -> slideDistance
                }
            }
        ) + fadeOut(
            animationSpec = tween(NORMAL_ANIMATION)
        )
    }
    
    /**
     * Smooth fade transition
     */
    fun fadeTransition(): ContentTransform {
        return fadeIn(
            animationSpec = tween(
                durationMillis = NORMAL_ANIMATION,
                easing = LinearOutSlowInEasing
            )
        ) togetherWith fadeOut(
            animationSpec = tween(
                durationMillis = FAST_ANIMATION,
                easing = FastOutLinearInEasing
            )
        )
    }
    
    /**
     * Scale transition for cards and buttons
     */
    fun scaleTransition(): ContentTransform {
        return scaleIn(
            animationSpec = tween(
                durationMillis = NORMAL_ANIMATION,
                easing = FastOutSlowInEasing
            ),
            initialScale = 0.8f
        ) + fadeIn(
            animationSpec = tween(NORMAL_ANIMATION)
        ) togetherWith scaleOut(
            animationSpec = tween(
                durationMillis = FAST_ANIMATION,
                easing = FastOutLinearInEasing
            ),
            targetScale = 1.1f
        ) + fadeOut(
            animationSpec = tween(FAST_ANIMATION)
        )
    }
    
    /**
     * Shared element transition for seamless navigation
     */
    @Composable
    fun SharedElementTransition(
        visible: Boolean,
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(
                animationSpec = tween(
                    durationMillis = NORMAL_ANIMATION,
                    easing = FastOutSlowInEasing
                ),
                initialOffsetY = { it / 3 }
            ) + fadeIn(
                animationSpec = tween(NORMAL_ANIMATION)
            ),
            exit = slideOutVertically(
                animationSpec = tween(
                    durationMillis = FAST_ANIMATION,
                    easing = FastOutLinearInEasing
                ),
                targetOffsetY = { -it / 3 }
            ) + fadeOut(
                animationSpec = tween(FAST_ANIMATION)
            ),
            modifier = modifier
        ) {
            content()
        }
    }
    
    /**
     * Pulsing animation for live indicators
     */
    @Composable
    fun PulsingIndicator(
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "scale"
        )
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.7f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "alpha"
        )
        
        Box(
            modifier = modifier.graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
        ) {
            content()
        }
    }
    
    /**
     * Smooth number counter animation
     */
    @Composable
    fun AnimatedCounter(
        targetValue: Float,
        modifier: Modifier = Modifier,
        animationSpec: AnimationSpec<Float> = tween(
            durationMillis = SLOW_ANIMATION,
            easing = FastOutSlowInEasing
        ),
        content: @Composable (value: Float) -> Unit
    ) {
        val animatedValue by animateFloatAsState(
            targetValue = targetValue,
            animationSpec = animationSpec,
            label = "counter"
        )
        
        content(animatedValue)
    }
    
    /**
     * Staggered list animation
     */
    @Composable
    fun StaggeredAnimation(
        visible: Boolean,
        index: Int,
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit
    ) {
        val delay = index * 50 // Stagger by 50ms per item
        
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(
                animationSpec = tween(
                    durationMillis = NORMAL_ANIMATION,
                    delayMillis = delay,
                    easing = FastOutSlowInEasing
                ),
                initialOffsetY = { it / 2 }
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = NORMAL_ANIMATION,
                    delayMillis = delay
                )
            ),
            exit = slideOutVertically(
                animationSpec = tween(
                    durationMillis = FAST_ANIMATION,
                    easing = FastOutLinearInEasing
                ),
                targetOffsetY = { -it / 2 }
            ) + fadeOut(
                animationSpec = tween(FAST_ANIMATION)
            ),
            modifier = modifier
        ) {
            content()
        }
    }
}

enum class SlideDirection {
    LEFT, RIGHT
}