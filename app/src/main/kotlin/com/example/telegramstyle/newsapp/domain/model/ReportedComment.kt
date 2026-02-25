package com.example.telegramstyle.newsapp.domain.model

/**
 * Domain model representing a reported comment for admin review.
 *
 * @property id Unique identifier for the report
 * @property commentId ID of the reported comment
 * @property newsId ID of the news item the comment belongs to
 * @property reporterUserName Display name of the user who reported
 * @property commentContent Content of the reported comment
 * @property reason Reason provided for the report
 * @property reportCount Number of times this comment was reported
 * @property reportedAt When the report was submitted
 */
data class ReportedComment(
    val id: String,
    val commentId: String,
    val newsId: String,
    val reporterUserName: String,
    val commentContent: String,
    val reason: String,
    val reportCount: Int = 1,
    val reportedAt: Long = System.currentTimeMillis()
)
