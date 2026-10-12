package com.example.data.repository

import com.example.BuildConfig
import com.example.data.local.dao.TitleDao
import com.example.data.local.dao.WatchlistDao
import com.example.data.local.entity.CachedTitleEntity
import com.example.data.local.entity.WatchlistEntity
import com.example.data.preferences.UserPreferencesRepository
import com.example.data.remote.TmdbApiClient
import com.example.data.remote.TmdbApiService
import com.example.data.remote.dto.TmdbCountryProvidersDto
import com.example.data.remote.dto.TmdbItemDto
import com.example.domain.model.MediaType
import com.example.domain.model.MonetizationType
import com.example.domain.model.Provider
import com.example.domain.model.ProviderOffer
import com.example.domain.model.RegionalAvailability
import com.example.domain.model.Title
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class TmdbRepository(
    private val titleDao: TitleDao,
    private val watchlistDao: WatchlistDao,
    private val preferencesRepository: UserPreferencesRepository
) {
    // Dynamic client that checks custom API key override, then BuildConfig.TMDB_API_KEY
    private val apiClient = TmdbApiClient {
        runBlocking {
            val custom = preferencesRepository.customApiKey.first()
            if (custom.isNotBlank()) {
                custom
            } else {
                BuildConfig.TMDB_API_KEY
            }
        }
    }

    private val apiService: TmdbApiService
        get() = apiClient.apiService

    suspend fun getEffectiveApiKey(): String = withContext(Dispatchers.IO) {
        val custom = preferencesRepository.customApiKey.first()
        if (custom.isNotBlank()) custom else BuildConfig.TMDB_API_KEY
    }

    suspend fun searchTitles(
        query: String,
        mediaType: MediaType = MediaType.ALL
    ): Result<List<Title>> = withContext(Dispatchers.IO) {
        if (query.isBlank()) {
            return@withContext Result.success(emptyList())
        }

        try {
            val response = when (mediaType) {
                MediaType.MOVIE -> apiService.searchMovies(query = query)
                MediaType.TV -> apiService.searchTv(query = query)
                MediaType.ALL -> apiService.searchMulti(query = query)
            }

            val titles = response.results
                .filter { it.posterPath != null || it.backdropPath != null || !it.title.isNullOrBlank() || !it.name.isNullOrBlank() }
                .map { dtoToTitle(it, mediaType) }

            // Cache retrieved titles
            val entities = titles.map {
                CachedTitleEntity(
                    id = it.id,
                    mediaType = it.mediaType.apiValue,
                    title = it.title,
                    posterPath = it.posterPath,
                    backdropPath = it.backdropPath,
                    releaseYear = it.releaseYear,
                    overview = it.overview,
                    voteAverage = it.voteAverage
                )
            }
            titleDao.insertAll(entities)

            Result.success(titles)
        } catch (e: Exception) {
            // Check offline cache on error
            val cached = titleDao.searchCachedTitles(query).first()
            if (cached.isNotEmpty()) {
                val cachedTitles = cached.map {
                    Title(
                        id = it.id,
                        title = it.title,
                        mediaType = MediaType.fromApiValue(it.mediaType),
                        posterPath = it.posterPath,
                        backdropPath = it.backdropPath,
                        releaseYear = it.releaseYear,
                        overview = it.overview,
                        voteAverage = it.voteAverage
                    )
                }
                Result.success(cachedTitles)
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun getTrending(): Result<List<Title>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getTrending(timeWindow = "day")
            val titles = response.results
                .filter { it.posterPath != null }
                .take(40)
                .map { dtoToTitle(it, MediaType.ALL) }
            Result.success(titles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWatchProviders(
        titleId: Int,
        mediaType: MediaType,
        countryCode: String
    ): Result<RegionalAvailability> = withContext(Dispatchers.IO) {
        try {
            val response = when (mediaType) {
                MediaType.TV -> apiService.getTvWatchProviders(titleId)
                else -> apiService.getMovieWatchProviders(titleId)
            }

            val upperCountry = countryCode.uppercase()
            val countryData: TmdbCountryProvidersDto? = response.results[upperCountry]

            if (countryData == null) {
                return@withContext Result.success(
                    RegionalAvailability(
                        countryCode = upperCountry,
                        flatrateProviders = emptyList(),
                        freeProviders = emptyList(),
                        adsProviders = emptyList(),
                        rentOffers = emptyList(),
                        buyOffers = emptyList(),
                        tmdbLink = null,
                        isConfirmedEmpty = true
                    )
                )
            }

            val flatrate = (countryData.flatrate ?: emptyList()).map {
                Provider(it.providerId, it.providerName, it.logoPath, it.displayPriority ?: 0)
            }
            val free = (countryData.free ?: emptyList()).map {
                Provider(it.providerId, it.providerName, it.logoPath, it.displayPriority ?: 0)
            }
            val ads = (countryData.ads ?: emptyList()).map {
                Provider(it.providerId, it.providerName, it.logoPath, it.displayPriority ?: 0)
            }
            val rent = (countryData.rent ?: emptyList()).map {
                ProviderOffer(
                    provider = Provider(it.providerId, it.providerName, it.logoPath, it.displayPriority ?: 0),
                    type = MonetizationType.RENT,
                    link = countryData.link
                )
            }
            val buy = (countryData.buy ?: emptyList()).map {
                ProviderOffer(
                    provider = Provider(it.providerId, it.providerName, it.logoPath, it.displayPriority ?: 0),
                    type = MonetizationType.BUY,
                    link = countryData.link
                )
            }

            val isEmpty = flatrate.isEmpty() && free.isEmpty() && ads.isEmpty() && rent.isEmpty() && buy.isEmpty()

            Result.success(
                RegionalAvailability(
                    countryCode = upperCountry,
                    flatrateProviders = flatrate,
                    freeProviders = free,
                    adsProviders = ads,
                    rentOffers = rent,
                    buyOffers = buy,
                    tmdbLink = countryData.link,
                    isConfirmedEmpty = isEmpty
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun dtoToTitle(dto: TmdbItemDto, requestedType: MediaType): Title {
        val resolvedMediaType = if (requestedType != MediaType.ALL) {
            requestedType
        } else {
            MediaType.fromApiValue(dto.mediaType)
        }

        val title = dto.title ?: dto.name ?: "Untitled"
        val rawDate = dto.releaseDate ?: dto.firstAirDate
        val year = rawDate?.take(4)?.takeIf { it.length == 4 }

        return Title(
            id = dto.id,
            title = title,
            mediaType = resolvedMediaType,
            posterPath = dto.posterPath,
            backdropPath = dto.backdropPath,
            releaseYear = year,
            overview = dto.overview ?: "",
            voteAverage = dto.voteAverage ?: 0.0,
            genreIds = dto.genreIds ?: emptyList()
        )
    }
}
