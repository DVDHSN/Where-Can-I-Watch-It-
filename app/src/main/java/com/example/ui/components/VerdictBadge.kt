package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.Verdict
import com.example.ui.theme.AppleGreen
import com.example.ui.theme.AppleOrange
import com.example.ui.theme.AppleRed
import com.example.ui.theme.AppleLabelSecondary

/**
 * Minimalist Apple Capsule Verdict Badge.
 * High-clarity status indicator styled with iOS system accent tones.
 */
@Composable
fun VerdictBadge(
    verdict: Verdict,
    modifier: Modifier = Modifier,
    isCompact: Boolean = true
) {
    val (statusColor, icon, label) = when (verdict) {
        is Verdict.Included -> {
            val providerName = verdict.primaryProvider.providerName
            val text = if (isCompact) "Included • $providerName" else "Included with your $providerName plan"
            Triple(AppleGreen, Icons.Default.CheckCircle, text)
        }
        is Verdict.RentOrBuy -> {
            val price = verdict.priceDisplay?.let { " ($it)" } ?: ""
            val text = if (isCompact) "Rent / Buy$price" else "Available on ${verdict.primaryProvider.providerName}$price"
            Triple(AppleOrange, Icons.Default.ShoppingBag, text)
        }
        is Verdict.Unavailable -> {
            val text = if (isCompact) "Not Streaming" else "Unavailable to stream in ${verdict.countryCode}"
            Triple(AppleRed, Icons.Default.Cancel, text)
        }
        is Verdict.Unknown -> {
            val text = if (isCompact) "Check Status" else "Streaming availability unverified"
            Triple(AppleLabelSecondary, Icons.AutoMirrored.Filled.HelpOutline, text)
        }
    }

    val animatedColor by animateColorAsState(
        targetValue = statusColor,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy),
        label = "verdict_status_color"
    )

    Box(
        modifier = modifier
            .testTag("verdict_badge")
            .clip(CircleShape)
            .background(animatedColor.copy(alpha = 0.14f))
            .border(0.6.dp, animatedColor.copy(alpha = 0.35f), CircleShape)
            .padding(
                horizontal = if (isCompact) 8.dp else 12.dp,
                vertical = if (isCompact) 4.dp else 6.dp
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = animatedColor,
                modifier = Modifier.size(if (isCompact) 12.dp else 15.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = label,
                color = animatedColor,
                fontSize = if (isCompact) 11.sp else 12.5.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.1).sp,
                maxLines = 1
            )
        }
    }
}
