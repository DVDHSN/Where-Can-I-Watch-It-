package com.example.domain.model

data class Title(
    val id: Int,
    val title: String,
    val mediaType: MediaType,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseYear: String?,
    val overview: String,
    val voteAverage: Double,
    val genres: List<String> = emptyList(),
    val genreIds: List<Int> = emptyList()
) {
    val posterUrl: String?
        get() = posterPath?.let {
            if (it.startsWith("http")) it else "https://image.tmdb.org/t/p/w500$it"
        }

    val backdropUrl: String?
        get() = backdropPath?.let {
            if (it.startsWith("http")) it else "https://image.tmdb.org/t/p/w780$it"
        }
}
