package com.example.telegramstyle.newsapp.di

import android.content.Context
import androidx.room.Room
import com.example.telegramstyle.newsapp.data.local.dao.AppSettingsDao
import com.example.telegramstyle.newsapp.data.local.dao.NewsItemDao
import com.example.telegramstyle.newsapp.data.local.dao.RssFeedDao
import com.example.telegramstyle.newsapp.data.local.dao.SearchHistoryDao
import com.example.telegramstyle.newsapp.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing Room database and DAO dependencies.
 * 
 * This module provides singleton instances of:
 * - AppDatabase: The main Room database
 * - NewsItemDao: For news item operations
 * - RssFeedDao: For RSS feed operations
 * - SearchHistoryDao: For search history operations
 * - AppSettingsDao: For app settings operations
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val DATABASE_NAME = "telegram_news_database"

    /**
     * Provides the Room database instance.
     * 
     * Uses fallbackToDestructiveMigration() for development phase.
     * This allows schema changes without manual migration scripts.
     * For production, proper migrations should be implemented.
     * 
     * @param context Application context for database creation
     * @return Singleton AppDatabase instance
     */
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    /**
     * Provides NewsItemDao for news item database operations.
     * 
     * @param database The AppDatabase instance
     * @return NewsItemDao for CRUD operations on news items
     */
    @Provides
    @Singleton
    fun provideNewsItemDao(database: AppDatabase): NewsItemDao {
        return database.newsItemDao()
    }

    /**
     * Provides RssFeedDao for RSS feed database operations.
     * 
     * @param database The AppDatabase instance
     * @return RssFeedDao for CRUD operations on RSS feeds
     */
    @Provides
    @Singleton
    fun provideRssFeedDao(database: AppDatabase): RssFeedDao {
        return database.rssFeedDao()
    }

    /**
     * Provides SearchHistoryDao for search history database operations.
     * 
     * @param database The AppDatabase instance
     * @return SearchHistoryDao for CRUD operations on search history
     */
    @Provides
    @Singleton
    fun provideSearchHistoryDao(database: AppDatabase): SearchHistoryDao {
        return database.searchHistoryDao()
    }

    /**
     * Provides AppSettingsDao for app settings database operations.
     * 
     * @param database The AppDatabase instance
     * @return AppSettingsDao for CRUD operations on app settings
     */
    @Provides
    @Singleton
    fun provideAppSettingsDao(database: AppDatabase): AppSettingsDao {
        return database.appSettingsDao()
    }
}
