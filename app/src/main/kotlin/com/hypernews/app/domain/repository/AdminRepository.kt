package com.hypernews.app.domain.repository

import com.hypernews.app.domain.model.AppStatistics
import com.hypernews.app.domain.model.ReportedComment

/**
 * Repository interface for admin and moderation operations.
 * Provides methods for managing reported content and user moderation.
 */
interface AdminRepository {
    
    /**
     * Checks if a user has admin privileges.
     *
     * @param userId Firebase UID of the user
     * @return Result containing true if user is admin, false otherwise
     */
    suspend fun isAdmin(userId: String): Result<Boolean>
    
    /**
     * Gets all reported comments pending review.
     *
     * @return Result containing list of reported comments
     */
    suspend fun getReportedComments(): Result<List<ReportedComment>>
    
    /**
     * Deletes a comment as an admin action.
     *
     * @param commentId ID of the comment to delete
     * @param newsId ID of the news item the comment belongs to
     * @return Result indicating success or failure
     */
    suspend fun deleteComment(commentId: String, newsId: String): Result<Unit>
    
    /**
     * Rejects a report (marks comment as appropriate).
     *
     * @param reportId ID of the report to reject
     * @return Result indicating success or failure
     */
    suspend fun rejectReport(reportId: String): Result<Unit>
    
    /**
     * Bans a user from commenting and reacting.
     *
     * @param userId Firebase UID of the user to ban
     * @return Result indicating success or failure
     */
    suspend fun banUser(userId: String): Result<Unit>
    
    /**
     * Unbans a previously banned user.
     *
     * @param userId Firebase UID of the user to unban
     * @return Result indicating success or failure
     */
    suspend fun unbanUser(userId: String): Result<Unit>
    
    /**
     * Gets application-wide statistics.
     *
     * @return Result containing app statistics
     */
    suspend fun getAppStatistics(): Result<AppStatistics>
}
