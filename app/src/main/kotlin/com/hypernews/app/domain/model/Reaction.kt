package com.hypernews.app.domain.model

import java.time.Instant

/**
 * Domain model representing a user's reaction to a news item.
 *
 * @property userId Firebase UID of the user who reacted
 * @property userName Display name of the user who reacted
 * @property reactionType The type of reaction given
 * @property timestamp When the reaction was given
 */
data class Reaction(
    val userId: String,
    val userName: String,
    val reactionType: ReactionType,
    val timestamp: Instant
)
