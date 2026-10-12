package com.example.domain.model

data class Genre(
    val id: Int,
    val name: String
)

object Genres {
    val ALL = listOf(
        Genre(28, "Action"),
        Genre(12, "Adventure"),
        Genre(16, "Animation"),
        Genre(35, "Comedy"),
        Genre(80, "Crime"),
        Genre(99, "Documentary"),
        Genre(18, "Drama"),
        Genre(10751, "Family"),
        Genre(14, "Fantasy"),
        Genre(27, "Horror"),
        Genre(9648, "Mystery"),
        Genre(10749, "Romance"),
        Genre(878, "Sci-Fi"),
        Genre(53, "Thriller"),
        // TV specific aliases
        Genre(10759, "Action & Adventure"),
        Genre(10765, "Sci-Fi & Fantasy")
    )

    private val idToNameMap = ALL.associate { it.id to it.name }

    fun getNameForId(id: Int): String? = idToNameMap[id]

    fun matchesGenre(genreIds: List<Int>, targetGenreName: String): Boolean {
        if (targetGenreName.equals("All", ignoreCase = true)) return true
        val target = ALL.firstOrNull { it.name.equals(targetGenreName, ignoreCase = true) } ?: return false
        if (target.name == "Action" && genreIds.contains(10759)) return true
        if (target.name == "Sci-Fi" && genreIds.contains(10765)) return true
        return genreIds.contains(target.id)
    }
}
