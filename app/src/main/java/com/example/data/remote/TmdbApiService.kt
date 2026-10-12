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
}
