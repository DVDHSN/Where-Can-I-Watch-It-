package com.example.ui.screens.watchlist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.ui.components.bouncyClickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.graphics.Color
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
import com.example.ui.components.TitleDetailsBottomSheet
import com.example.ui.components.VerdictBadge
import com.example.ui.theme.CinemaBackground
import com.example.ui.theme.CinemaBorder
import com.example.ui.theme.CinemaPrimary
import com.example.ui.theme.CinemaSurface
import com.example.ui.theme.CinemaSurfaceVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchlistScreen(
    viewModel: WatchlistViewModel,
    modifier: Modifier = Modifier
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
            onToggleWatchlist = { viewModel.toggleWatchlistForSelected() },
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
            .background(CinemaBackground)
            .padding(horizontal = 16.dp)
            .testTag("watchlist_screen")
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Header with Refresh action
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "My Watchlist",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${watchlistItems.size} saved title${if (watchlistItems.size == 1) "" else "s"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = { viewModel.refreshAllWatchlistVerdicts() },
                enabled = !isRefreshing && watchlistItems.isNotEmpty(),
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(CinemaSurfaceVariant)
            ) {
                if (isRefreshing) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = CinemaPrimary, strokeWidth = 2.dp)
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh availability",
                        tint = CinemaPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Status Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatusFilterChip(
                label = "All (${watchlistItems.size})",
                isSelected = filterStatus == "ALL",
                onClick = { viewModel.setFilter("ALL") }
            )
            StatusFilterChip(
                label = "Included (${watchlistItems.count { it.verdictType == "INCLUDED" }})",
                isSelected = filterStatus == "INCLUDED",
                onClick = { viewModel.setFilter("INCLUDED") }
            )
            StatusFilterChip(
                label = "Rent / Buy",
                isSelected = filterStatus == "RENT_BUY",
                onClick = { viewModel.setFilter("RENT_BUY") }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // List Content
        if (watchlistItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(56.dp)
                    )
                    Text(
                        text = "Your Watchlist is empty",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Save movies and shows from Search or Discover to track whether they become included with your subscriptions.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(filteredList, key = { "${it.id}_${it.mediaType}" }) { item ->
                    WatchlistItemCard(
                        item = item,
                        countryCode = countryCode,
                        onClick = { viewModel.openDetails(item) },
                        onRemove = { viewModel.removeItem(item) }
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
        else -> Verdict.Unknown(reason = "Status unknown")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .bouncyClickable(onClick = onClick)
            .border(0.8.dp, CinemaBorder, RoundedCornerShape(16.dp))
            .testTag("watchlist_item_${item.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CinemaSurface)
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
                    .width(68.dp)
                    .aspectRatio(2f / 3f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CinemaSurfaceVariant),
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
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (!item.releaseYear.isNullOrBlank()) {
                    Text(
                        text = item.releaseYear,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                VerdictBadge(
                    verdict = itemVerdict,
                    isCompact = true
                )
            }

            IconButton(
                onClick = onRemove,
                modifier = Modifier.testTag("remove_watchlist_${item.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Remove from watchlist",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun StatusFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(label, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = CinemaPrimary,
            selectedLabelColor = Color.Black,
            containerColor = CinemaSurface,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = isSelected,
            borderColor = if (isSelected) CinemaPrimary else CinemaBorder
        ),
        modifier = Modifier.bouncyClickable(onClick = onClick)
    )
}
