package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppleBorder
import com.example.ui.theme.AppleLabelPrimary
import com.example.ui.theme.AppleLabelSecondary
import com.example.ui.theme.AppleSecondaryBackground
import com.example.ui.theme.AppleTertiaryBackground

/**
 * Authentic Apple iOS Segmented Control with a sliding spring-animated indicator pill.
 */
@Composable
fun <T> AppleSegmentedControl(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    itemLabel: (T) -> String = { it.toString() },
    height: Dp = 38.dp,
    containerCornerRadius: Dp = 10.dp
) {
    val selectedIndex = items.indexOf(selectedItem).coerceAtLeast(0)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(containerCornerRadius))
            .background(AppleSecondaryBackground)
            .border(0.6.dp, AppleBorder, RoundedCornerShape(containerCornerRadius))
            .padding(2.5.dp)
            .testTag("apple_segmented_control")
    ) {
        val totalWidth = maxWidth
        val segmentWidth = if (items.isNotEmpty()) totalWidth / items.size else 0.dp

        val indicatorOffset by animateDpAsState(
            targetValue = segmentWidth * selectedIndex,
            animationSpec = spring(
                dampingRatio = 0.75f,
                stiffness = Spring.StiffnessMediumLow
            ),
            label = "segmented_indicator_offset"
        )

        // Sliding Indicator Pill (iOS Glass/Elevated Thumb)
        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .width(segmentWidth)
                .fillMaxHeight()
                .shadow(
                    elevation = 2.dp,
                    shape = RoundedCornerShape(containerCornerRadius - 2.dp),
                    clip = false
                )
                .clip(RoundedCornerShape(containerCornerRadius - 2.dp))
                .background(Color(0xFF38383A)) // iOS Active Thumb Fill
                .border(
                    0.5.dp,
                    Color(0x33FFFFFF),
                    RoundedCornerShape(containerCornerRadius - 2.dp)
                )
        )

        // Segments Row
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = index == selectedIndex

                val textColor by animateColorAsState(
                    targetValue = if (isSelected) AppleLabelPrimary else AppleLabelSecondary,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy),
                    label = "segmented_text_color"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(containerCornerRadius - 2.dp))
                        .applePressEffect(
                            scaleDown = 0.94f,
                            alphaDown = 0.85f,
                            onClick = { onItemSelected(item) }
                        )
                        .testTag("segment_${itemLabel(item).lowercase().replace(" ", "_")}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = itemLabel(item),
                        color = textColor,
                        fontSize = 12.5.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        letterSpacing = (-0.1).sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
