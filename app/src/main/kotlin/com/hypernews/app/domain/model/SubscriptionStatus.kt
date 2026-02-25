package com.hypernews.app.domain.model

import java.time.Instant

/**
 * Domain model representing a user's subscription status.
 *
 * @property plan The current subscription plan
 * @property isActive Whether the subscription is currently active
 * @property expiryDate When the subscription expires (nullable for free plan)
 * @property trialUsed Whether the user has used their free trial
 */
data class SubscriptionStatus(
    val plan: SubscriptionPlan,
    val isActive: Boolean,
    val expiryDate: Instant?,
    val trialUsed: Boolean
)
