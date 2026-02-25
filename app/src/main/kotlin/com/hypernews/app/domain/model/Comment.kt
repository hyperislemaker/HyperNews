package com.hypernews.app.domain.model

import java.time.Instant

/**
 * Domain model representing a comment on a news item.
 *
 * @property id Unique identifier for the comment
 * @property userId Firebase UID of the comment author
 * @property userName Display name of the comment author
 * @property userPhotoUrl Profile photo URL of the author (nullable if using avatar)
 * @property text The comment text content
 * @property timestamp When the comment was posted
 * @property edited Whether the comment has been edited
 * @property editedAt When the comment was last edited (nullable if never edited)
 * @property isReported Whether the comment has been reported
 * @property reportCount Number of times the comment has been reported
 */
data class Comment(
    val id: String,
    val userId: String,
    val userName: String,
    val userPhotoUrl: String?,
    val text: String,
    val timestamp: Instant,
    val edited: Boolean,
    val editedAt: Instant?,
    val isReported: Boolean,
    val reportCount: Int
)
