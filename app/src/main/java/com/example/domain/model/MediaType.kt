package com.example.domain.model

enum class MediaType(val apiValue: String) {
    MOVIE("movie"),
    TV("tv"),
    ALL("all");

    companion object {
        fun fromApiValue(value: String?): MediaType {
            return when (value?.lowercase()) {
                "tv" -> TV
                else -> MOVIE
            }
        }
    }
}
