package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.CachedTitleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TitleDao {

    @Query("SELECT * FROM cached_titles WHERE title LIKE '%' || :query || '%' ORDER BY lastFetchedAt DESC")
    fun searchCachedTitles(query: String): Flow<List<CachedTitleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(titles: List<CachedTitleEntity>)

    @Query("SELECT * FROM cached_titles WHERE id = :id AND mediaType = :mediaType LIMIT 1")
    suspend fun getTitle(id: Int, mediaType: String): CachedTitleEntity?

    @Query("DELETE FROM cached_titles WHERE lastFetchedAt < :threshold")
    suspend fun clearOldCache(threshold: Long)
}
