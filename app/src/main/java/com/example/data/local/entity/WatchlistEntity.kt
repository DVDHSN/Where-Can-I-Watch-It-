package com.example.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "watchlist",
    primaryKeys = ["id", "mediaType"]
)
data class WatchlistEntity(
    val id: Int,
    val mediaType: String,
    val title: String,
    val posterPath: String?,
    val releaseYear: String?,
    val overview: String,
    val voteAverage: Double,
    val verdictType: String, // "INCLUDED", "RENT_BUY", "UNAVAILABLE", "UNKNOWN"
    val matchedProviderName: String?,
    val matchedProviderLogo: String?,
    val deepLink: String?,
    val addedAt: Long = System.currentTimeMillis(),
    val notifyOnChange: Boolean = true
)
