package com.hypernews.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hypernews.app.data.local.dao.AppSettingsDao
import com.hypernews.app.data.local.dao.NewsItemDao
import com.hypernews.app.data.local.dao.RssFeedDao
import com.hypernews.app.data.local.dao.SearchHistoryDao
import com.hypernews.app.data.local.dao.WhatsAppChannelDao
import com.hypernews.app.data.local.entity.AppSettingsEntity
import com.hypernews.app.data.local.entity.NewsItemEntity
import com.hypernews.app.data.local.entity.RssFeedEntity
import com.hypernews.app.data.local.entity.SearchHistoryEntity
import com.hypernews.app.data.local.entity.WhatsAppChannelEntity
import com.hypernews.app.data.local.entity.WhatsAppMessageEntity

/**
 * Room database for the Telegram-Style News App.
 *
 * This database stores all local data including:
 * - News items fetched from RSS feeds
 * - RSS feed sources configured by the user
 * - Search history for quick access to previous searches
 * - Application settings (theme, sync interval, etc.)
 * - WhatsApp channel messages
 *
 * Migration Strategy:
 * - Version 1: Initial schema with all 4 entities
 * - Version 2: Added WhatsApp channel entities
 * - Uses fallbackToDestructiveMigration() during development phase
 * - For production releases, proper migration scripts should be implemented
 *   to preserve user data (favorites, settings, etc.)
 *
 * @see NewsItemEntity for news item storage
 * @see RssFeedEntity for RSS feed source storage
 * @see SearchHistoryEntity for search history storage
 * @see AppSettingsEntity for application settings storage
 * @see WhatsAppChannelEntity for WhatsApp channel storage
 * @see WhatsAppMessageEntity for WhatsApp message storage
 */
@Database(
    entities = [
        NewsItemEntity::class,
        RssFeedEntity::class,
        SearchHistoryEntity::class,
        AppSettingsEntity::class,
        WhatsAppChannelEntity::class,
        WhatsAppMessageEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Provides access to news item database operations.
     *
     * @return NewsItemDao for CRUD operations on news items
     */
    abstract fun newsItemDao(): NewsItemDao

    /**
     * Provides access to RSS feed database operations.
     *
     * @return RssFeedDao for CRUD operations on RSS feeds
     */
    abstract fun rssFeedDao(): RssFeedDao

    /**
     * Provides access to search history database operations.
     *
     * @return SearchHistoryDao for CRUD operations on search history
     */
    abstract fun searchHistoryDao(): SearchHistoryDao

    /**
     * Provides access to app settings database operations.
     *
     * @return AppSettingsDao for CRUD operations on app settings
     */
    abstract fun appSettingsDao(): AppSettingsDao

    /**
     * Provides access to WhatsApp channel database operations.
     *
     * @return WhatsAppChannelDao for CRUD operations on WhatsApp channels and messages
     */
    abstract fun whatsAppChannelDao(): WhatsAppChannelDao

    companion object {
        /**
         * Database name used for Room database creation.
         */
        const val DATABASE_NAME = "telegram_news_database"
    }
}
