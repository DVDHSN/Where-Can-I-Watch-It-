package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.domain.model.MediaType
import com.example.domain.model.RegionalAvailability
import com.example.domain.model.Title
import com.example.domain.model.Verdict
import com.example.ui.theme.AppleBorder
import com.example.ui.theme.AppleGold
import com.example.ui.theme.AppleGreen
import com.example.ui.theme.AppleLabelPrimary
import com.example.ui.theme.AppleLabelSecondary
import com.example.ui.theme.AppleLabelTertiary
import com.example.ui.theme.AppleOrange
import com.example.ui.theme.AppleSecondaryBackground
import com.example.ui.theme.AppleTertiaryBackground

/**
 * Apple iOS Modal Sheet for Title Details.
 * Designed with Human Interface Guidelines, translucent materials, and spring motion.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TitleDetailsBottomSheet(
    title: Title,
    verdict: Verdict?,
    availability: RegionalAvailability?,
    userSubscriptions: Set<Int>,
    isInWatchlist: Boolean,
    isLoadingVerdict: Boolean,
    onToggleWatchlist: () -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF161618),
        dragHandle = {
            // Apple Grabber Capsule
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(38.dp)
                    .height(4.5.dp)
                    .clip(CircleShape)
                    .background(Color(0x40FFFFFF))
            )
        },
        modifier = modifier.testTag("title_details_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 36.dp)
        ) {
            // Backdrop with gradient fade
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(AppleSecondaryBackground)
            ) {
                if (title.backdropUrl != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(title.backdropUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }

                // Apple gradient fade overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0xCC161618),
                                    Color(0xFF161618)
                                )
                            )
                        )
                )

                // iOS Circular Blurred Close Button
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(14.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0x66000000))
                        .border(0.5.dp, Color(0x33FFFFFF), CircleShape)
                        .applePressEffect(scaleDown = 0.88f, onClick = onDismiss)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }

            // Body
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title and Meta row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Poster thumbnail
                    Box(
                        modifier = Modifier
                            .width(82.dp)
                            .aspectRatio(2f / 3f)
                            .clip(RoundedCornerShape(12.dp))
                            .border(0.6.dp, AppleBorder, RoundedCornerShape(12.dp))
                            .background(AppleSecondaryBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        if (title.posterUrl != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(title.posterUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = title.title,
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = title.title,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.35).sp
                            ),
                            color = AppleLabelPrimary
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(7.dp)
                        ) {
                            if (!title.releaseYear.isNullOrBlank()) {
                                Text(
                                    text = title.releaseYear,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AppleLabelSecondary
                                )
                                Text(
                                    text = "•",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AppleLabelSecondary.copy(alpha = 0.4f)
                                )
                            }
                            Text(
                                text = if (title.mediaType == MediaType.TV) "TV Series" else "Movie",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = AppleGold
                            )

                            if (title.voteAverage > 0.0) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(AppleGold.copy(alpha = 0.12f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = AppleGold,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Text(
                                        text = String.format("%.1f", title.voteAverage),
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = AppleGold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Streaming Verdict Section
                AnimatedContent(
                    targetState = isLoadingVerdict,
                    transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
                    label = "verdict_container_transition"
                ) { loading ->
                    if (loading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(AppleSecondaryBackground)
                                .border(0.6.dp, AppleBorder, RoundedCornerShape(14.dp))
                                .padding(18.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = AppleGold,
                                    strokeWidth = 2.dp
                                )
                                Text(
                                    text = "Verifying subscription catalog...",
                                    color = AppleLabelSecondary,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    } else if (verdict != null) {
                        AppleVerdictCard(
                            verdict = verdict,
                            onOpenLink = { url ->
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            }
                        )
                    }
                }

                // Apple Primary Action Button: Watchlist Toggle with Haptic Spring Compression
                val bookmarkScale by animateFloatAsState(
                    targetValue = if (isInWatchlist) 1.05f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "bookmark_spring"
                )
                val buttonBg by animateColorAsState(
                    targetValue = if (isInWatchlist) AppleTertiaryBackground else AppleGold,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy),
                    label = "button_bg_color"
                )

                Button(
                    onClick = onToggleWatchlist,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("watchlist_toggle_button")
                        .applePressEffect(scaleDown = 0.96f, onClick = onToggleWatchlist),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttonBg,
                        contentColor = if (isInWatchlist) AppleLabelPrimary else Color(0xFF141000)
                    )
                ) {
                    Icon(
                        imageVector = if (isInWatchlist) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        modifier = Modifier
                            .size(18.dp)
                            .scale(bookmarkScale),
                        tint = if (isInWatchlist) AppleGold else Color(0xFF141000)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isInWatchlist) "Added to Watchlist" else "Add to Watchlist",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        letterSpacing = (-0.1).sp
                    )
                }

                // Apple Inset Group: Alternate Available Providers
                if (availability != null) {
                    val outsideSubscriptions = (availability.flatrateProviders + availability.freeProviders)
                        .filter { !userSubscriptions.contains(it.providerId) }

                    if (outsideSubscriptions.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(AppleSecondaryBackground)
                                .border(0.6.dp, AppleBorder, RoundedCornerShape(14.dp))
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "OTHER STREAMING OPTIONS",
                                style = MaterialTheme.typography.labelSmall,
                                color = AppleLabelTertiary,
                                fontWeight = FontWeight.Bold
                            )

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                outsideSubscriptions.forEach { provider ->
                                    Row(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(AppleTertiaryBackground)
                                            .border(0.5.dp, AppleBorder, CircleShape)
                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        ProviderLogo(
                                            logoUrl = provider.logoUrl,
                                            providerName = provider.providerName,
                                            size = 18.dp
                                        )
                                        Text(
                                            text = provider.providerName,
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                            color = AppleLabelPrimary,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Synopsis Inset Group
                if (title.overview.isNotBlank()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(AppleSecondaryBackground)
                            .border(0.6.dp, AppleBorder, RoundedCornerShape(14.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "OVERVIEW",
                            style = MaterialTheme.typography.labelSmall,
                            color = AppleLabelTertiary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = title.overview,
                            style = MaterialTheme.typography.bodyMedium,
                            color = AppleLabelSecondary,
                            lineHeight = 21.sp,
                            fontSize = 13.5.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AppleVerdictCard(
    verdict: Verdict,
    onOpenLink: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AppleSecondaryBackground)
            .border(0.6.dp, AppleBorder, RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        VerdictBadge(
            verdict = verdict,
            isCompact = false
        )

        when (verdict) {
            is Verdict.Included -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ProviderLogo(
                        logoUrl = verdict.primaryProvider.logoUrl,
                        providerName = verdict.primaryProvider.providerName,
                        size = 36.dp
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Included with ${verdict.primaryProvider.providerName}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = AppleLabelPrimary
                        )
                        Text(
                            text = "Ready to stream at no additional cost",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppleGreen,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (!verdict.deepLink.isNullOrBlank()) {
                        Button(
                            onClick = { onOpenLink(verdict.deepLink) },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = AppleGold),
                            modifier = Modifier.applePressEffect(scaleDown = 0.92f, onClick = { onOpenLink(verdict.deepLink) })
                        ) {
                            Text("Open", fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = null,
                                modifier = Modifier.size(11.dp),
                                tint = Color.Black
                            )
                        }
                    }
                }
            }

            is Verdict.RentOrBuy -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ProviderLogo(
                        logoUrl = verdict.primaryProvider.logoUrl,
                        providerName = verdict.primaryProvider.providerName,
                        size = 36.dp
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Rent or Buy on ${verdict.primaryProvider.providerName}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = AppleLabelPrimary
                        )
                        Text(
                            text = verdict.priceDisplay ?: "Available on demand",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppleOrange,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (!verdict.deepLink.isNullOrBlank()) {
                        Button(
                            onClick = { onOpenLink(verdict.deepLink) },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = AppleTertiaryBackground),
                            modifier = Modifier
                                .border(0.5.dp, AppleBorder, CircleShape)
                                .applePressEffect(scaleDown = 0.92f, onClick = { onOpenLink(verdict.deepLink) })
                        ) {
                            Text("Store", fontSize = 12.sp, color = AppleLabelPrimary, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = null,
                                modifier = Modifier.size(11.dp),
                                tint = AppleLabelPrimary
                            )
                        }
                    }
                }
            }

            is Verdict.Unavailable -> {
                Text(
                    text = "This title is not currently streaming or available on digital stores in ${verdict.countryCode}.",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppleLabelSecondary
                )
            }

            is Verdict.Unknown -> {
                Text(
                    text = verdict.reason,
                    style = MaterialTheme.typography.bodySmall,
                    color = AppleLabelSecondary
                )
            }
        }
    }
}
