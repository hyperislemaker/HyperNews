package com.hypernews.app.domain.repository

import com.hypernews.app.domain.model.NewsItem
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for news item operations.
 * Provides methods for fetching, searching, and managing news items.
 */
interface NewsRepository {
    
    /**
     * Gets paginated news items ordered by published date (newest first).
     *
     * @param limit Maximum number of items to return
     * @param offset Number of items to skip
     * @return Flow emitting list of news items
     */
    fun getNewsPaged(limit: Int, offset: Int): Flow<List<NewsItem>>
    
    /**
     * Gets all favorite news items.
     *
     * @return Flow emitting list of favorite news items
     */
    fun getFavorites(): Flow<List<NewsItem>>
    
    /**
     * Searches news items by title and summary.
     *
     * @param query Search query (minimum 3 characters)
     * @return Flow emitting list of matching news items
     */
    fun searchNews(query: String): Flow<List<NewsItem>>
    
    /**
     * Toggles the favorite status of a news item.
     *
     * @param newsId ID of the news item
     * @return Result indicating success or failure
     */
    suspend fun toggleFavorite(newsId: String): Result<Unit>
    
    /**
     * Gets a single news item by its ID.
     *
     * @param newsId ID of the news item
     * @return Result containing the news item or error
     */
    suspend fun getNewsById(newsId: String): Result<NewsItem>
}
