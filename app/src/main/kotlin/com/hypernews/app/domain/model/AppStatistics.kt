package com.hypernews.app.domain.model

/**
 * Domain model representing application-wide statistics for admin panel.
 *
 * @property totalUsers Total number of registered users
 * @property totalComments Total number of comments across all news
 * @property totalReactions Total number of reactions across all news
 * @property totalReports Total number of reported comments
 * @property activeUsers Number of currently active users
 * @property mostActiveUsers List of most active users by engagement
 */
data class AppStatistics(
    val totalUsers: Int,
    val totalComments: Int,
    val totalReactions: Int,
    val totalReports: Int,
    val activeUsers: Int,
    val mostActiveUsers: List<UserProfile>
)
