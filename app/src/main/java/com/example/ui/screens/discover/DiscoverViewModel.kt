package com.example.ui.screens.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.preferences.UserPreferencesRepository
import com.example.data.repository.TmdbRepository
import com.example.data.repository.WatchlistRepository
import com.example.domain.model.Genres
import com.example.domain.model.MediaType
import com.example.domain.model.RegionalAvailability
import com.example.domain.model.Title
import com.example.domain.model.Verdict
import com.example.domain.usecase.ResolveVerdictUseCase
import kotlinx.coroutines.Dispatchers
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
class DiscoverViewModel(
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

    private val _trendingTitles = MutableStateFlow<List<Title>>(emptyList())
    val trendingTitles: StateFlow<List<Title>> = _trendingTitles.asStateFlow()

    private val _titleVerdicts = MutableStateFlow<Map<Int, Verdict>>(emptyMap())
    val titleVerdicts: StateFlow<Map<Int, Verdict>> = _titleVerdicts.asStateFlow()

    private val _filterState = MutableStateFlow(DiscoverFilterState())
    val filterState: StateFlow<DiscoverFilterState> = _filterState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private val _canLoadMore = MutableStateFlow(true)
    val canLoadMore: StateFlow<Boolean> = _canLoadMore.asStateFlow()

    private var currentPage = 1

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

    // Derived filtered list applying user criteria
    val filteredTitles: StateFlow<List<Title>> = combine(
        _trendingTitles,
        _titleVerdicts,
        _filterState
    ) { titles, verdicts, filter ->
        val isDefault = filter.activeFilterCount == 0 && !filter.onlySubscribed
        titles.filter { title ->
            // 1. MediaType filter (Movies / Shows)
            if (filter.mediaType != MediaType.ALL && title.mediaType != filter.mediaType) {
                return@filter false
            }

            // 2. Genre filter (Only enforce client-side if titles are from unfiltered trending,
            // otherwise TMDB discover already strictly filtered by genre)
            if (isDefault && filter.selectedGenre != "All" && title.genreIds.isNotEmpty() && !Genres.matchesGenre(title.genreIds, filter.selectedGenre)) {
                return@filter false
            }

            // 3. Year filter
            if (!filter.yearFilter.matches(title.releaseYear)) {
                return@filter false
            }

            // 4. Rating filter
            if (filter.ratingFilter.minRating > 0.0 && title.voteAverage > 0.0 && title.voteAverage < filter.ratingFilter.minRating) {
                return@filter false
            }

            // 5. Subscriptions filter (Only hide if verdict has loaded and is explicitly not included)
            val verdict = verdicts[title.id]
            if (filter.onlySubscribed || filter.availabilityFilter == AvailabilityFilter.SUBSCRIBED_ONLY) {
                if (verdict != null && verdict !is Verdict.Included) {
                    return@filter false
                }
            } else if (filter.availabilityFilter == AvailabilityFilter.RENT_BUY) {
                if (verdict != null && verdict !is Verdict.RentOrBuy) {
                    return@filter false
                }
            }

            true
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Observe filter state with debouncing to query all eligible titles
        viewModelScope.launch {
            _filterState
                .debounce(200)
                .distinctUntilChanged()
                .collect { filter ->
                    fetchTitles(filter)
                }
        }
    }

    fun loadTrending() {
        fetchTitles(_filterState.value)
    }

    private fun fetchTitles(filter: DiscoverFilterState) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            currentPage = 1
            _canLoadMore.value = true

            val yearRange = when (filter.yearFilter) {
                YearFilter.YEAR_2024_PLUS -> Pair("2024-01-01", null)
                YearFilter.YEAR_2020_2023 -> Pair("2020-01-01", "2023-12-31")
                YearFilter.YEAR_2010S -> Pair("2010-01-01", "2019-12-31")
                YearFilter.CLASSICS -> Pair(null, "2009-12-31")
                YearFilter.ALL -> Pair(null, null)
            }

            val isDefault = filter.activeFilterCount == 0 && !filter.onlySubscribed

            val result = if (isDefault) {
                tmdbRepository.getTrending()
            } else {
                tmdbRepository.discoverTitles(
                    mediaType = filter.mediaType,
                    genreName = filter.selectedGenre,
                    releaseDateGte = yearRange.first,
                    releaseDateLte = yearRange.second,
                    minRating = if (filter.ratingFilter.minRating > 0.0) filter.ratingFilter.minRating else null,
                    watchRegion = countryCode.value,
                    withWatchProviders = subscribedProviderIds.value,
                    onlySubscribed = filter.onlySubscribed,
                    page = 1,
                    pageCount = 2
                )
            }

            result.fold(
                onSuccess = { titles ->
                    _trendingTitles.value = titles
                    _isLoading.value = false
                    preloadVerdicts(titles)
                },
                onFailure = { error ->
                    _isLoading.value = false
                    _errorMessage.value = error.localizedMessage ?: "Failed to load titles"
                }
            )
        }
    }

    fun loadMoreTitles() {
        if (_isLoading.value || _isLoadingMore.value || !_canLoadMore.value) return
        val filter = _filterState.value
        val isDefault = filter.activeFilterCount == 0 && !filter.onlySubscribed
        if (isDefault) return // Trending has fixed 40 items

        viewModelScope.launch {
            _isLoadingMore.value = true
            currentPage += 2

            val yearRange = when (filter.yearFilter) {
                YearFilter.YEAR_2024_PLUS -> Pair("2024-01-01", null)
                YearFilter.YEAR_2020_2023 -> Pair("2020-01-01", "2023-12-31")
                YearFilter.YEAR_2010S -> Pair("2010-01-01", "2019-12-31")
                YearFilter.CLASSICS -> Pair(null, "2009-12-31")
                YearFilter.ALL -> Pair(null, null)
            }

            val result = tmdbRepository.discoverTitles(
                mediaType = filter.mediaType,
                genreName = filter.selectedGenre,
                releaseDateGte = yearRange.first,
                releaseDateLte = yearRange.second,
                minRating = if (filter.ratingFilter.minRating > 0.0) filter.ratingFilter.minRating else null,
                watchRegion = countryCode.value,
                withWatchProviders = subscribedProviderIds.value,
                onlySubscribed = filter.onlySubscribed,
                page = currentPage,
                pageCount = 2
            )

            result.fold(
                onSuccess = { newTitles ->
                    _isLoadingMore.value = false
                    if (newTitles.isEmpty()) {
                        _canLoadMore.value = false
                    } else {
                        val merged = (_trendingTitles.value + newTitles).distinctBy { "${it.id}_${it.mediaType}" }
                        _trendingTitles.value = merged
                        preloadVerdicts(newTitles)
                    }
                },
                onFailure = {
                    _isLoadingMore.value = false
                    _canLoadMore.value = false
                }
            )
        }
    }

    fun toggleOnlySubscribed() {
        val current = _filterState.value
        val newSubscribed = !current.onlySubscribed
        _filterState.value = current.copy(
            onlySubscribed = newSubscribed,
            availabilityFilter = if (newSubscribed) AvailabilityFilter.SUBSCRIBED_ONLY else AvailabilityFilter.ALL
        )
    }

    fun setMediaType(type: MediaType) {
        _filterState.value = _filterState.value.copy(mediaType = type)
    }

    fun setGenre(genreName: String) {
        _filterState.value = _filterState.value.copy(selectedGenre = genreName)
    }

    fun setYearFilter(yearFilter: YearFilter) {
        _filterState.value = _filterState.value.copy(yearFilter = yearFilter)
    }

    fun setAvailabilityFilter(availabilityFilter: AvailabilityFilter) {
        _filterState.value = _filterState.value.copy(
            availabilityFilter = availabilityFilter,
            onlySubscribed = (availabilityFilter == AvailabilityFilter.SUBSCRIBED_ONLY)
        )
    }

    fun setRatingFilter(ratingFilter: RatingFilter) {
        _filterState.value = _filterState.value.copy(ratingFilter = ratingFilter)
    }

    fun resetFilters() {
        _filterState.value = DiscoverFilterState()
    }

    private fun preloadVerdicts(titles: List<Title>) {
        val currentCountry = countryCode.value
        val userSubs = subscribedProviderIds.value

        titles.forEach { title ->
            if (!_titleVerdicts.value.containsKey(title.id)) {
                viewModelScope.launch(Dispatchers.IO) {
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
