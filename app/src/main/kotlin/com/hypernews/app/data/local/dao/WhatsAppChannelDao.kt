package com.hypernews.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.hypernews.app.data.local.entity.WhatsAppChannelEntity
import com.hypernews.app.data.local.entity.WhatsAppMessageEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for WhatsApp channel and message operations.
 */
@Dao
interface WhatsAppChannelDao {

    // Channel operations
    @Query("SELECT * FROM whatsapp_channels ORDER BY last_message_time DESC")
    fun getAllChannels(): Flow<List<WhatsAppChannelEntity>>

    @Query("SELECT * FROM whatsapp_channels WHERE channel_id = :channelId")
    suspend fun getChannelById(channelId: String): WhatsAppChannelEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannel(channel: WhatsAppChannelEntity)

    @Query("UPDATE whatsapp_channels SET last_message_preview = :preview, last_message_time = :time, unread_count = unread_count + 1 WHERE channel_id = :channelId")
    suspend fun updateChannelLastMessage(channelId: String, preview: String, time: Long)

    @Query("UPDATE whatsapp_channels SET unread_count = 0 WHERE channel_id = :channelId")
    suspend fun markChannelAsRead(channelId: String)

    @Query("DELETE FROM whatsapp_channels WHERE channel_id = :channelId")
    suspend fun deleteChannel(channelId: String)

    @Query("DELETE FROM whatsapp_channels")
    suspend fun deleteAllChannels()

    // Message operations
    @Query("SELECT * FROM whatsapp_messages WHERE channel_id = :channelId ORDER BY timestamp DESC")
    fun getMessagesByChannel(channelId: String): Flow<List<WhatsAppMessageEntity>>

    @Query("SELECT * FROM whatsapp_messages WHERE channel_id = :channelId ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentMessages(channelId: String, limit: Int): Flow<List<WhatsAppMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: WhatsAppMessageEntity): Long

    @Query("UPDATE whatsapp_messages SET is_read = 1 WHERE channel_id = :channelId")
    suspend fun markMessagesAsRead(channelId: String)

    @Query("DELETE FROM whatsapp_messages WHERE channel_id = :channelId")
    suspend fun deleteMessagesByChannel(channelId: String)

    @Query("DELETE FROM whatsapp_messages")
    suspend fun deleteAllMessages()

    @Query("SELECT COUNT(*) FROM whatsapp_messages WHERE channel_id = :channelId AND is_read = 0")
    suspend fun getUnreadCount(channelId: String): Int

    // Transaction for adding new message and updating channel
    @Transaction
    suspend fun addMessageAndUpdateChannel(
        channelId: String,
        channelName: String,
        message: String,
        timestamp: Long
    ) {
        // Check if channel exists, if not create it
        val existingChannel = getChannelById(channelId)
        if (existingChannel == null) {
            insertChannel(
                WhatsAppChannelEntity(
                    channelId = channelId,
                    channelName = channelName,
                    lastMessagePreview = message,
                    lastMessageTime = timestamp,
                    unreadCount = 1
                )
            )
        } else {
            updateChannelLastMessage(channelId, message, timestamp)
        }

        // Insert the message
        insertMessage(
            WhatsAppMessageEntity(
                channelId = channelId,
                content = message,
                timestamp = timestamp,
                isRead = false
            )
        )
    }
}
