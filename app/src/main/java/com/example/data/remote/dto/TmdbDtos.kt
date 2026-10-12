package com.example.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TmdbSearchResponse(
    val page: Int = 1,
    val results: List<TmdbItemDto> = emptyList(),
    @Json(name = "total_pages") val totalPages: Int = 1,
    @Json(name = "total_results") val totalResults: Int = 0
)

@JsonClass(generateAdapter = true)
data class TmdbItemDto(
    val id: Int,
    val title: String? = null,
    val name: String? = null,
    @Json(name = "media_type") val mediaType: String? = null,
    @Json(name = "poster_path") val posterPath: String? = null,
    @Json(name = "backdrop_path") val backdropPath: String? = null,
    @Json(name = "release_date") val releaseDate: String? = null,
    @Json(name = "first_air_date") val firstAirDate: String? = null,
    val overview: String? = null,
    @Json(name = "vote_average") val voteAverage: Double? = null,
    @Json(name = "genre_ids") val genreIds: List<Int>? = null
)

@JsonClass(generateAdapter = true)
data class TmdbTitleDetailsDto(
    val id: Int,
    val title: String? = null,
    val name: String? = null,
    @Json(name = "poster_path") val posterPath: String? = null,
    @Json(name = "backdrop_path") val backdropPath: String? = null,
    @Json(name = "release_date") val releaseDate: String? = null,
    @Json(name = "first_air_date") val firstAirDate: String? = null,
    val overview: String? = null,
    @Json(name = "vote_average") val voteAverage: Double? = null,
    val genres: List<TmdbGenreDto>? = null,
    @Json(name = "runtime") val runtime: Int? = null,
    @Json(name = "number_of_seasons") val numberOfSeasons: Int? = null,
    @Json(name = "watch/providers") val watchProviders: TmdbWatchProvidersResponse? = null
)

@JsonClass(generateAdapter = true)
data class TmdbGenreDto(
    val id: Int,
    val name: String
)

@JsonClass(generateAdapter = true)
data class TmdbWatchProvidersResponse(
    val id: Int = 0,
    val results: Map<String, TmdbCountryProvidersDto> = emptyMap()
)

@JsonClass(generateAdapter = true)
data class TmdbCountryProvidersDto(
    val link: String? = null,
    val flatrate: List<TmdbProviderDto>? = null,
    val rent: List<TmdbProviderDto>? = null,
    val buy: List<TmdbProviderDto>? = null,
    val free: List<TmdbProviderDto>? = null,
    val ads: List<TmdbProviderDto>? = null
)

@JsonClass(generateAdapter = true)
data class TmdbProviderDto(
    @Json(name = "provider_id") val providerId: Int,
    @Json(name = "provider_name") val providerName: String,
    @Json(name = "logo_path") val logoPath: String? = null,
    @Json(name = "display_priority") val displayPriority: Int? = 0
)
