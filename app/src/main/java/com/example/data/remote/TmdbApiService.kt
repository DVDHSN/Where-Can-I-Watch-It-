package com.example.data.remote

import com.example.data.remote.dto.TmdbSearchResponse
import com.example.data.remote.dto.TmdbTitleDetailsDto
import com.example.data.remote.dto.TmdbWatchProvidersResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApiService {

    @GET("3/search/multi")
    suspend fun searchMulti(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("include_adult") includeAdult: Boolean = false
    ): TmdbSearchResponse

    @GET("3/search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("include_adult") includeAdult: Boolean = false
    ): TmdbSearchResponse

    @GET("3/search/tv")
    suspend fun searchTv(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("include_adult") includeAdult: Boolean = false
    ): TmdbSearchResponse

    @GET("3/movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("append_to_response") appendToResponse: String = "watch/providers"
    ): TmdbTitleDetailsDto

    @GET("3/tv/{series_id}")
    suspend fun getTvDetails(
        @Path("series_id") seriesId: Int,
        @Query("append_to_response") appendToResponse: String = "watch/providers"
    ): TmdbTitleDetailsDto

    @GET("3/movie/{movie_id}/watch/providers")
    suspend fun getMovieWatchProviders(
        @Path("movie_id") movieId: Int
    ): TmdbWatchProvidersResponse

    @GET("3/tv/{series_id}/watch/providers")
    suspend fun getTvWatchProviders(
        @Path("series_id") seriesId: Int
    ): TmdbWatchProvidersResponse

    @GET("3/trending/all/{time_window}")
    suspend fun getTrending(
        @Path("time_window") timeWindow: String = "day"
    ): TmdbSearchResponse

    @GET("3/discover/movie")
    suspend fun discoverMovies(
        @Query("page") page: Int = 1,
        @Query("with_genres") withGenres: String? = null,
        @Query("primary_release_year") primaryReleaseYear: Int? = null,
        @Query("primary_release_date.gte") primaryReleaseDateGte: String? = null,
        @Query("primary_release_date.lte") primaryReleaseDateLte: String? = null,
        @Query("vote_average.gte") voteAverageGte: Double? = null,
        @Query("vote_count.gte") voteCountGte: Int? = null,
        @Query("watch_region") watchRegion: String? = null,
        @Query("with_watch_providers") withWatchProviders: String? = null,
        @Query("with_watch_monetization_types") withWatchMonetizationTypes: String? = null,
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("include_adult") includeAdult: Boolean = false
    ): TmdbSearchResponse

    @GET("3/discover/tv")
    suspend fun discoverTv(
        @Query("page") page: Int = 1,
        @Query("with_genres") withGenres: String? = null,
        @Query("first_air_date_year") firstAirDateYear: Int? = null,
        @Query("first_air_date.gte") firstAirDateGte: String? = null,
        @Query("first_air_date.lte") firstAirDateLte: String? = null,
        @Query("vote_average.gte") voteAverageGte: Double? = null,
        @Query("vote_count.gte") voteCountGte: Int? = null,
        @Query("watch_region") watchRegion: String? = null,
        @Query("with_watch_providers") withWatchProviders: String? = null,
        @Query("with_watch_monetization_types") withWatchMonetizationTypes: String? = null,
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("include_adult") includeAdult: Boolean = false
    ): TmdbSearchResponse
}
