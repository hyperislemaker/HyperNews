package com.example.telegramstyle.newsapp.domain.repository

import com.example.telegramstyle.newsapp.domain.model.SubscriptionPlan
import com.example.telegramstyle.newsapp.domain.model.SubscriptionStatus

/**
 * Repository interface for subscription and billing operations.
 * Provides methods for managing premium subscriptions via Google Play Billing.
 */
interface SubscriptionRepository {
    
    /**
     * Checks the current user's subscription status.
     *
     * @return Result containing the subscription status
     */
    suspend fun checkSubscriptionStatus(): Result<SubscriptionStatus>
    
    /**
     * Initiates a subscription purchase.
     *
     * @param plan The subscription plan to purchase
     * @return Result indicating success or failure
     */
    suspend fun purchaseSubscription(plan: SubscriptionPlan): Result<Unit>
    
    /**
     * Restores previous purchases for the current user.
     *
     * @return Result containing the restored subscription status
     */
    suspend fun restorePurchases(): Result<SubscriptionStatus>
}
