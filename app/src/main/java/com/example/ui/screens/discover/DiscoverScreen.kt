package com.example.ui.screens.discover

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.CountryPresets
import com.example.domain.model.MediaType
import com.example.ui.components.TitleCard
import com.example.ui.components.TitleDetailsBottomSheet
import com.example.ui.theme.CinemaBackground
import com.example.ui.theme.CinemaBorder
import com.example.ui.theme.CinemaPrimary
import com.example.ui.theme.CinemaSurface
import com.example.ui.theme.CinemaSurfaceVariant
import com.example.ui.theme.VerdictIncluded

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(
    viewModel: DiscoverViewModel,
    modifier: Modifier = Modifier
) {
    val trendingTitles by viewModel.trendingTitles.collectAsStateWithLifecycle()
    val filteredTitles by viewModel.filteredTitles.collectAsStateWithLifecycle()
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()
    val titleVerdicts by viewModel.titleVerdicts.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    val countryCode by viewModel.countryCode.collectAsStateWithLifecycle()
    val subscribedProviderIds by viewModel.subscribedProviderIds.collectAsStateWithLifecycle()

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
            onResetFilters = { viewModel.resetFilters() },
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
            onToggleWatchlist = { viewModel.toggleWatchlistForSelected() },
            onDismiss = { viewModel.closeTitleDetails() },
            sheetState = detailsSheetState
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CinemaBackground)
            .padding(horizontal = 16.dp)
            .testTag("discover_screen")
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Whatshot,
                        contentDescription = null,
                        tint = CinemaPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Discover",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Text(
                    text = "Streaming options in ${currentCountry.flagEmoji} ${currentCountry.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = { viewModel.loadTrending() },
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(CinemaSurfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh trending",
                    tint = CinemaPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // QUICK FILTER ACTION BAR
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // "Only In Subscriptions" Toggle Pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (filterState.onlySubscribed) VerdictIncluded.copy(alpha = 0.2f) else CinemaSurface)
                    .border(
                        1.dp,
                        if (filterState.onlySubscribed) VerdictIncluded else CinemaBorder,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { viewModel.toggleOnlySubscribed() }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .testTag("filter_subscribed_only_button"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = if (filterState.onlySubscribed) Icons.Default.CheckCircle else Icons.Default.Check,
                    contentDescription = null,
                    tint = if (filterState.onlySubscribed) VerdictIncluded else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "My Subscriptions Only",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (filterState.onlySubscribed) VerdictIncluded else MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // More Filters Modal Button
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (filterState.activeFilterCount > 0) CinemaPrimary.copy(alpha = 0.2f) else CinemaSurface)
                    .border(
                        1.dp,
                        if (filterState.activeFilterCount > 0) CinemaPrimary else CinemaBorder,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { showFilterSheet = true }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .testTag("open_filters_sheet_button"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filters",
                    tint = if (filterState.activeFilterCount > 0) CinemaPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = if (filterState.activeFilterCount > 0) "Filters (${filterState.activeFilterCount})" else "Filters",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (filterState.activeFilterCount > 0) CinemaPrimary else MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // HORIZONTAL QUICK CHIPS (Media Type & Top Genres)
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Type chips
            item {
                QuickFilterChip(
                    label = "All Types",
                    isSelected = filterState.mediaType == MediaType.ALL,
                    onClick = { viewModel.setMediaType(MediaType.ALL) }
                )
            }
            item {
                QuickFilterChip(
                    label = "Movies",
                    isSelected = filterState.mediaType == MediaType.MOVIE,
                    onClick = { viewModel.setMediaType(MediaType.MOVIE) }
                )
            }
            item {
                QuickFilterChip(
                    label = "TV Series",
                    isSelected = filterState.mediaType == MediaType.TV,
                    onClick = { viewModel.setMediaType(MediaType.TV) }
                )
            }

            // Divider dot
            item {
                Text(
                    text = "•",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            // Genre quick chips
            items(quickGenres) { genre ->
                QuickFilterChip(
                    label = genre,
                    isSelected = filterState.selectedGenre.equals(genre, ignoreCase = true),
                    onClick = { viewModel.setGenre(genre) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Active Filter Summary / Result Count
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filteredTitles.size} title${if (filteredTitles.size == 1) "" else "s"} found",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )

            if (filterState.activeFilterCount > 0) {
                Text(
                    text = "Clear filters",
                    fontSize = 12.sp,
                    color = CinemaPrimary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clickable(onClick = { viewModel.resetFilters() })
                        .padding(vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

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
                        CircularProgressIndicator(color = CinemaPrimary)
                    }
                }

                errorMessage != null && trendingTitles.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Unable to fetch discover titles",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = errorMessage ?: "Please check your network connection.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(52.dp)
                            )
                            Text(
                                text = "No titles match these filters",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (filterState.onlySubscribed)
                                    "None of the current trending titles are included with your active subscriptions in ${currentCountry.name}. Try toggling 'My Subscriptions Only' off or adding more services in Settings."
                                else
                                    "Try loosening your genre, year, or rating filters to see more results.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Button(
                                onClick = { viewModel.resetFilters() },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CinemaSurfaceVariant)
                            ) {
                                Text("Reset All Filters", color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(filteredTitles, key = { "${it.id}_${it.mediaType}" }) { title ->
                            TitleCard(
                                title = title,
                                verdict = titleVerdicts[title.id],
                                onClick = { viewModel.openTitleDetails(title) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
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
        )
    )
}
