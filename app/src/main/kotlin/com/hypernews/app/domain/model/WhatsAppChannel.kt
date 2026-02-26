package com.hypernews.app.domain.model

/**
 * Domain model representing a WhatsApp channel.
 */
data class WhatsAppChannel(
    val channelId: String,
    val channelName: String,
    val channelIcon: String? = null,
    val lastMessagePreview: String = "",
    val lastMessageTime: Long = System.currentTimeMillis(),
    val unreadCount: Int = 0
)

/**
 * Domain model representing a WhatsApp channel message.
 */
data class WhatsAppMessage(
    val id: Long = 0,
    val channelId: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
