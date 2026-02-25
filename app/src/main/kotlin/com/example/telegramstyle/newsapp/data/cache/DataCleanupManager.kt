package com.example.telegramstyle.newsapp.data.cache

import com.example.telegramstyle.newsapp.data.local.dao.NewsItemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataCleanupManager @Inject constructor(
    private val newsItemDao: NewsItemDao,
    private val imageCacheManager: ImageCacheManager
) {
    companion object {
        const val FREE_RETENTION_DAYS = 30
        const val PREMIUM_RETENTION_DAYS = 90
        private const val MILLIS_PER_DAY = 24 * 60 * 60 * 1000L
    }

    suspend fun cleanupOldNews(isPremium: Boolean = false) = withContext(Dispatchers.IO) {
        val retentionDays = if (isPremium) PREMIUM_RETENTION_DAYS else FREE_RETENTION_DAYS
        val cutoffTime = System.currentTimeMillis() - (retentionDays * MILLIS_PER_DAY)
        newsItemDao.deleteOldNews(cutoffTime)
    }

    suspend fun clearAllCache() = withContext(Dispatchers.IO) {
        imageCacheManager.clearCache()
    }

    suspend fun getCacheSize(): Long = withContext(Dispatchers.IO) {
        imageCacheManager.getCacheSize()
    }
}
