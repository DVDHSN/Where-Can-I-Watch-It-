package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.Verdict
import com.example.ui.theme.VerdictIncluded
import com.example.ui.theme.VerdictIncludedContainer
import com.example.ui.theme.VerdictOnIncludedContainer
import com.example.ui.theme.VerdictOnRentBuyContainer
import com.example.ui.theme.VerdictOnUnavailableContainer
import com.example.ui.theme.VerdictOnUnknownContainer
import com.example.ui.theme.VerdictRentBuy
import com.example.ui.theme.VerdictRentBuyContainer
import com.example.ui.theme.VerdictUnavailable
import com.example.ui.theme.VerdictUnavailableContainer
import com.example.ui.theme.VerdictUnknown
import com.example.ui.theme.VerdictUnknownContainer

@Composable
fun VerdictBadge(
    verdict: Verdict,
    modifier: Modifier = Modifier,
    isCompact: Boolean = true
) {
    val (targetBgColor, targetTextColor, targetBorderColor, icon, label) = when (verdict) {
        is Verdict.Included -> {
            val providerName = verdict.primaryProvider.providerName
            val text = if (isCompact) "Included • $providerName" else "Included with your $providerName subscription"
            Quintet(
                VerdictIncludedContainer,
                VerdictOnIncludedContainer,
                VerdictIncluded.copy(alpha = 0.6f),
                Icons.Default.CheckCircle,
                text
            )
        }
        is Verdict.RentOrBuy -> {
            val price = verdict.priceDisplay?.let { " ($it)" } ?: ""
            val text = if (isCompact) "Rent / Buy$price" else "Available to rent or buy on ${verdict.primaryProvider.providerName}$price"
            Quintet(
                VerdictRentBuyContainer,
                VerdictOnRentBuyContainer,
                VerdictRentBuy.copy(alpha = 0.6f),
                Icons.Default.ShoppingBag,
                text
            )
        }
        is Verdict.Unavailable -> {
            val text = if (isCompact) "Unavailable" else "Unavailable in ${verdict.countryCode}"
            Quintet(
                VerdictUnavailableContainer,
                VerdictOnUnavailableContainer,
                VerdictUnavailable.copy(alpha = 0.6f),
                Icons.Default.Cancel,
                text
            )
        }
        is Verdict.Unknown -> {
            val text = if (isCompact) "Check Status" else "Availability unverified"
            Quintet(
                VerdictUnknownContainer,
                VerdictOnUnknownContainer,
                VerdictUnknown.copy(alpha = 0.5f),
                Icons.AutoMirrored.Filled.HelpOutline,
                text
            )
        }
    }

    val animatedBg by animateColorAsState(targetValue = targetBgColor, animationSpec = tween(250), label = "verdict_bg")
    val animatedBorder by animateColorAsState(targetValue = targetBorderColor, animationSpec = tween(250), label = "verdict_border")

    Box(
        modifier = modifier
            .testTag("verdict_badge")
            .clip(RoundedCornerShape(8.dp))
            .background(animatedBg)
            .border(0.8.dp, animatedBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = if (isCompact) 8.dp else 12.dp, vertical = if (isCompact) 4.dp else 7.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = targetTextColor,
                modifier = Modifier.size(if (isCompact) 13.dp else 17.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                color = targetTextColor,
                fontSize = if (isCompact) 11.sp else 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }
    }
}

private data class Quintet<A, B, C, D, E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
)
