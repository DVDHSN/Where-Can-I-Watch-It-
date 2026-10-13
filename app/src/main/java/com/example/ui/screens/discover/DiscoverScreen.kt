package com.example.ui.screens.discover

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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.CountryPresets
import com.example.domain.model.MediaType
import com.example.ui.components.AppleSegmentedControl
import com.example.ui.components.TitleCard
import com.example.ui.components.TitleDetailsBottomSheet
import com.example.ui.components.applePressEffect
import com.example.ui.theme.AppleBackground
import com.example.ui.theme.AppleBorder
import com.example.ui.theme.AppleGold
import com.example.ui.theme.AppleGreen
import com.example.ui.theme.AppleLabelPrimary
import com.example.ui.theme.AppleLabelSecondary
import com.example.ui.theme.AppleLabelTertiary
import com.example.ui.theme.AppleSecondaryBackground
import com.example.ui.theme.AppleTertiaryBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(
    viewModel: DiscoverViewModel,
    modifier: Modifier = Modifier,
    onShowNotification: ((ImageVector, String, Color) -> Unit)? = null
) {
    val trendingTitles by viewModel.trendingTitles.collectAsStateWithLifecycle()
    val filteredTitles by viewModel.filteredTitles.collectAsStateWithLifecycle()
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()
    val titleVerdicts by viewModel.titleVerdicts.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isLoadingMore by viewModel.isLoadingMore.collectAsStateWithLifecycle()
    val canLoadMore by viewModel.canLoadMore.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    val countryCode by viewModel.countryCode.collectAsStateWithLifecycle()
    val subscribedProviderIds by viewModel.subscribedProviderIds.collectAsStateWithLifecycle()
    val savedWatchlistIds by viewModel.savedWatchlistIds.collectAsStateWithLifecycle()

    val selectedTitle by viewModel.selectedTitle.collectAsStateWithLifecycle()
    val selectedAvailability by viewModel.selectedTitleAvailability.collectAsStateWithLifecycle()
    val isLoadingDetails by viewModel.isLoadingDetails.collectAsStateWithLifecycle()
    val isSelectedInWatchlist by viewModel.isSelectedInWatchlist.collectAsStateWithLifecycle()

    var showFilterSheet by remember { mutableStateOf(false) }
    val filterSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val detailsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val currentCountry = CountryPresets.findByCode(countryCode)

    val quickGenres = listOf("All", "Action", "Comedy", "Drama", "Sci-Fi", "Horror", "Animation")

    // Filter Bottom Sheet
    if (showFilterSheet) {
        DiscoverFilterBottomSheet(
            filterState = filterState,
            onFilterChanged = { newState ->
                viewModel.setMediaType(newState.mediaType)
                viewModel.setGenre(newState.selectedGenre)
                viewModel.setYearFilter(newState.yearFilter)
                viewModel.setAvailabilityFilter(newState.availabilityFilter)
                viewModel.setRatingFilter(newState.ratingFilter)
            },
            onResetFilters = {
                viewModel.resetFilters()
                onShowNotification?.invoke(Icons.Default.Tune, "Filters reset", AppleLabelSecondary)
            },
            onDismiss = { showFilterSheet = false },
            sheetState = filterSheetState,
            matchingCount = filteredTitles.size
        )
    }

    // Title Details Sheet
    if (selectedTitle != null) {
        TitleDetailsBottomSheet(
            title = selectedTitle!!,
            verdict = titleVerdicts[selectedTitle!!.id],
            availability = selectedAvailability,
            userSubscriptions = subscribedProviderIds,
            isInWatchlist = isSelectedInWatchlist,
            isLoadingVerdict = isLoadingDetails,
            onToggleWatchlist = {
                val wasInWatchlist = isSelectedInWatchlist
                viewModel.toggleWatchlistForSelected()
                if (!wasInWatchlist) {
                    onShowNotification?.invoke(Icons.Default.Bookmark, "Saved to Watchlist", AppleGold)
                } else {
                    onShowNotification?.invoke(Icons.Default.Bookmark, "Removed from Watchlist", AppleLabelSecondary)
                }
            },
            onDismiss = { viewModel.closeTitleDetails() },
            sheetState = detailsSheetState
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppleBackground)
            .padding(horizontal = 16.dp)
            .testTag("discover_screen")
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Header: Apple Large Title + Animated Refresh
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Discover",
                    style = MaterialTheme.typography.displayMedium,
                    color = AppleLabelPrimary
                )
                Text(
                    text = "Trending in ${currentCountry.flagEmoji} ${currentCountry.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppleLabelSecondary,
                    letterSpacing = (-0.1).sp
                )
            }

            val refreshRotation by animateFloatAsState(
                targetValue = if (isLoading) 360f else 0f,
                animationSpec = if (isLoading) tween(800, easing = FastOutSlowInEasing) else spring(),
                label = "discover_refresh_rotation"
            )

            IconButton(
                onClick = {
                    viewModel.loadTrending()
                    onShowNotification?.invoke(Icons.Default.Refresh, "Refreshing trending titles...", AppleGold)
                },
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(AppleSecondaryBackground)
                    .border(0.6.dp, AppleBorder, CircleShape)
                    .applePressEffect(scaleDown = 0.85f) { viewModel.loadTrending() }
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh trending",
                    tint = AppleGold,
                    modifier = Modifier
                        .size(18.dp)
                        .rotate(refreshRotation)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Apple Segmented Control for Media Type (All | Movies | TV Shows)
        val mediaTypes = listOf(MediaType.ALL, MediaType.MOVIE, MediaType.TV)
        AppleSegmentedControl(
            items = mediaTypes,
            selectedItem = filterState.mediaType,
            onItemSelected = viewModel::setMediaType,
            itemLabel = {
                when (it) {
                    MediaType.ALL -> "All Trending"
                    MediaType.MOVIE -> "Movies"
                    MediaType.TV -> "TV Shows"
                }
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Actions Bar: Subscriptions Only Pill + Filters Modal Pill
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Subscriptions Only Toggle Pill
            val isSubsOnly = filterState.onlySubscribed
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (isSubsOnly) AppleGreen.copy(alpha = 0.16f) else AppleSecondaryBackground)
                    .border(
                        0.6.dp,
                        if (isSubsOnly) AppleGreen else AppleBorder,
                        CircleShape
                    )
                    .applePressEffect(scaleDown = 0.94f) {
                        viewModel.toggleOnlySubscribed()
                        if (!isSubsOnly) {
                            onShowNotification?.invoke(Icons.Default.CheckCircle, "Showing your active subscriptions only", AppleGreen)
                        }
                    }
                    .padding(horizontal = 12.dp, vertical = 7.dp)
                    .testTag("filter_subscribed_only_button"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = if (isSubsOnly) Icons.Default.CheckCircle else Icons.Default.Check,
                    contentDescription = null,
                    tint = if (isSubsOnly) AppleGreen else AppleLabelSecondary,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = "My Subscriptions Only",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSubsOnly) AppleGreen else AppleLabelPrimary
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Filters Modal Trigger with Active Count Pill
            val hasActiveFilters = filterState.activeFilterCount > 0
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (hasActiveFilters) AppleGold.copy(alpha = 0.16f) else AppleSecondaryBackground)
                    .border(
                        0.6.dp,
                        if (hasActiveFilters) AppleGold else AppleBorder,
                        CircleShape
                    )
                    .applePressEffect(scaleDown = 0.94f) { showFilterSheet = true }
                    .padding(horizontal = 12.dp, vertical = 7.dp)
                    .testTag("open_filters_sheet_button"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Filters",
                    tint = if (hasActiveFilters) AppleGold else AppleLabelSecondary,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = if (hasActiveFilters) "Filters (${filterState.activeFilterCount})" else "Filters",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (hasActiveFilters) AppleGold else AppleLabelPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Horizontal Apple Genre Capsules
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(quickGenres) { genre ->
                val isSelected = filterState.selectedGenre.equals(genre, ignoreCase = true)
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isSelected) AppleGold else AppleSecondaryBackground)
                        .border(
                            0.6.dp,
                            if (isSelected) AppleGold else AppleBorder,
                            CircleShape
                        )
                        .applePressEffect(scaleDown = 0.92f) { viewModel.setGenre(genre) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = genre,
                        color = if (isSelected) Color(0xFF141000) else AppleLabelSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Active Filter Summary
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filteredTitles.size} title${if (filteredTitles.size == 1) "" else "s"}",
                fontSize = 12.sp,
                color = AppleLabelTertiary,
                fontWeight = FontWeight.Medium
            )

            if (filterState.activeFilterCount > 0) {
                Text(
                    text = "Reset filters",
                    fontSize = 12.sp,
                    color = AppleGold,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .applePressEffect(scaleDown = 0.95f) { viewModel.resetFilters() }
                        .padding(vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // List Content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when {
                isLoading && trendingTitles.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AppleGold, strokeWidth = 2.5.dp)
                    }
                }

                errorMessage != null && trendingTitles.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Unable to fetch discover titles",
                                style = MaterialTheme.typography.titleMedium,
                                color = AppleLabelPrimary
                            )
                            Text(
                                text = errorMessage ?: "Please check your network connection.",
                                style = MaterialTheme.typography.bodySmall,
                                color = AppleLabelSecondary
                            )
                        }
                    }
                }

                filteredTitles.isEmpty() -> {
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
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(AppleSecondaryBackground)
                                    .border(0.6.dp, AppleBorder, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = null,
                                    tint = AppleLabelSecondary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Text(
                                text = "No titles match these filters",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = AppleLabelPrimary
                            )
                            Text(
                                text = if (filterState.onlySubscribed)
                                    "None of the current trending titles are included with your active subscriptions in ${currentCountry.name}. Try turning off 'My Subscriptions Only' or adjusting services in Settings."
                                else
                                    "Try clearing genre or rating filters to see more trending titles.",
                                style = MaterialTheme.typography.bodySmall,
                                color = AppleLabelSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(top = 4.dp, bottom = 20.dp)
                    ) {
                        items(filteredTitles, key = { "${it.id}_${it.mediaType}" }) { title ->
                            val isSaved = savedWatchlistIds.contains(title.id)
                            TitleCard(
                                title = title,
                                verdict = titleVerdicts[title.id],
                                isInWatchlist = isSaved,
                                onToggleWatchlist = {
                                    viewModel.toggleWatchlist(title)
                                    if (!isSaved) {
                                        onShowNotification?.invoke(Icons.Default.Bookmark, "Saved ${title.title}", AppleGold)
                                    } else {
                                        onShowNotification?.invoke(Icons.Default.Bookmark, "Removed ${title.title}", AppleLabelSecondary)
                                    }
                                },
                                onClick = { viewModel.openTitleDetails(title) }
                            )
                        }

                        // Load More Footer for filtered results
                        if (canLoadMore && (filterState.activeFilterCount > 0 || filterState.onlySubscribed)) {
                            item(key = "load_more_footer") {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isLoadingMore) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            CircularProgressIndicator(
                                                color = AppleGold,
                                                strokeWidth = 2.dp,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = "Fetching more eligible titles...",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = AppleLabelSecondary
                                            )
                                        }
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .background(AppleSecondaryBackground)
                                                .border(0.6.dp, AppleBorder, CircleShape)
                                                .applePressEffect(scaleDown = 0.94f) {
                                                    viewModel.loadMoreTitles()
                                                }
                                                .padding(horizontal = 20.dp, vertical = 10.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "Load More Eligible Titles",
                                                color = AppleGold,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
