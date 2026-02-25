package com.example.telegramstyle.newsapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing an RSS feed source stored in the local database.
 *
 * @property id Unique identifier for the RSS feed
 * @property url The URL of the RSS feed
 * @property name Display name of the feed source
 * @property isActive Whether the feed is currently active for fetching
 * @property lastFetchTime When the feed was last fetched (epoch milliseconds, nullable if never fetched)
 * @property notificationsEnabled Whether notifications are enabled for this feed
 */
@Entity(tableName = "rss_feeds")
data class RssFeedEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "url")
    val url: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean,

    @ColumnInfo(name = "last_fetch_time")
    val lastFetchTime: Long?,

    @ColumnInfo(name = "notifications_enabled")
    val notificationsEnabled: Boolean
)
