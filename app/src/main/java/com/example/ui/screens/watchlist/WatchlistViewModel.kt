package com.example.ui.screens.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.WatchlistEntity
import com.example.data.preferences.UserPreferencesRepository
import com.example.data.repository.TmdbRepository
import com.example.data.repository.WatchlistRepository
import com.example.domain.model.MediaType
import com.example.domain.model.RegionalAvailability
import com.example.domain.model.Title
import com.example.domain.model.Verdict
import com.example.domain.usecase.ResolveVerdictUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WatchlistViewModel(
    private val watchlistRepository: WatchlistRepository,
    private val tmdbRepository: TmdbRepository,
    private val preferencesRepository: UserPreferencesRepository,
    private val resolveVerdictUseCase: ResolveVerdictUseCase = ResolveVerdictUseCase()
) : ViewModel() {

    val watchlistItems: StateFlow<List<WatchlistEntity>> = watchlistRepository.allWatchlist
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val countryCode: StateFlow<String> = preferencesRepository.countryCode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "MY")

    val subscribedProviderIds: StateFlow<Set<Int>> = preferencesRepository.subscribedProviderIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), setOf(8, 337, 119, 350))

    private val _filterStatus = MutableStateFlow<String?>("ALL")
    val filterStatus: StateFlow<String?> = _filterStatus.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    // Title Details Sheet State
    private val _selectedTitle = MutableStateFlow<Title?>(null)
    val selectedTitle: StateFlow<Title?> = _selectedTitle.asStateFlow()

    private val _selectedTitleVerdict = MutableStateFlow<Verdict?>(null)
    val selectedTitleVerdict: StateFlow<Verdict?> = _selectedTitleVerdict.asStateFlow()

    private val _selectedTitleAvailability = MutableStateFlow<RegionalAvailability?>(null)
    val selectedTitleAvailability: StateFlow<RegionalAvailability?> = _selectedTitleAvailability.asStateFlow()

    private val _isLoadingDetails = MutableStateFlow(false)
    val isLoadingDetails: StateFlow<Boolean> = _isLoadingDetails.asStateFlow()

    fun setFilter(status: String?) {
        _filterStatus.value = status
    }

    fun removeItem(item: WatchlistEntity) {
        viewModelScope.launch {
            watchlistRepository.removeFromWatchlist(item.id, MediaType.fromApiValue(item.mediaType))
        }
    }

    fun refreshAllWatchlistVerdicts() {
        viewModelScope.launch {
            _isRefreshing.value = true
            val items = watchlistRepository.getAllWatchlistSync()
            val country = countryCode.value
            val userSubs = subscribedProviderIds.value

            items.forEach { item ->
                val mediaType = MediaType.fromApiValue(item.mediaType)
                val availResult = tmdbRepository.getWatchProviders(item.id, mediaType, country)
                availResult.onSuccess { availability ->
                    val verdict = resolveVerdictUseCase.execute(country, userSubs, availability)
                    watchlistRepository.updateVerdict(item.id, mediaType, verdict)
                }
            }
            _isRefreshing.value = false
        }
    }

    fun openDetails(item: WatchlistEntity) {
        val title = Title(
            id = item.id,
            title = item.title,
            mediaType = MediaType.fromApiValue(item.mediaType),
            posterPath = item.posterPath,
            backdropPath = null,
            releaseYear = item.releaseYear,
            overview = item.overview,
            voteAverage = item.voteAverage
        )
        _selectedTitle.value = title
        _isLoadingDetails.value = true

        viewModelScope.launch {
            val country = countryCode.value
            val userSubs = subscribedProviderIds.value

            val availResult = tmdbRepository.getWatchProviders(item.id, title.mediaType, country)
            availResult.fold(
                onSuccess = { availability ->
                    _selectedTitleAvailability.value = availability
                    val verdict = resolveVerdictUseCase.execute(country, userSubs, availability)
                    _selectedTitleVerdict.value = verdict
                    _isLoadingDetails.value = false
                },
                onFailure = {
                    _selectedTitleAvailability.value = null
                    _selectedTitleVerdict.value = Verdict.Unknown("Error retrieving details")
                    _isLoadingDetails.value = false
                }
            )
        }
    }

    fun closeDetails() {
        _selectedTitle.value = null
        _selectedTitleAvailability.value = null
        _selectedTitleVerdict.value = null
    }

    fun toggleWatchlistForSelected() {
        val title = _selectedTitle.value ?: return
        viewModelScope.launch {
            watchlistRepository.removeFromWatchlist(title.id, title.mediaType)
            closeDetails()
        }
    }
}
