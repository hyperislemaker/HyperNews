package com.hypernews.app.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.hypernews.app.R
import com.hypernews.app.domain.model.NewsItem
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val CHANNEL_GENERAL = "general_news"
        const val CHANNEL_BREAKING = "breaking_news"
        private var notificationId = 0
    }
    
    init {
        createNotificationChannels()
    }
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NotificationManager::class.java)
            
            val generalChannel = NotificationChannel(
                CHANNEL_GENERAL,
                "Genel Haberler",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Genel haber bildirimleri"
            }
            
            val breakingChannel = NotificationChannel(
                CHANNEL_BREAKING,
                "Son Dakika",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Son dakika haber bildirimleri"
            }
            
            manager.createNotificationChannel(generalChannel)
            manager.createNotificationChannel(breakingChannel)
        }
    }
    
    fun showNewsNotification(newsItem: NewsItem) {
        val notification = NotificationCompat.Builder(context, CHANNEL_GENERAL)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(newsItem.sourceName)
            .setContentText(newsItem.title)
            .setAutoCancel(true)
            .setContentIntent(createPendingIntent(newsItem.id))
            .build()
        
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(notificationId++, notification)
    }
    
    fun showBreakingNewsNotification(newsItem: NewsItem) {
        val notification = NotificationCompat.Builder(context, CHANNEL_BREAKING)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("🔴 Son Dakika - ${newsItem.sourceName}")
            .setContentText(newsItem.title)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(createPendingIntent(newsItem.id))
            .build()
        
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(notificationId++, notification)
    }
    
    private fun createPendingIntent(newsId: String): PendingIntent {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            putExtra("news_id", newsId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        
        return PendingIntent.getActivity(
            context,
            newsId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
