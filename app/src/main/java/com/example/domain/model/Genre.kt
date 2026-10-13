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
        if (target.name.equals("Action", ignoreCase = true) && (genreIds.contains(10759) || genreIds.contains(28))) return true
        if (target.name.equals("Adventure", ignoreCase = true) && (genreIds.contains(10759) || genreIds.contains(12))) return true
        if (target.name.equals("Sci-Fi", ignoreCase = true) && (genreIds.contains(10765) || genreIds.contains(878))) return true
        if (target.name.equals("Fantasy", ignoreCase = true) && (genreIds.contains(10765) || genreIds.contains(14))) return true
        if (target.name.equals("Thriller", ignoreCase = true) && (genreIds.contains(53) || genreIds.contains(9648))) return true
        return genreIds.contains(target.id)
    }

    fun getGenreIdForApi(genreName: String, isTv: Boolean): String? {
        if (genreName.equals("All", ignoreCase = true)) return null
        return when (genreName.lowercase()) {
            "action" -> if (isTv) "10759" else "28"
            "adventure" -> if (isTv) "10759" else "12"
            "sci-fi", "science fiction" -> if (isTv) "10765" else "878"
            "fantasy" -> if (isTv) "10765" else "14"
            "horror" -> if (isTv) null else "27"
            "romance" -> if (isTv) null else "10749"
            "thriller" -> if (isTv) "9648" else "53"
            else -> ALL.firstOrNull { it.name.equals(genreName, ignoreCase = true) }?.id?.toString()
        }
    }
}
