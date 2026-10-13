package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.AppleSecondaryBackground
import com.example.ui.theme.AppleTertiaryBackground

/**
 * Authentic Apple iOS tactile press bounce effect with spring physics.
 * Scales down gently and subtly dims opacity during touch, rebounding with natural momentum.
 */
@Composable
fun Modifier.applePressEffect(
    enabled: Boolean = true,
    scaleDown: Float = 0.95f,
    alphaDown: Float = 0.88f,
    onClick: () -> Unit
): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleDown else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "apple_press_scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isPressed) alphaDown else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "apple_press_alpha"
    )

    return this
        .scale(scale)
        .alpha(alpha)
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            onClick = onClick
        )
}

/**
 * Backward compatibility alias for bouncyClickable.
 */
@Composable
fun Modifier.bouncyClickable(
    enabled: Boolean = true,
    scaleDown: Float = 0.95f,
    onClick: () -> Unit
): Modifier = applePressEffect(
    enabled = enabled,
    scaleDown = scaleDown,
    alphaDown = 0.92f,
    onClick = onClick
)

/**
 * Gentle Apple-style breathing pulse animation for active badges or highlights.
 */
@Composable
fun Modifier.applePulse(
    minScale: Float = 0.97f,
    maxScale: Float = 1.03f,
    durationMillis: Int = 1800
): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_transition")
    val scale by infiniteTransition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    return this.scale(scale)
}

/**
 * Apple Dark Mode Frosted Shimmer Brush.
 */
@Composable
fun shimmerBrush(targetValue: Float = 1000f): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer_transition")
    val translateAnimation by transition.animateFloat(
        initialValue = -300f,
        targetValue = targetValue + 300f,
        animationSpec = infiniteRepeatable(
            animation = tween(1300, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_animation"
    )

    return Brush.linearGradient(
        colors = listOf(
            AppleSecondaryBackground,
            AppleTertiaryBackground.copy(alpha = 0.95f),
            Color(0xFF38383A),
            AppleTertiaryBackground.copy(alpha = 0.95f),
            AppleSecondaryBackground
        ),
        start = Offset(x = translateAnimation - 350f, y = translateAnimation - 350f),
        end = Offset(x = translateAnimation, y = translateAnimation)
    )
}
