package com.hypernews.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.hypernews.app.data.local.dao.AppSettingsDao
import com.hypernews.app.data.local.dao.NewsItemDao
import com.hypernews.app.data.mapper.toEntity
import com.hypernews.app.data.remote.firebase.BreakingNewsDetector
import com.hypernews.app.data.remote.rss.RssFeedManager
import com.hypernews.app.domain.common.Result
import com.hypernews.app.domain.model.NotificationSourcePreference
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class NewsSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val rssFeedManager: RssFeedManager,
    private val newsItemDao: NewsItemDao,
    private val breakingNewsDetector: BreakingNewsDetector,
    private val notificationHelper: NotificationHelper,
    private val appSettingsDao: AppSettingsDao
) : CoroutineWorker(context, params) {
    
    companion object {
        const val WORK_NAME = "news_sync_worker"
        private const val DEFAULT_INTERVAL_MINUTES = 1L // Debug için 1 dakika
        private const val KEY_NOTIFICATION_SOURCE = "notification_source_preference"
        
        fun schedule(context: Context, intervalMinutes: Long = DEFAULT_INTERVAL_MINUTES) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            
            val request = PeriodicWorkRequestBuilder<NewsSyncWorker>(intervalMinutes, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.MINUTES)
                .build()
            
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
        
        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
    
    override suspend fun doWork(): Result {
        return try {
            // Check notification preference
            val preference = getNotificationPreference()
            val shouldShowRssNotifications = preference != NotificationSourcePreference.WHATSAPP_ONLY
            
            when (val result = rssFeedManager.fetchAllFeeds()) {
                is com.hypernews.app.domain.common.Result.Success -> {
                    val newsItems = result.data.map { item ->
                        val isBreaking = breakingNewsDetector.isBreakingNews(item)
                        val keywords = if (isBreaking) breakingNewsDetector.detectBreakingKeywords(item) else emptyList()
                        item.copy(isBreakingNews = isBreaking, breakingKeywords = keywords)
                    }
                    
                    newsItemDao.insertAll(newsItems.map { it.toEntity() })
                    
                    // Only show notifications if RSS notifications are enabled
                    if (shouldShowRssNotifications) {
                        newsItems.filter { it.isBreakingNews }.forEach { news ->
                            notificationHelper.showBreakingNewsNotification(news)
                        }
                    }
                    
                    Result.success()
                }
                is com.hypernews.app.domain.common.Result.Error -> Result.retry()
                is com.hypernews.app.domain.common.Result.Loading -> Result.retry()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
    
    private suspend fun getNotificationPreference(): NotificationSourcePreference {
        val value = appSettingsDao.getValue(KEY_NOTIFICATION_SOURCE)
        return try {
            value?.let { NotificationSourcePreference.valueOf(it) } ?: NotificationSourcePreference.BOTH
        } catch (e: Exception) {
            NotificationSourcePreference.BOTH
        }
    }
}
