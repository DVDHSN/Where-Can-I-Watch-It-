package com.example.ui.screens.discover

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import com.example.ui.components.bouncyClickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.Genres
import com.example.domain.model.MediaType
import com.example.ui.theme.CinemaBorder
import com.example.ui.theme.CinemaPrimary
import com.example.ui.theme.CinemaSurface
import com.example.ui.theme.CinemaSurfaceVariant
import com.example.ui.theme.VerdictIncluded
import com.example.ui.theme.VerdictRentBuy

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DiscoverFilterBottomSheet(
    filterState: DiscoverFilterState,
    onFilterChanged: (DiscoverFilterState) -> Unit,
    onResetFilters: () -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState,
    matchingCount: Int,
    modifier: Modifier = Modifier
) {
    val genresList = listOf(
        "All", "Action", "Comedy", "Drama", "Sci-Fi", "Horror",
        "Animation", "Romance", "Thriller", "Documentary", "Mystery", "Family"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CinemaSurface,
        modifier = modifier.testTag("discover_filter_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter Discover",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (filterState.activeFilterCount > 0) {
                    Text(
                        text = "Reset all",
                        color = CinemaPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clickable(onClick = onResetFilters)
                            .padding(4.dp)
                    )
                }
            }

            // FILTER 1: Only Subscribed
            FilterSectionHeader(title = "Streaming Availability", icon = Icons.Default.LiveTv)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CinemaSurfaceVariant)
                    .border(
                        1.dp,
                        if (filterState.onlySubscribed) VerdictIncluded.copy(alpha = 0.5f) else CinemaBorder,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable {
                        val newOnlySub = !filterState.onlySubscribed
                        onFilterChanged(
                            filterState.copy(
                                onlySubscribed = newOnlySub,
                                availabilityFilter = if (newOnlySub) AvailabilityFilter.SUBSCRIBED_ONLY else AvailabilityFilter.ALL
                            )
                        )
                    }
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Only in my subscriptions",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Show only titles included with your active services",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = filterState.onlySubscribed,
                    onCheckedChange = { checked ->
                        onFilterChanged(
                            filterState.copy(
                                onlySubscribed = checked,
                                availabilityFilter = if (checked) AvailabilityFilter.SUBSCRIBED_ONLY else AvailabilityFilter.ALL
                            )
                        )
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = VerdictIncluded
                    )
                )
            }

            // FILTER 2: Rent / Buy Option
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AvailabilityFilter.entries.forEach { option ->
                    val isSelected = filterState.availabilityFilter == option
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) CinemaSurfaceVariant else Color.Transparent)
                            .border(
                                1.dp,
                                if (isSelected) CinemaPrimary else CinemaBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                onFilterChanged(
                                    filterState.copy(
                                        availabilityFilter = option,
                                        onlySubscribed = (option == AvailabilityFilter.SUBSCRIBED_ONLY)
                                    )
                                )
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = option.label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) CinemaPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // FILTER 3: Movies / TV Shows
            FilterSectionHeader(title = "Content Type", icon = Icons.Default.Movie)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Pair("All Types", MediaType.ALL),
                    Pair("Movies", MediaType.MOVIE),
                    Pair("TV Shows", MediaType.TV)
                ).forEach { (label, type) ->
                    val isSelected = filterState.mediaType == type
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) CinemaSurfaceVariant else Color.Transparent)
                            .border(
                                1.dp,
                                if (isSelected) CinemaPrimary else CinemaBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { onFilterChanged(filterState.copy(mediaType = type)) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) CinemaPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // FILTER 4: Genre
            FilterSectionHeader(title = "Genre", icon = Icons.Default.Category)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                genresList.forEach { genreName ->
                    val isSelected = filterState.selectedGenre.equals(genreName, ignoreCase = true)
                    FilterChip(
                        selected = isSelected,
                        onClick = { onFilterChanged(filterState.copy(selectedGenre = genreName)) },
                        label = { Text(genreName, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CinemaPrimary,
                            selectedLabelColor = Color.Black,
                            containerColor = CinemaSurfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurface
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) CinemaPrimary else CinemaBorder
                        )
                    )
                }
            }

            // FILTER 5: Release Year
            FilterSectionHeader(title = "Release Year", icon = Icons.Default.CalendarMonth)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                YearFilter.entries.forEach { yearOpt ->
                    val isSelected = filterState.yearFilter == yearOpt
                    FilterChip(
                        selected = isSelected,
                        onClick = { onFilterChanged(filterState.copy(yearFilter = yearOpt)) },
                        label = { Text(yearOpt.label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CinemaPrimary,
                            selectedLabelColor = Color.Black,
                            containerColor = CinemaSurfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurface
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) CinemaPrimary else CinemaBorder
                        )
                    )
                }
            }

            // FILTER 6: Rating
            FilterSectionHeader(title = "Minimum Rating", icon = Icons.Default.Star)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RatingFilter.entries.forEach { ratingOpt ->
                    val isSelected = filterState.ratingFilter == ratingOpt
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) CinemaSurfaceVariant else Color.Transparent)
                            .border(
                                1.dp,
                                if (isSelected) Color(0xFFFBBF24) else CinemaBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { onFilterChanged(filterState.copy(ratingFilter = ratingOpt)) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = ratingOpt.label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color(0xFFFBBF24) else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Button
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .bouncyClickable(onClick = onDismiss)
                    .testTag("apply_discover_filters_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CinemaPrimary)
            ) {
                Text(
                    text = "Show $matchingCount Result${if (matchingCount == 1) "" else "s"}",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun FilterSectionHeader(title: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = CinemaPrimary,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
