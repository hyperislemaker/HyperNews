package com.example.telegramstyle.newsapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.telegramstyle.newsapp.data.local.entity.RssFeedEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for RssFeedEntity.
 * Provides methods for accessing and manipulating RSS feed sources in the local database.
 */
@Dao
interface RssFeedDao {

    /**
     * Retrieves all active RSS feeds.
     *
     * @return Flow emitting list of active RSS feeds
     */
    @Query("SELECT * FROM rss_feeds WHERE is_active = 1")
    fun getActiveFeeds(): Flow<List<RssFeedEntity>>

    /**
     * Inserts or replaces an RSS feed in the database.
     *
     * @param feed The RSS feed to insert or replace
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(feed: RssFeedEntity)

    /**
     * Deletes an RSS feed from the database.
     *
     * @param feed The RSS feed to delete
     */
    @Delete
    suspend fun delete(feed: RssFeedEntity)

    /**
     * Updates an existing RSS feed.
     *
     * @param feed The RSS feed to update
     */
    @Update
    suspend fun update(feed: RssFeedEntity)

    /**
     * Retrieves a single RSS feed by its ID.
     *
     * @param id The unique identifier of the RSS feed
     * @return The RSS feed if found, null otherwise
     */
    @Query("SELECT * FROM rss_feeds WHERE id = :id")
    suspend fun getById(id: String): RssFeedEntity?

    @Query("SELECT * FROM rss_feeds WHERE url = :url LIMIT 1")
    suspend fun getFeedByUrl(url: String): RssFeedEntity?

    @Query("DELETE FROM rss_feeds WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE rss_feeds SET last_fetch_time = :lastFetchTime WHERE id = :id")
    suspend fun updateLastFetchTime(id: String, lastFetchTime: Long)

    @Query("SELECT * FROM rss_feeds WHERE is_active = 1")
    suspend fun getAllActiveFeeds(): List<RssFeedEntity>

    @Query("SELECT * FROM rss_feeds")
    fun getAllFeeds(): Flow<List<RssFeedEntity>>
}
