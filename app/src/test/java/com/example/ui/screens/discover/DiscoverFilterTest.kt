package com.example.ui.screens.discover

import com.example.domain.model.Genres
import com.example.domain.model.MediaType
import com.example.domain.model.Provider
import com.example.domain.model.Title
import com.example.domain.model.Verdict
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DiscoverFilterTest {

    private val netflix = Provider(8, "Netflix", "/netflix.jpg")
    private val appleTv = Provider(350, "Apple TV+", "/appletv.jpg")

    private val movie2024 = Title(
        id = 1,
        title = "Dune: Part Two",
        mediaType = MediaType.MOVIE,
        posterPath = "/dune.jpg",
        backdropPath = null,
        releaseYear = "2024",
        overview = "Paul Atreides unites with Chani...",
        voteAverage = 8.3,
        genreIds = listOf(878, 12) // Sci-Fi, Adventure
    )

    private val show2022 = Title(
        id = 2,
        title = "House of the Dragon",
        mediaType = MediaType.TV,
        posterPath = "/hotd.jpg",
        backdropPath = null,
        releaseYear = "2022",
        overview = "The reign of House Targaryen begins...",
        voteAverage = 8.4,
        genreIds = listOf(18, 14, 10765) // Drama, Fantasy, Sci-Fi & Fantasy
    )

    private val comedy2015 = Title(
        id = 3,
        title = "The Intern",
        mediaType = MediaType.MOVIE,
        posterPath = "/intern.jpg",
        backdropPath = null,
        releaseYear = "2015",
        overview = "70-year-old widower Ben Whittaker...",
        voteAverage = 7.2,
        genreIds = listOf(35, 18) // Comedy, Drama
    )

    private val classic2008 = Title(
        id = 4,
        title = "The Dark Knight",
        mediaType = MediaType.MOVIE,
        posterPath = "/batman.jpg",
        backdropPath = null,
        releaseYear = "2008",
        overview = "Batman raises the stakes in his war on crime...",
        voteAverage = 8.5,
        genreIds = listOf(18, 28, 80) // Drama, Action, Crime
    )

    @Test
    fun `year filter correctly partitions release years`() {
        assertTrue(YearFilter.YEAR_2024_PLUS.matches(movie2024.releaseYear))
        assertFalse(YearFilter.YEAR_2024_PLUS.matches(show2022.releaseYear))

        assertTrue(YearFilter.YEAR_2020_2023.matches(show2022.releaseYear))
        assertFalse(YearFilter.YEAR_2020_2023.matches(movie2024.releaseYear))

        assertTrue(YearFilter.YEAR_2010S.matches(comedy2015.releaseYear))
        assertFalse(YearFilter.YEAR_2010S.matches(classic2008.releaseYear))

        assertTrue(YearFilter.CLASSICS.matches(classic2008.releaseYear))
        assertFalse(YearFilter.CLASSICS.matches(comedy2015.releaseYear))
    }

    @Test
    fun `genre filter correctly matches TMDB genre IDs including TV aliases`() {
        assertTrue(Genres.matchesGenre(movie2024.genreIds, "Sci-Fi"))
        assertTrue(Genres.matchesGenre(show2022.genreIds, "Sci-Fi")) // via 10765
        assertFalse(Genres.matchesGenre(comedy2015.genreIds, "Sci-Fi"))

        assertTrue(Genres.matchesGenre(comedy2015.genreIds, "Comedy"))
        assertFalse(Genres.matchesGenre(movie2024.genreIds, "Comedy"))

        assertTrue(Genres.matchesGenre(classic2008.genreIds, "Action"))
        assertFalse(Genres.matchesGenre(comedy2015.genreIds, "Action"))
    }

    @Test
    fun `rating filter correctly excludes titles below threshold`() {
        val titles = listOf(movie2024, show2022, comedy2015, classic2008)

        val min8 = titles.filter { it.voteAverage >= RatingFilter.RATING_8_PLUS.minRating }
        assertEquals(3, min8.size) // 8.3, 8.4, 8.5 (comedy 7.2 excluded)

        val min7 = titles.filter { it.voteAverage >= RatingFilter.RATING_7_PLUS.minRating }
        assertEquals(4, min7.size)
    }

    @Test
    fun `verdict filter correctly isolates only included subscription titles`() {
        val verdicts = mapOf(
            1 to Verdict.Included(primaryProvider = netflix),
            2 to Verdict.RentOrBuy(primaryProvider = appleTv),
            3 to Verdict.Included(primaryProvider = netflix),
            4 to Verdict.Unavailable(countryCode = "MY")
        )

        val titles = listOf(movie2024, show2022, comedy2015, classic2008)

        val subscribedOnly = titles.filter { verdicts[it.id] is Verdict.Included }
        assertEquals(2, subscribedOnly.size)
        assertEquals(listOf(1, 3), subscribedOnly.map { it.id })

        val rentBuyOnly = titles.filter { verdicts[it.id] is Verdict.RentOrBuy }
        assertEquals(1, rentBuyOnly.size)
        assertEquals(2, rentBuyOnly.first().id)
    }

    @Test
    fun `media type filter correctly partitions movies and shows`() {
        val titles = listOf(movie2024, show2022, comedy2015, classic2008)

        val movies = titles.filter { it.mediaType == MediaType.MOVIE }
        assertEquals(3, movies.size)

        val tvShows = titles.filter { it.mediaType == MediaType.TV }
        assertEquals(1, tvShows.size)
        assertEquals("House of the Dragon", tvShows.first().title)
    }
}
