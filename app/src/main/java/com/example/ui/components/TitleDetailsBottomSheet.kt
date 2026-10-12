package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.OutlinedButton
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
import com.example.ui.theme.CinemaBorder
import com.example.ui.theme.CinemaPrimary
import com.example.ui.theme.CinemaSurface
import com.example.ui.theme.CinemaSurfaceVariant
import com.example.ui.theme.VerdictIncluded
import com.example.ui.theme.VerdictRentBuy

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
        containerColor = CinemaSurface,
        modifier = modifier.testTag("title_details_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            // Header with backdrop
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .background(CinemaSurfaceVariant)
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
                            .height(210.dp)
                    )
                }

                // Smooth cinematic dark gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xCC090A0E), CinemaSurface)
                            )
                        )
                )

                // Close Button with spring hover
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(14.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .bouncyClickable(onClick = onDismiss)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            }

            // Main Content Body
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title and Meta row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Poster thumbnail with subtle border
                    Box(
                        modifier = Modifier
                            .width(84.dp)
                            .aspectRatio(2f / 3f)
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, CinemaBorder, RoundedCornerShape(10.dp))
                            .background(CinemaSurfaceVariant),
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
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = title.title,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.3).sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (!title.releaseYear.isNullOrBlank()) {
                                Text(
                                    text = title.releaseYear,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "•",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            )
                            Text(
                                text = if (title.mediaType == MediaType.TV) "TV Series" else "Movie",
                                style = MaterialTheme.typography.bodyMedium,
                                color = CinemaPrimary,
                                fontWeight = FontWeight.Bold
                            )

                            if (title.voteAverage > 0.0) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0x22FACC15))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = CinemaPrimary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = String.format("%.1f", title.voteAverage),
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = CinemaPrimary
                                    )
                                }
                            }
                        }
                    }
                }

                // PRIMARY VERDICT SECTION WITH ANIMATED STATE SWITCH
                AnimatedContent(
                    targetState = isLoadingVerdict,
                    transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(250)) },
                    label = "verdict_container_transition"
                ) { loading ->
                    if (loading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(CinemaSurfaceVariant)
                                .border(1.dp, CinemaBorder, RoundedCornerShape(14.dp))
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = CinemaPrimary,
                                    strokeWidth = 2.5.dp
                                )
                                Text(
                                    text = "Checking your subscriptions...",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    } else if (verdict != null) {
                        VerdictBannerCard(
                            verdict = verdict,
                            onOpenLink = { url ->
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            }
                        )
                    }
                }

                // Watchlist action button with tactile spring animation
                val bookmarkScale by animateFloatAsState(
                    targetValue = if (isInWatchlist) 1.08f else 1f,
                    animationSpec = spring(dampingRatio = 0.5f, stiffness = 500f),
                    label = "bookmark_spring"
                )
                val buttonBg by animateColorAsState(
                    targetValue = if (isInWatchlist) CinemaSurfaceVariant else CinemaPrimary,
                    animationSpec = tween(250),
                    label = "button_bg_color"
                )

                Button(
                    onClick = onToggleWatchlist,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("watchlist_toggle_button")
                        .bouncyClickable(onClick = onToggleWatchlist),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttonBg,
                        contentColor = if (isInWatchlist) MaterialTheme.colorScheme.onSurface else Color.Black
                    )
                ) {
                    Icon(
                        imageVector = if (isInWatchlist) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                            .scale(bookmarkScale),
                        tint = if (isInWatchlist) CinemaPrimary else Color.Black
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isInWatchlist) "Saved to Watchlist" else "Add to Watchlist",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                // "AVAILABLE OUTSIDE YOUR SUBSCRIPTIONS" (Secondary options)
                if (availability != null) {
                    val outsideSubscriptions = (availability.flatrateProviders + availability.freeProviders)
                        .filter { !userSubscriptions.contains(it.providerId) }

                    if (outsideSubscriptions.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .border(1.dp, CinemaBorder, RoundedCornerShape(14.dp))
                                .background(CinemaSurfaceVariant.copy(alpha = 0.4f))
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Available outside your subscriptions",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                outsideSubscriptions.forEach { provider ->
                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(CinemaSurface)
                                            .border(1.dp, CinemaBorder, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        ProviderLogo(
                                            logoUrl = provider.logoUrl,
                                            providerName = provider.providerName,
                                            size = 20.dp
                                        )
                                        Text(
                                            text = provider.providerName,
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Synopsis
                if (title.overview.isNotBlank()) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Synopsis",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = title.overview,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VerdictBannerCard(
    verdict: Verdict,
    onOpenLink: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CinemaSurfaceVariant)
            .border(1.dp, CinemaBorder, RoundedCornerShape(14.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
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
                            text = "Stream on ${verdict.primaryProvider.providerName}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Included with active plan",
                            style = MaterialTheme.typography.bodySmall,
                            color = VerdictIncluded,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (!verdict.deepLink.isNullOrBlank()) {
                        Button(
                            onClick = { onOpenLink(verdict.deepLink) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CinemaPrimary),
                            modifier = Modifier.bouncyClickable(onClick = { onOpenLink(verdict.deepLink) })
                        ) {
                            Text("Watch", fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
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
                            text = "Rent on ${verdict.primaryProvider.providerName}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = verdict.priceDisplay ?: "Rental options available",
                            style = MaterialTheme.typography.bodySmall,
                            color = VerdictRentBuy,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (!verdict.deepLink.isNullOrBlank()) {
                        OutlinedButton(
                            onClick = { onOpenLink(verdict.deepLink) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.bouncyClickable(onClick = { onOpenLink(verdict.deepLink) })
                        ) {
                            Text("View", fontSize = 12.sp, color = CinemaPrimary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = CinemaPrimary
                            )
                        }
                    }
                }
            }

            is Verdict.Unavailable -> {
                Text(
                    text = "No streaming, rental, or purchase options were found in ${verdict.countryCode}.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            is Verdict.Unknown -> {
                Text(
                    text = verdict.reason,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
