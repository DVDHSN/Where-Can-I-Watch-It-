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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.MediaType
import com.example.ui.components.AppleSegmentedControl
import com.example.ui.components.applePressEffect
import com.example.ui.theme.AppleBorder
import com.example.ui.theme.AppleGold
import com.example.ui.theme.AppleGreen
import com.example.ui.theme.AppleLabelPrimary
import com.example.ui.theme.AppleLabelSecondary
import com.example.ui.theme.AppleLabelTertiary
import com.example.ui.theme.AppleSecondaryBackground
import com.example.ui.theme.AppleTertiaryBackground

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
        containerColor = Color(0xFF161618),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(38.dp)
                    .height(4.5.dp)
                    .clip(CircleShape)
                    .background(Color(0x40FFFFFF))
            )
        },
        modifier = modifier.testTag("discover_filter_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 6.dp)
                .padding(bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Filters",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = AppleLabelPrimary,
                        letterSpacing = (-0.3).sp
                    )
                    Text(
                        text = "$matchingCount titles match current criteria",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppleLabelSecondary
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0x33FFFFFF))
                        .applePressEffect(scaleDown = 0.85f, onClick = onDismiss)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = AppleLabelPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // SECTION 1: Media Type Segmented Control
            AppleSectionLabel(title = "MEDIA TYPE")
            val mediaTypes = listOf(MediaType.ALL, MediaType.MOVIE, MediaType.TV)
            AppleSegmentedControl(
                items = mediaTypes,
                selectedItem = filterState.mediaType,
                onItemSelected = { onFilterChanged(filterState.copy(mediaType = it)) },
                itemLabel = {
                    when (it) {
                        MediaType.ALL -> "All Titles"
                        MediaType.MOVIE -> "Movies"
                        MediaType.TV -> "TV Shows"
                    }
                }
            )

            // SECTION 2: Subscriptions Toggle Inset Row
            AppleSectionLabel(title = "STREAMING SUBSCRIPTIONS")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(AppleSecondaryBackground)
                    .border(0.6.dp, AppleBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Included with My Subscriptions",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = AppleLabelPrimary
                    )
                    Text(
                        text = "Hide titles requiring rental or purchase",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppleLabelSecondary
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Switch(
                    checked = filterState.onlySubscribed,
                    onCheckedChange = { onFilterChanged(filterState.copy(onlySubscribed = it)) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AppleGreen,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = AppleTertiaryBackground
                    )
                )
            }

            // SECTION 3: Genres
            AppleSectionLabel(title = "GENRES")
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                genresList.forEach { genre ->
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
                            .applePressEffect(scaleDown = 0.92f) {
                                onFilterChanged(filterState.copy(selectedGenre = genre))
                            }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = genre,
                            color = if (isSelected) Color(0xFF141000) else AppleLabelPrimary,
                            fontSize = 12.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            // SECTION 4: Release Year
            AppleSectionLabel(title = "RELEASE PERIOD")
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                YearFilter.entries.forEach { year ->
                    val isSelected = filterState.yearFilter == year
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isSelected) AppleGold else AppleSecondaryBackground)
                            .border(
                                0.6.dp,
                                if (isSelected) AppleGold else AppleBorder,
                                CircleShape
                            )
                            .applePressEffect(scaleDown = 0.92f) {
                                onFilterChanged(filterState.copy(yearFilter = year))
                            }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = year.label,
                            color = if (isSelected) Color(0xFF141000) else AppleLabelPrimary,
                            fontSize = 12.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            // SECTION 5: Minimum Rating
            AppleSectionLabel(title = "MINIMUM RATING")
            val ratingOptions = RatingFilter.entries
            AppleSegmentedControl(
                items = ratingOptions,
                selectedItem = filterState.ratingFilter,
                onItemSelected = { onFilterChanged(filterState.copy(ratingFilter = it)) },
                itemLabel = { it.label }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons (Reset and Done)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onResetFilters,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .applePressEffect(scaleDown = 0.95f, onClick = onResetFilters),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = AppleSecondaryBackground),
                    border = androidx.compose.foundation.BorderStroke(0.6.dp, AppleBorder)
                ) {
                    Text(
                        text = "Reset All",
                        color = AppleLabelPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .applePressEffect(scaleDown = 0.95f, onClick = onDismiss),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = AppleGold)
                ) {
                    Text(
                        text = "Apply Filters",
                        color = Color(0xFF141000),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun AppleSectionLabel(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = AppleLabelTertiary,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp
    )
}
