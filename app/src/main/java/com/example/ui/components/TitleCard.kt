package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.domain.model.MediaType
import com.example.domain.model.Title
import com.example.domain.model.Verdict
import com.example.ui.theme.AppleBorder
import com.example.ui.theme.AppleGold
import com.example.ui.theme.AppleLabelPrimary
import com.example.ui.theme.AppleLabelSecondary
import com.example.ui.theme.AppleSecondaryBackground
import com.example.ui.theme.AppleTertiaryBackground

/**
 * Apple Inset Grouped Title Card.
 * Features tactile spring compression on press, poster thumbnail, and animated metadata.
 */
@Composable
fun TitleCard(
    title: Title,
    verdict: Verdict?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isInWatchlist: Boolean = false,
    onToggleWatchlist: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .testTag("title_card_${title.id}")
            .fillMaxWidth()
            .applePressEffect(scaleDown = 0.97f, alphaDown = 0.9f, onClick = onClick)
            .border(0.6.dp, AppleBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppleSecondaryBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // High-fidelity Poster with Apple continuous curvature
            Box(
                modifier = Modifier
                    .width(72.dp)
                    .aspectRatio(2f / 3f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AppleTertiaryBackground),
                contentAlignment = Alignment.Center
            ) {
                if (title.posterUrl != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(title.posterUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = title.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = if (title.mediaType == MediaType.TV) Icons.Default.Tv else Icons.Default.Movie,
                        contentDescription = null,
                        tint = AppleLabelSecondary.copy(alpha = 0.5f),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(13.dp))

            // Metadata & Content Details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = title.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            letterSpacing = (-0.25).sp
                        ),
                        color = AppleLabelPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    // Optional Quick-Action Watchlist Button with Spring Pulse
                    if (onToggleWatchlist != null) {
                        val bookmarkScale by animateFloatAsState(
                            targetValue = if (isInWatchlist) 1.2f else 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            label = "bookmark_spring_scale"
                        )
                        val iconColor by animateColorAsState(
                            targetValue = if (isInWatchlist) AppleGold else AppleLabelSecondary,
                            label = "bookmark_tint"
                        )

                        Box(
                            modifier = Modifier
                                .padding(start = 6.dp)
                                .size(28.dp)
                                .clip(CircleShape)
                                .applePressEffect(scaleDown = 0.82f, onClick = onToggleWatchlist)
                                .scale(bookmarkScale),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isInWatchlist) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = if (isInWatchlist) "Remove from Watchlist" else "Save to Watchlist",
                                tint = iconColor,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }
                }

                // Subtitle metadata: Year • Type • Rating
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (!title.releaseYear.isNullOrBlank()) {
                        Text(
                            text = title.releaseYear,
                            style = MaterialTheme.typography.bodySmall,
                            color = AppleLabelSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppleLabelSecondary.copy(alpha = 0.4f)
                        )
                    }

                    // Media Type Minimal Capsule
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp))
                            .background(AppleTertiaryBackground)
                            .padding(horizontal = 5.dp, vertical = 1.5.dp)
                    ) {
                        Text(
                            text = if (title.mediaType == MediaType.TV) "TV" else "MOVIE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppleLabelSecondary,
                            letterSpacing = 0.2.sp
                        )
                    }

                    if (title.voteAverage > 0.0) {
                        Spacer(modifier = Modifier.weight(1f))
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
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppleGold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Spring-revealed Verdict Badge
                AnimatedVisibility(
                    visible = verdict != null,
                    enter = fadeIn(
                        spring(dampingRatio = Spring.DampingRatioNoBouncy)
                    ) + slideInVertically(
                        spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                    ) { 15 } + scaleIn(initialScale = 0.92f)
                ) {
                    if (verdict != null) {
                        VerdictBadge(
                            verdict = verdict,
                            isCompact = true
                        )
                    }
                }
            }
        }
    }
}
