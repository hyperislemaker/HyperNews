package com.example.telegramstyle.newsapp.domain.repository

import com.example.telegramstyle.newsapp.domain.model.Reaction
import com.example.telegramstyle.newsapp.domain.model.ReactionType
import com.example.telegramstyle.newsapp.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for reaction operations.
 * Provides methods for adding, removing, and fetching reactions on news items.
 */
interface ReactionRepository {
    
    /**
     * Adds or updates a reaction to a news item.
     * If user already has a different reaction, it will be replaced.
     * If user clicks the same reaction, it will be removed (toggle behavior).
     *
     * @param newsId ID of the news item
     * @param reactionType Type of reaction to add
     * @return Result indicating success or failure
     */
    suspend fun addReaction(newsId: String, reactionType: ReactionType): Result<Unit>
    
    /**
     * Removes the current user's reaction from a news item.
     *
     * @param newsId ID of the news item
     * @return Result indicating success or failure
     */
    suspend fun removeReaction(newsId: String): Result<Unit>
    
    /**
     * Gets reaction counts for a news item.
     *
     * @param newsId ID of the news item
     * @return Result containing map of reaction types to counts
     */
    suspend fun getReactionCounts(newsId: String): Result<Map<ReactionType, Int>>
    
    /**
     * Gets users who reacted with a specific reaction type.
     *
     * @param newsId ID of the news item
     * @param reactionType Type of reaction to filter by
     * @return Result containing list of user profiles
     */
    suspend fun getUsersWhoReacted(newsId: String, reactionType: ReactionType): Result<List<UserProfile>>
    
    /**
     * Observes reaction counts for a news item in real-time.
     *
     * @param newsId ID of the news item
     * @return Flow emitting map of reaction types to counts
     */
    fun observeReactions(newsId: String): Flow<Map<ReactionType, Int>>
}
