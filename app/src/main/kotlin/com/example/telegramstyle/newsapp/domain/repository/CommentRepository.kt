package com.example.telegramstyle.newsapp.domain.repository

import com.example.telegramstyle.newsapp.domain.model.Comment
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for comment operations.
 * Provides methods for adding, editing, deleting, and fetching comments.
 */
interface CommentRepository {
    
    /**
     * Adds a new comment to a news item.
     *
     * @param newsId ID of the news item
     * @param text Comment text content
     * @return Result containing the created Comment or error
     */
    suspend fun addComment(newsId: String, text: String): Result<Comment>
    
    /**
     * Edits an existing comment.
     *
     * @param commentId ID of the comment to edit
     * @param newText New text content
     * @return Result containing the updated Comment or error
     */
    suspend fun editComment(commentId: String, newText: String): Result<Comment>
    
    /**
     * Deletes a comment.
     *
     * @param commentId ID of the comment to delete
     * @return Result indicating success or failure
     */
    suspend fun deleteComment(commentId: String): Result<Unit>
    
    /**
     * Gets paginated comments for a news item.
     *
     * @param newsId ID of the news item
     * @param page Page number (0-indexed)
     * @param pageSize Number of comments per page
     * @return Result containing list of comments or error
     */
    suspend fun getComments(newsId: String, page: Int, pageSize: Int): Result<List<Comment>>
    
    /**
     * Reports a comment for moderation.
     *
     * @param commentId ID of the comment to report
     * @param reason Optional reason for the report
     * @return Result indicating success or failure
     */
    suspend fun reportComment(commentId: String, reason: String?): Result<Unit>
    
    /**
     * Observes comments for a news item in real-time.
     *
     * @param newsId ID of the news item
     * @return Flow emitting list of comments
     */
    fun observeComments(newsId: String): Flow<List<Comment>>
}
