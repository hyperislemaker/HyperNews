package com.hypernews.app.domain.repository

import com.hypernews.app.domain.model.NewsItem
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for reading list operations (Premium feature).
 * Provides methods for managing a user's reading list with cloud sync.
 */
interface ReadingListRepository {
    
    /**
     * Adds a news item to the reading list.
     *
     * @param newsId ID of the news item to add
     * @return Result indicating success or failure
     */
    suspend fun addToReadingList(newsId: String): Result<Unit>
    
    /**
     * Removes a news item from the reading list.
     *
     * @param newsId ID of the news item to remove
     * @return Result indicating success or failure
     */
    suspend fun removeFromReadingList(newsId: String): Result<Unit>
    
    /**
     * Gets all items in the reading list.
     *
     * @return Flow emitting list of news items in reading list
     */
    fun getReadingList(): Flow<List<NewsItem>>
    
    /**
     * Marks a reading list item as read.
     *
     * @param newsId ID of the news item to mark as read
     * @return Result indicating success or failure
     */
    suspend fun markAsRead(newsId: String): Result<Unit>
}
