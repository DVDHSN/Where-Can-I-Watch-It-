package com.example.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "cached_titles",
    primaryKeys = ["id", "mediaType"]
)
data class CachedTitleEntity(
    val id: Int,
    val mediaType: String,
    val title: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseYear: String?,
    val overview: String,
    val voteAverage: Double,
    val lastFetchedAt: Long = System.currentTimeMillis()
)
