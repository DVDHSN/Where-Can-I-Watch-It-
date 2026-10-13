package com.example.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.preferences.UserPreferencesRepository
import com.example.data.repository.TmdbRepository
import com.example.data.repository.WatchlistRepository
import com.example.domain.model.MediaType
import com.example.domain.model.RegionalAvailability
import com.example.domain.model.Title
import com.example.domain.model.Verdict
import com.example.domain.usecase.ResolveVerdictUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SearchViewModel(
    private val tmdbRepository: TmdbRepository,
    private val watchlistRepository: WatchlistRepository,
    private val preferencesRepository: UserPreferencesRepository,
    private val resolveVerdictUseCase: ResolveVerdictUseCase = ResolveVerdictUseCase()
) : ViewModel() {

    val countryCode: StateFlow<String> = preferencesRepository.countryCode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "MY")

    val subscribedProviderIds: StateFlow<Set<Int>> = preferencesRepository.subscribedProviderIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), setOf(8, 337, 119, 350))

    val savedWatchlistIds: StateFlow<Set<Int>> = watchlistRepository.allWatchlist
        .map { list -> list.map { it.id }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val onboardingCompleted: StateFlow<Boolean> = preferencesRepository.onboardingCompleted
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _mediaTypeFilter = MutableStateFlow(MediaType.ALL)
    val mediaTypeFilter: StateFlow<MediaType> = _mediaTypeFilter.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Title>>(emptyList())
    val searchResults: StateFlow<List<Title>> = _searchResults.asStateFlow()

    private val _titleVerdicts = MutableStateFlow<Map<Int, Verdict>>(emptyMap())
    val titleVerdicts: StateFlow<Map<Int, Verdict>> = _titleVerdicts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Title Details Sheet State
    private val _selectedTitle = MutableStateFlow<Title?>(null)
    val selectedTitle: StateFlow<Title?> = _selectedTitle.asStateFlow()

    private val _selectedTitleAvailability = MutableStateFlow<RegionalAvailability?>(null)
    val selectedTitleAvailability: StateFlow<RegionalAvailability?> = _selectedTitleAvailability.asStateFlow()

    private val _isLoadingDetails = MutableStateFlow(false)
    val isLoadingDetails: StateFlow<Boolean> = _isLoadingDetails.asStateFlow()

    val isSelectedInWatchlist: StateFlow<Boolean> = _selectedTitle
        .flatMapLatest { title ->
            if (title != null) {
                watchlistRepository.isInWatchlist(title.id, title.mediaType)
            } else {
                flowOf(false)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        // Debounce search query
        viewModelScope.launch {
            combine(_searchQuery, _mediaTypeFilter) { query, type -> Pair(query, type) }
                .debounce(350)
                .distinctUntilChanged()
                .collect { (query, type) ->
                    executeSearch(query, type)
                }
        }
    }

    fun onQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onMediaTypeChanged(type: MediaType) {
        _mediaTypeFilter.value = type
    }

    fun completeOnboarding(country: String, subscriptions: Set<Int>) {
        viewModelScope.launch {
            preferencesRepository.setCountryCode(country)
            preferencesRepository.setSubscribedProviderIds(subscriptions)
            preferencesRepository.setOnboardingCompleted(true)
        }
    }

    private fun executeSearch(query: String, type: MediaType) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            _isLoading.value = false
            _errorMessage.value = null
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = tmdbRepository.searchTitles(query, type)
            result.fold(
                onSuccess = { titles ->
                    _searchResults.value = titles
                    _isLoading.value = false
                    // Preload verdicts for top results
                    preloadVerdicts(titles.take(8))
                },
                onFailure = { error ->
                    _isLoading.value = false
                    _errorMessage.value = error.localizedMessage ?: "Failed to load titles"
                }
            )
        }
    }

    private fun preloadVerdicts(titles: List<Title>) {
        val currentCountry = countryCode.value
        val userSubs = subscribedProviderIds.value

        titles.forEach { title ->
            if (!_titleVerdicts.value.containsKey(title.id)) {
                viewModelScope.launch {
                    val availResult = tmdbRepository.getWatchProviders(title.id, title.mediaType, currentCountry)
                    val verdict = availResult.fold(
                        onSuccess = { availability ->
                            resolveVerdictUseCase.execute(currentCountry, userSubs, availability)
                        },
                        onFailure = {
                            Verdict.Unknown("Error verifying")
                        }
                    )
                    _titleVerdicts.value = _titleVerdicts.value + (title.id to verdict)
                }
            }
        }
    }

    fun openTitleDetails(title: Title) {
        _selectedTitle.value = title
        _isLoadingDetails.value = true

        val currentCountry = countryCode.value
        val userSubs = subscribedProviderIds.value

        viewModelScope.launch {
            val availResult = tmdbRepository.getWatchProviders(title.id, title.mediaType, currentCountry)
            availResult.fold(
                onSuccess = { availability ->
                    _selectedTitleAvailability.value = availability
                    val verdict = resolveVerdictUseCase.execute(currentCountry, userSubs, availability)
                    _titleVerdicts.value = _titleVerdicts.value + (title.id to verdict)
                    _isLoadingDetails.value = false
                },
                onFailure = {
                    _selectedTitleAvailability.value = null
                    _titleVerdicts.value = _titleVerdicts.value + (title.id to Verdict.Unknown("Network error"))
                    _isLoadingDetails.value = false
                }
            )
        }
    }

    fun closeTitleDetails() {
        _selectedTitle.value = null
        _selectedTitleAvailability.value = null
    }

    fun toggleWatchlist(title: Title) {
        viewModelScope.launch {
            val isSaved = savedWatchlistIds.value.contains(title.id)
            if (isSaved) {
                watchlistRepository.removeFromWatchlist(title.id, title.mediaType)
            } else {
                val currentVerdict = _titleVerdicts.value[title.id] ?: Verdict.Unknown("Pending verification")
                watchlistRepository.addToWatchlist(title, currentVerdict)
            }
        }
    }

    fun toggleWatchlistForSelected() {
        val title = _selectedTitle.value ?: return
        val currentVerdict = _titleVerdicts.value[title.id] ?: Verdict.Unknown("Pending verification")

        viewModelScope.launch {
            if (isSelectedInWatchlist.value) {
                watchlistRepository.removeFromWatchlist(title.id, title.mediaType)
            } else {
                watchlistRepository.addToWatchlist(title, currentVerdict)
            }
        }
    }
}
