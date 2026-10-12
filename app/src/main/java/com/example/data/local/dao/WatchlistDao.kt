package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.WatchlistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {

    @Query("SELECT * FROM watchlist ORDER BY addedAt DESC")
    fun getAllWatchlist(): Flow<List<WatchlistEntity>>

    @Query("SELECT * FROM watchlist ORDER BY addedAt DESC")
    suspend fun getAllWatchlistSync(): List<WatchlistEntity>

    @Query("SELECT * FROM watchlist WHERE id = :id AND mediaType = :mediaType LIMIT 1")
    fun getWatchlistItem(id: Int, mediaType: String): Flow<WatchlistEntity?>

    @Query("SELECT EXISTS(SELECT 1 FROM watchlist WHERE id = :id AND mediaType = :mediaType)")
    fun isInWatchlist(id: Int, mediaType: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: WatchlistEntity)

    @Delete
    suspend fun delete(item: WatchlistEntity)

    @Query("DELETE FROM watchlist WHERE id = :id AND mediaType = :mediaType")
    suspend fun deleteById(id: Int, mediaType: String)

    @Query("UPDATE watchlist SET verdictType = :verdictType, matchedProviderName = :providerName, matchedProviderLogo = :providerLogo WHERE id = :id AND mediaType = :mediaType")
    suspend fun updateVerdict(
        id: Int,
        mediaType: String,
        verdictType: String,
        providerName: String?,
        providerLogo: String?
    )
}
