package com.example.ui.screens.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.CountryPresets
import com.example.domain.model.MediaType
import com.example.ui.components.AppleSegmentedControl
import com.example.ui.components.OnboardingDialog
import com.example.ui.components.ProviderLogo
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
fun SearchScreen(
    viewModel: SearchViewModel,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    onShowNotification: ((ImageVector, String, Color) -> Unit)? = null
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val mediaTypeFilter by viewModel.mediaTypeFilter.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val titleVerdicts by viewModel.titleVerdicts.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    val countryCode by viewModel.countryCode.collectAsStateWithLifecycle()
    val subscribedProviderIds by viewModel.subscribedProviderIds.collectAsStateWithLifecycle()
    val onboardingCompleted by viewModel.onboardingCompleted.collectAsStateWithLifecycle()
    val savedWatchlistIds by viewModel.savedWatchlistIds.collectAsStateWithLifecycle()

    val selectedTitle by viewModel.selectedTitle.collectAsStateWithLifecycle()
    val selectedAvailability by viewModel.selectedTitleAvailability.collectAsStateWithLifecycle()
    val isLoadingDetails by viewModel.isLoadingDetails.collectAsStateWithLifecycle()
    val isSelectedInWatchlist by viewModel.isSelectedInWatchlist.collectAsStateWithLifecycle()

    val focusManager = LocalFocusManager.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val currentCountry = CountryPresets.findByCode(countryCode)
    val subscribedPresets = currentCountry.popularProviders.filter {
        subscribedProviderIds.contains(it.id)
    }

    // Onboarding Dialog
    if (!onboardingCompleted) {
        OnboardingDialog(
            initialCountryCode = countryCode,
            initialSelectedProviders = subscribedProviderIds,
            onComplete = { country, subscriptions ->
                viewModel.completeOnboarding(country, subscriptions)
                onShowNotification?.invoke(Icons.Default.Movie, "Welcome to Where Can I Watch It!", AppleGold)
            },
            onDismiss = {
                viewModel.completeOnboarding(countryCode, subscribedProviderIds)
            }
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
            sheetState = sheetState
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppleBackground)
            .padding(horizontal = 16.dp)
            .testTag("search_screen")
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Apple Large Title Header + Country Pill
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Search",
                    style = MaterialTheme.typography.displayMedium,
                    color = AppleLabelPrimary
                )
                Text(
                    text = "Subscriptions-first streaming verdict",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppleLabelSecondary,
                    letterSpacing = (-0.1).sp
                )
            }

            // Apple Region Capsule Pill with spring compression
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(AppleSecondaryBackground)
                    .border(0.6.dp, AppleBorder, CircleShape)
                    .applePressEffect(scaleDown = 0.9f, onClick = onNavigateToSettings)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(text = currentCountry.flagEmoji, fontSize = 13.sp)
                Text(
                    text = currentCountry.code,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppleLabelPrimary,
                    letterSpacing = 0.2.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Apple Search Input Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = viewModel::onQueryChanged,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_text_input"),
            placeholder = {
                Text(
                    text = "Movies, shows, documentaries...",
                    color = AppleLabelTertiary,
                    fontSize = 14.5.sp,
                    letterSpacing = (-0.2).sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = AppleLabelSecondary,
                    modifier = Modifier.size(19.dp)
                )
            },
            trailingIcon = {
                AnimatedVisibility(
                    visible = searchQuery.isNotEmpty(),
                    enter = fadeIn(tween(150)) + scaleIn(initialScale = 0.8f),
                    exit = fadeOut(tween(150)) + scaleOut(targetScale = 0.8f)
                ) {
                    IconButton(
                        onClick = { viewModel.onQueryChanged("") },
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2C2C2E))
                            .applePressEffect(scaleDown = 0.85f) { viewModel.onQueryChanged("") }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = AppleLabelSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = AppleSecondaryBackground,
                unfocusedContainerColor = AppleSecondaryBackground,
                focusedBorderColor = AppleBorder,
                unfocusedBorderColor = AppleBorder,
                focusedTextColor = AppleLabelPrimary,
                unfocusedTextColor = AppleLabelPrimary,
                cursorColor = AppleGold
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Apple Segmented Control for Media Filters (All | Movies | TV Shows)
        val mediaFilterOptions = listOf(MediaType.ALL, MediaType.MOVIE, MediaType.TV)
        AppleSegmentedControl(
            items = mediaFilterOptions,
            selectedItem = mediaTypeFilter,
            onItemSelected = viewModel::onMediaTypeChanged,
            itemLabel = {
                when (it) {
                    MediaType.ALL -> "All Titles"
                    MediaType.MOVIE -> "Movies"
                    MediaType.TV -> "TV Shows"
                }
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // User's Subscriptions Quick Bar
        if (subscribedPresets.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(AppleSecondaryBackground)
                    .border(0.6.dp, AppleBorder, RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ACTIVE PLANS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppleGreen,
                    letterSpacing = 0.4.sp
                )
                Spacer(modifier = Modifier.width(10.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(subscribedPresets) { preset ->
                        ProviderLogo(
                            logoUrl = preset.logoUrl,
                            providerName = preset.name,
                            size = 20.dp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Content Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = AppleGold,
                            strokeWidth = 2.5.dp,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                errorMessage != null -> {
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
                                    imageVector = Icons.Default.SearchOff,
                                    contentDescription = null,
                                    tint = AppleLabelSecondary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Text(
                                text = "Search Unavailable",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = AppleLabelPrimary
                            )
                            Text(
                                text = errorMessage ?: "Please check network connection or verify your TMDB key in Settings.",
                                style = MaterialTheme.typography.bodySmall,
                                color = AppleLabelSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                searchQuery.isBlank() -> {
                    // Apple Minimalist Zero State
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(AppleSecondaryBackground)
                                    .border(0.6.dp, AppleBorder, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = AppleGold,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Text(
                                text = "Search Movies & TV Shows",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = (-0.2).sp
                                ),
                                color = AppleLabelPrimary
                            )
                            Text(
                                text = "Find out instantly whether titles are included in your ${currentCountry.name} active streaming catalog.",
                                style = MaterialTheme.typography.bodySmall,
                                color = AppleLabelSecondary,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                searchResults.isEmpty() -> {
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
                                text = "No Results for \"$searchQuery\"",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = AppleLabelPrimary
                            )
                            Text(
                                text = "Check the spelling or try searching for another title.",
                                style = MaterialTheme.typography.bodySmall,
                                color = AppleLabelSecondary
                            )
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(top = 4.dp, bottom = 18.dp)
                    ) {
                        items(searchResults, key = { "${it.id}_${it.mediaType}" }) { title ->
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
                    }
                }
            }
        }
    }
}
