package com.hypernews.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hypernews.app.data.local.entity.NewsItemEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for NewsItemEntity.
 * Provides methods for accessing and manipulating news items in the local database.
 */
@Dao
interface NewsItemDao {

    /**
     * Retrieves news items with pagination, ordered by published date descending (newest first).
     *
     * @param limit Maximum number of items to return
     * @param offset Number of items to skip
     * @return Flow emitting list of news items
     */
    @Query("SELECT * FROM news_items ORDER BY published_date DESC LIMIT :limit OFFSET :offset")
    fun getNewsPaged(limit: Int, offset: Int): Flow<List<NewsItemEntity>>

    /**
     * Retrieves all favorite news items, ordered by published date descending.
     *
     * @return Flow emitting list of favorite news items
     */
    @Query("SELECT * FROM news_items WHERE is_favorite = 1 ORDER BY published_date DESC")
    fun getFavorites(): Flow<List<NewsItemEntity>>

    /**
     * Searches news items by title or summary containing the query string.
     *
     * @param query Search query string
     * @return Flow emitting list of matching news items
     */
    @Query("SELECT * FROM news_items WHERE title LIKE '%' || :query || '%' OR summary LIKE '%' || :query || '%' ORDER BY published_date DESC")
    fun searchNews(query: String): Flow<List<NewsItemEntity>>

    /**
     * Inserts multiple news items into the database.
     * Ignores items that already exist (based on primary key).
     *
     * @param items List of news items to insert
     * @return List of row IDs for inserted items (-1 for ignored items)
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<NewsItemEntity>): List<Long>

    /**
     * Deletes old news items that are not favorites.
     * Used for automatic cleanup of old news.
     *
     * @param timestamp Cutoff timestamp - items older than this will be deleted
     */
    @Query("DELETE FROM news_items WHERE published_date < :timestamp AND is_favorite = 0")
    suspend fun deleteOldNews(timestamp: Long)

    /**
     * Updates an existing news item.
     *
     * @param item The news item to update
     */
    @Update
    suspend fun update(item: NewsItemEntity)

    /**
     * Retrieves a single news item by its ID.
     *
     * @param id The unique identifier of the news item
     * @return The news item if found, null otherwise
     */
    @Query("SELECT * FROM news_items WHERE id = :id")
    suspend fun getById(id: String): NewsItemEntity?

    @Query("SELECT * FROM news_items ORDER BY published_date DESC")
    fun getAllNews(): Flow<List<NewsItemEntity>>

    @Query("""
        SELECT n.* FROM news_items n
        INNER JOIN rss_feeds f ON n.source_name = f.name
        WHERE f.is_active = 1
        ORDER BY n.published_date DESC
    """)
    fun getNewsFromActiveFeeds(): Flow<List<NewsItemEntity>>

    @Query("UPDATE news_items SET is_favorite = NOT is_favorite WHERE id = :id")
    suspend fun toggleFavorite(id: String)

    @Query("DELETE FROM news_items WHERE source_name NOT IN (SELECT name FROM rss_feeds WHERE is_active = 1) AND is_favorite = 0")
    suspend fun deleteNewsFromInactiveFeeds()
}
