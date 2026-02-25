package com.example.telegramstyle.newsapp.worker

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
import com.example.telegramstyle.newsapp.data.local.dao.NewsItemDao
import com.example.telegramstyle.newsapp.data.mapper.toEntity
import com.example.telegramstyle.newsapp.data.remote.firebase.BreakingNewsDetector
import com.example.telegramstyle.newsapp.data.remote.rss.RssFeedManager
import com.example.telegramstyle.newsapp.domain.common.Result
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
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, params) {
    
    companion object {
        const val WORK_NAME = "news_sync_worker"
        private const val DEFAULT_INTERVAL_MINUTES = 15L
        
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
            when (val result = rssFeedManager.fetchAllFeeds()) {
                is com.example.telegramstyle.newsapp.domain.common.Result.Success -> {
                    val newsItems = result.data.map { item ->
                        val isBreaking = breakingNewsDetector.isBreakingNews(item)
                        val keywords = if (isBreaking) breakingNewsDetector.detectBreakingKeywords(item) else emptyList()
                        item.copy(isBreakingNews = isBreaking, breakingKeywords = keywords)
                    }
                    
                    newsItemDao.insertAll(newsItems.map { it.toEntity() })
                    
                    newsItems.filter { it.isBreakingNews }.forEach { news ->
                        notificationHelper.showBreakingNewsNotification(news)
                    }
                    
                    Result.success()
                }
                is com.example.telegramstyle.newsapp.domain.common.Result.Error -> Result.retry()
                is com.example.telegramstyle.newsapp.domain.common.Result.Loading -> Result.retry()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
