package com.hypernews.app.domain.model

import java.time.Instant

/**
 * Domain model representing a user profile.
 *
 * @property id Unique identifier (Firebase UID)
 * @property userName User's display name/nickname
 * @property email User's email address
 * @property profileImageUrl URL of the user's profile photo (nullable if using avatar)
 * @property createdAt When the profile was created
 * @property commentCount Total number of comments made by the user
 * @property reactionCount Total number of reactions given by the user
 * @property isBanned Whether the user is banned from commenting/reacting
 * @property banReason Reason for ban if banned
 */
data class UserProfile(
    val id: String,
    val userName: String,
    val email: String,
    val profileImageUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val commentCount: Int = 0,
    val reactionCount: Int = 0,
    val isBanned: Boolean = false,
    val banReason: String? = null
)
