package com.example.ui.screens.watchlist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.local.entity.WatchlistEntity
import com.example.domain.model.Provider
import com.example.domain.model.Verdict
import com.example.ui.components.AppleSegmentedControl
import com.example.ui.components.TitleDetailsBottomSheet
import com.example.ui.components.VerdictBadge
import com.example.ui.components.applePressEffect
import com.example.ui.theme.AppleBackground
import com.example.ui.theme.AppleBorder
import com.example.ui.theme.AppleGold
import com.example.ui.theme.AppleLabelPrimary
import com.example.ui.theme.AppleLabelSecondary
import com.example.ui.theme.AppleLabelTertiary
import com.example.ui.theme.AppleRed
import com.example.ui.theme.AppleSecondaryBackground
import com.example.ui.theme.AppleTertiaryBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchlistScreen(
    viewModel: WatchlistViewModel,
    modifier: Modifier = Modifier,
    onShowNotification: ((ImageVector, String, Color) -> Unit)? = null,
    onNavigateToSearch: (() -> Unit)? = null
) {
    val watchlistItems by viewModel.watchlistItems.collectAsStateWithLifecycle()
    val filterStatus by viewModel.filterStatus.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val countryCode by viewModel.countryCode.collectAsStateWithLifecycle()
    val subscribedProviderIds by viewModel.subscribedProviderIds.collectAsStateWithLifecycle()

    val selectedTitle by viewModel.selectedTitle.collectAsStateWithLifecycle()
    val selectedVerdict by viewModel.selectedTitleVerdict.collectAsStateWithLifecycle()
    val selectedAvailability by viewModel.selectedTitleAvailability.collectAsStateWithLifecycle()
    val isLoadingDetails by viewModel.isLoadingDetails.collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (selectedTitle != null) {
        TitleDetailsBottomSheet(
            title = selectedTitle!!,
            verdict = selectedVerdict,
            availability = selectedAvailability,
            userSubscriptions = subscribedProviderIds,
            isInWatchlist = true,
            isLoadingVerdict = isLoadingDetails,
            onToggleWatchlist = {
                viewModel.toggleWatchlistForSelected()
                onShowNotification?.invoke(Icons.Default.DeleteOutline, "Removed from Watchlist", AppleLabelSecondary)
            },
            onDismiss = { viewModel.closeDetails() },
            sheetState = sheetState
        )
    }

    val filteredList = when (filterStatus) {
        "INCLUDED" -> watchlistItems.filter { it.verdictType == "INCLUDED" }
        "RENT_BUY" -> watchlistItems.filter { it.verdictType == "RENT_BUY" }
        "UNAVAILABLE" -> watchlistItems.filter { it.verdictType == "UNAVAILABLE" }
        else -> watchlistItems
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppleBackground)
            .padding(horizontal = 16.dp)
            .testTag("watchlist_screen")
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Apple Large Title Header with Refresh Action
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Watchlist",
                    style = MaterialTheme.typography.displayMedium,
                    color = AppleLabelPrimary
                )
                Text(
                    text = "${watchlistItems.size} saved title${if (watchlistItems.size == 1) "" else "s"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppleLabelSecondary,
                    letterSpacing = (-0.1).sp
                )
            }

            // Apple Refresh Button with Animated Rotation
            val refreshRotation by animateFloatAsState(
                targetValue = if (isRefreshing) 360f else 0f,
                animationSpec = if (isRefreshing) tween(800, easing = FastOutSlowInEasing) else spring(),
                label = "refresh_rotation"
            )

            IconButton(
                onClick = {
                    viewModel.refreshAllWatchlistVerdicts()
                    onShowNotification?.invoke(Icons.Default.Refresh, "Checking latest streaming availability...", AppleGold)
                },
                enabled = !isRefreshing && watchlistItems.isNotEmpty(),
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(AppleSecondaryBackground)
                    .border(0.6.dp, AppleBorder, CircleShape)
                    .applePressEffect(scaleDown = 0.85f) {
                        viewModel.refreshAllWatchlistVerdicts()
                    }
            ) {
                if (isRefreshing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = AppleGold,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh availability",
                        tint = if (watchlistItems.isNotEmpty()) AppleGold else AppleLabelTertiary,
                        modifier = Modifier
                            .size(18.dp)
                            .rotate(refreshRotation)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Apple Segmented Control for Watchlist Status Filter
        val statusOptions = listOf("ALL", "INCLUDED", "RENT_BUY")
        val currentStatus = filterStatus ?: "ALL"
        AppleSegmentedControl(
            items = statusOptions,
            selectedItem = currentStatus,
            onItemSelected = { viewModel.setFilter(it) },
            itemLabel = { status: String ->
                when (status) {
                    "ALL" -> "All (${watchlistItems.size})"
                    "INCLUDED" -> "Included (${watchlistItems.count { it.verdictType == "INCLUDED" }})"
                    "RENT_BUY" -> "Rent/Buy"
                    else -> status
                }
            }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Watchlist Content Area
        if (watchlistItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(AppleSecondaryBackground)
                            .border(0.6.dp, AppleBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            tint = AppleGold,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Text(
                        text = "Your Watchlist is Empty",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.2).sp
                        ),
                        color = AppleLabelPrimary
                    )

                    Text(
                        text = "Save movies and shows from Search or Discover to track whether they become available on your subscriptions.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppleLabelSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 19.sp
                    )

                    if (onNavigateToSearch != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Button(
                            onClick = onNavigateToSearch,
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = AppleGold),
                            modifier = Modifier.applePressEffect(scaleDown = 0.94f, onClick = onNavigateToSearch)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = Color(0xFF141000),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Explore Titles",
                                color = Color(0xFF141000),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp
                            )
                        }
                    }
                }
            }
        } else if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No titles match this filter.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppleLabelSecondary
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(top = 4.dp, bottom = 20.dp)
            ) {
                items(filteredList, key = { "${it.id}_${it.mediaType}" }) { item ->
                    WatchlistItemCard(
                        item = item,
                        countryCode = countryCode,
                        onClick = { viewModel.openDetails(item) },
                        onRemove = {
                            viewModel.removeItem(item)
                            onShowNotification?.invoke(Icons.Default.DeleteOutline, "Removed ${item.title}", AppleLabelSecondary)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun WatchlistItemCard(
    item: WatchlistEntity,
    countryCode: String,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    val posterUrl = item.posterPath?.let {
        if (it.startsWith("http")) it else "https://image.tmdb.org/t/p/w500$it"
    }

    val itemVerdict: Verdict = when (item.verdictType) {
        "INCLUDED" -> {
            val provider = Provider(0, item.matchedProviderName ?: "Subscription", item.matchedProviderLogo)
            Verdict.Included(primaryProvider = provider, deepLink = item.deepLink)
        }
        "RENT_BUY" -> {
            val provider = Provider(0, item.matchedProviderName ?: "Store", item.matchedProviderLogo)
            Verdict.RentOrBuy(primaryProvider = provider, deepLink = item.deepLink)
        }
        "UNAVAILABLE" -> Verdict.Unavailable(countryCode = countryCode)
        else -> Verdict.Unknown(reason = "Status unverified")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .applePressEffect(scaleDown = 0.97f, onClick = onClick)
            .border(0.6.dp, AppleBorder, RoundedCornerShape(16.dp))
            .testTag("watchlist_item_${item.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppleSecondaryBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Poster
            Box(
                modifier = Modifier
                    .width(66.dp)
                    .aspectRatio(2f / 3f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AppleTertiaryBackground),
                contentAlignment = Alignment.Center
            ) {
                if (posterUrl != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(posterUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = item.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        letterSpacing = (-0.2).sp
                    ),
                    color = AppleLabelPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (!item.releaseYear.isNullOrBlank()) {
                    Text(
                        text = item.releaseYear,
                        style = MaterialTheme.typography.bodySmall,
                        color = AppleLabelSecondary,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                VerdictBadge(
                    verdict = itemVerdict,
                    isCompact = true
                )
            }

            // Quick Remove Button with Apple Press Effect
            IconButton(
                onClick = onRemove,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .applePressEffect(scaleDown = 0.82f, onClick = onRemove)
                    .testTag("remove_watchlist_${item.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Remove from watchlist",
                    tint = AppleLabelTertiary,
                    modifier = Modifier.size(19.dp)
                )
            }
        }
    }
}
