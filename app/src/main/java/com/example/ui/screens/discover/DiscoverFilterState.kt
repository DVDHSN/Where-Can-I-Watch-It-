package com.example.ui.screens.discover

import com.example.domain.model.MediaType

enum class YearFilter(val label: String) {
    ALL("All Years"),
    YEAR_2024_PLUS("2024+"),
    YEAR_2020_2023("2020–2023"),
    YEAR_2010S("2010s"),
    CLASSICS("Before 2010");

    fun matches(yearStr: String?): Boolean {
        if (this == ALL) return true
        val year = yearStr?.toIntOrNull() ?: return false
        return when (this) {
            ALL -> true
            YEAR_2024_PLUS -> year >= 2024
            YEAR_2020_2023 -> year in 2020..2023
            YEAR_2010S -> year in 2010..2019
            CLASSICS -> year < 2010
        }
    }
}

enum class RatingFilter(val label: String, val minRating: Double) {
    ALL("Any Rating", 0.0),
    RATING_6_PLUS("6.0+ ★", 6.0),
    RATING_7_PLUS("7.0+ ★", 7.0),
    RATING_8_PLUS("8.0+ ★", 8.0)
}

enum class AvailabilityFilter(val label: String) {
    ALL("All"),
    SUBSCRIBED_ONLY("Included (🟢)"),
    RENT_BUY("Rent/Buy (🟡)")
}

data class DiscoverFilterState(
    val onlySubscribed: Boolean = false,
    val mediaType: MediaType = MediaType.ALL,
    val selectedGenre: String = "All",
    val yearFilter: YearFilter = YearFilter.ALL,
    val availabilityFilter: AvailabilityFilter = AvailabilityFilter.ALL,
    val ratingFilter: RatingFilter = RatingFilter.ALL
) {
    val activeFilterCount: Int
        get() {
            var count = 0
            if (onlySubscribed) count++
            if (mediaType != MediaType.ALL) count++
            if (selectedGenre != "All") count++
            if (yearFilter != YearFilter.ALL) count++
            if (availabilityFilter != AvailabilityFilter.ALL) count++
            if (ratingFilter != RatingFilter.ALL) count++
            return count
        }
}
