package com.example.data.repository

import com.example.data.local.dao.WatchlistDao
import com.example.data.local.entity.WatchlistEntity
import com.example.domain.model.MediaType
import com.example.domain.model.Title
import com.example.domain.model.Verdict
import kotlinx.coroutines.flow.Flow

class WatchlistRepository(
    private val watchlistDao: WatchlistDao
) {
    val allWatchlist: Flow<List<WatchlistEntity>> = watchlistDao.getAllWatchlist()

    suspend fun getAllWatchlistSync(): List<WatchlistEntity> = watchlistDao.getAllWatchlistSync()

    fun isInWatchlist(id: Int, mediaType: MediaType): Flow<Boolean> {
        return watchlistDao.isInWatchlist(id, mediaType.apiValue)
    }

    suspend fun addToWatchlist(title: Title, verdict: Verdict) {
        val (verdictType, providerName, providerLogo, deepLink) = extractVerdictInfo(verdict)

        val entity = WatchlistEntity(
            id = title.id,
            mediaType = title.mediaType.apiValue,
            title = title.title,
            posterPath = title.posterPath,
            releaseYear = title.releaseYear,
            overview = title.overview,
            voteAverage = title.voteAverage,
            verdictType = verdictType,
            matchedProviderName = providerName,
            matchedProviderLogo = providerLogo,
            deepLink = deepLink,
            addedAt = System.currentTimeMillis(),
            notifyOnChange = true
        )
        watchlistDao.insert(entity)
    }

    suspend fun removeFromWatchlist(id: Int, mediaType: MediaType) {
        watchlistDao.deleteById(id, mediaType.apiValue)
    }

    suspend fun updateVerdict(id: Int, mediaType: MediaType, verdict: Verdict) {
        val (verdictType, providerName, providerLogo, _) = extractVerdictInfo(verdict)
        watchlistDao.updateVerdict(
            id = id,
            mediaType = mediaType.apiValue,
            verdictType = verdictType,
            providerName = providerName,
            providerLogo = providerLogo
        )
    }

    private fun extractVerdictInfo(verdict: Verdict): Quadruple<String, String?, String?, String?> {
        return when (verdict) {
            is Verdict.Included -> Quadruple(
                "INCLUDED",
                verdict.primaryProvider.providerName,
                verdict.primaryProvider.logoPath,
                verdict.deepLink
            )
            is Verdict.RentOrBuy -> Quadruple(
                "RENT_BUY",
                verdict.primaryProvider.providerName,
                verdict.primaryProvider.logoPath,
                verdict.deepLink
            )
            is Verdict.Unavailable -> Quadruple(
                "UNAVAILABLE",
                null,
                null,
                null
            )
            is Verdict.Unknown -> Quadruple(
                "UNKNOWN",
                null,
                null,
                null
            )
        }
    }
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
