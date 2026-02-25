package com.hypernews.app.domain.repository

import com.hypernews.app.domain.model.RssFeed
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for RSS feed management operations.
 * Provides methods for adding, removing, and managing RSS feed sources.
 */
interface RssFeedRepository {
    
    /**
     * Adds a new RSS feed source.
     *
     * @param url URL of the RSS feed
     * @param name Display name for the feed
     * @return Result containing the created RssFeed or error
     */
    suspend fun addFeed(url: String, name: String): Result<RssFeed>
    
    /**
     * Removes an RSS feed source.
     *
     * @param feedId ID of the feed to remove
     * @return Result indicating success or failure
     */
    suspend fun removeFeed(feedId: String): Result<Unit>
    
    /**
     * Gets all active RSS feeds.
     *
     * @return Flow emitting list of active RSS feeds
     */
    fun getActiveFeeds(): Flow<List<RssFeed>>
    
    /**
     * Updates an existing RSS feed.
     *
     * @param feed The updated RssFeed object
     * @return Result indicating success or failure
     */
    suspend fun updateFeed(feed: RssFeed): Result<Unit>
    
    /**
     * Validates if a URL is a valid RSS feed.
     *
     * @param url URL to validate
     * @return Result containing true if valid, false otherwise
     */
    suspend fun validateFeedUrl(url: String): Result<Boolean>
}
