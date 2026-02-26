package com.hypernews.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a WhatsApp channel.
 */
@Entity(tableName = "whatsapp_channels")
data class WhatsAppChannelEntity(
    @PrimaryKey
    @ColumnInfo(name = "channel_id")
    val channelId: String,

    @ColumnInfo(name = "channel_name")
    val channelName: String,

    @ColumnInfo(name = "channel_icon")
    val channelIcon: String? = null,

    @ColumnInfo(name = "last_message_preview")
    val lastMessagePreview: String = "",

    @ColumnInfo(name = "last_message_time")
    val lastMessageTime: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "unread_count")
    val unreadCount: Int = 0
)
