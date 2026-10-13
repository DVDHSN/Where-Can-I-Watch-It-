package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppleGold
import com.example.ui.theme.AppleLabelPrimary
import kotlinx.coroutines.delay

data class DynamicBannerMessage(
    val id: Long = System.currentTimeMillis(),
    val icon: ImageVector,
    val text: String,
    val iconTint: Color = AppleGold
)

/**
 * Apple Dynamic Island Style Floating Notification Pill.
 * Pops from the top status area with spring physics when actions take place.
 */
@Composable
fun AppleDynamicIsland(
    message: DynamicBannerMessage?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    durationMs: Long = 2200L
) {
    LaunchedEffect(message?.id) {
        if (message != null) {
            delay(durationMs)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = message != null,
        enter = slideInVertically(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            initialOffsetY = { -it }
        ) + fadeIn() + scaleIn(initialScale = 0.85f),
        exit = slideOutVertically(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMedium
            ),
            targetOffsetY = { -it }
        ) + fadeOut() + scaleOut(targetScale = 0.9f),
        modifier = modifier
            .statusBarsPadding()
            .padding(top = 8.dp)
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            if (message != null) {
                Row(
                    modifier = Modifier
                        .shadow(16.dp, shape = CircleShape, spotColor = Color(0x66000000))
                        .clip(CircleShape)
                        .background(Color(0xFF141416))
                        .border(1.dp, Color(0x33FFFFFF), CircleShape)
                        .padding(horizontal = 16.dp, vertical = 9.dp)
                        .testTag("dynamic_island_banner"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = message.icon,
                        contentDescription = null,
                        tint = message.iconTint,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = message.text,
                        color = AppleLabelPrimary,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.1).sp
                    )
                }
            }
        }
    }
}
