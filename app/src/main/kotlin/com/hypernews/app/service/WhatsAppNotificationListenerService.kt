package com.hypernews.app.service

import android.app.Notification
import android.content.Intent
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.hypernews.app.data.local.dao.AppSettingsDao
import com.hypernews.app.data.local.dao.WhatsAppChannelDao
import com.hypernews.app.domain.model.NotificationSourcePreference
import com.hypernews.app.worker.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Service that listens to WhatsApp channel notifications and stores them.
 */
@AndroidEntryPoint
class WhatsAppNotificationListenerService : NotificationListenerService() {

    @Inject
    lateinit var whatsAppChannelDao: WhatsAppChannelDao

    @Inject
    lateinit var appSettingsDao: AppSettingsDao

    @Inject
    lateinit var notificationHelper: NotificationHelper

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        private const val TAG = "WhatsAppNotifListener"
        private const val WHATSAPP_PACKAGE = "com.whatsapp"
        private const val WHATSAPP_BUSINESS_PACKAGE = "com.whatsapp.w4b"
        
        // Keys for identifying channel notifications
        private val CHANNEL_INDICATORS = listOf(
            "📢", // Channel emoji
            "Channel", 
            "Kanal",
            "channel update",
            "kanal güncellemesi"
        )
        
        const val KEY_NOTIFICATION_SOURCE = "notification_source_preference"
    }

    override fun onBind(intent: Intent?): IBinder? {
        return super.onBind(intent)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn ?: return
        
        // Check if it's from WhatsApp
        if (sbn.packageName != WHATSAPP_PACKAGE && sbn.packageName != WHATSAPP_BUSINESS_PACKAGE) {
            return
        }

        serviceScope.launch {
            try {
                // Check user preference
                val preference = getNotificationPreference()
                if (preference == NotificationSourcePreference.RSS_ONLY) {
                    return@launch
                }

                val notification = sbn.notification
                val extras = notification.extras

                val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: return@launch
                val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: return@launch
                val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()

                // Check if this is a channel notification
                if (!isChannelNotification(title, text, notification)) {
                    Log.d(TAG, "Not a channel notification: $title")
                    return@launch
                }

                Log.d(TAG, "Channel notification detected: $title - $text")

                // Extract channel info
                val channelId = generateChannelId(title)
                val channelName = extractChannelName(title)
                val messageContent = bigText ?: text

                // Save to database
                whatsAppChannelDao.addMessageAndUpdateChannel(
                    channelId = channelId,
                    channelName = channelName,
                    message = messageContent,
                    timestamp = sbn.postTime
                )

                // Show our own notification (Telegram style)
                showChannelNotification(channelName, messageContent)

            } catch (e: Exception) {
                Log.e(TAG, "Error processing notification", e)
            }
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

    private fun isChannelNotification(title: String, text: String, notification: Notification): Boolean {
        // Check for channel indicators in title or text
        val combinedText = "$title $text".lowercase()
        
        // Check for channel emoji or keywords
        if (CHANNEL_INDICATORS.any { combinedText.contains(it.lowercase()) }) {
            return true
        }

        // Check notification channel ID (WhatsApp uses specific channels for different types)
        val channelId = notification.channelId
        if (channelId?.contains("channel", ignoreCase = true) == true) {
            return true
        }

        // Check for group key pattern (channels often have specific patterns)
        val groupKey = notification.group
        if (groupKey?.contains("channel", ignoreCase = true) == true) {
            return true
        }

        // Additional heuristics:
        // - Channels typically don't have reply actions
        // - Channels often have a specific notification style
        val actions = notification.actions
        val hasNoReplyAction = actions?.none { 
            it.title?.toString()?.lowercase()?.contains("reply") == true ||
            it.title?.toString()?.lowercase()?.contains("yanıtla") == true
        } ?: true

        // If no reply action and has specific format, likely a channel
        if (hasNoReplyAction && title.contains(":")) {
            return true
        }

        return false
    }

    private fun generateChannelId(title: String): String {
        // Generate a consistent ID from the channel name
        val cleanTitle = extractChannelName(title)
        return cleanTitle.lowercase().replace(" ", "_").hashCode().toString()
    }

    private fun extractChannelName(title: String): String {
        // Remove common prefixes/suffixes and clean up the name
        return title
            .replace("📢", "")
            .replace("Channel:", "")
            .replace("Kanal:", "")
            .trim()
            .split(":").firstOrNull()?.trim() ?: title.trim()
    }

    private fun showChannelNotification(channelName: String, message: String) {
        // Use existing notification helper to show Telegram-style notification
        notificationHelper.showWhatsAppChannelNotification(channelName, message)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        // Optional: Handle notification removal if needed
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
