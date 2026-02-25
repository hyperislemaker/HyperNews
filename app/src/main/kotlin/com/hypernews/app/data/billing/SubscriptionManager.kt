package com.hypernews.app.data.billing

import android.app.Activity
import com.hypernews.app.domain.common.AppError
import com.hypernews.app.domain.common.Result
import com.hypernews.app.domain.model.SubscriptionPlan
import com.hypernews.app.domain.model.SubscriptionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubscriptionManager @Inject constructor() {
    
    companion object {
        const val MONTHLY_PRICE = "₺29.99"
        const val YEARLY_PRICE = "₺249.99"
        const val TRIAL_DAYS = 7
    }

    private val _subscriptionStatus = MutableStateFlow(
        SubscriptionStatus(
            plan = SubscriptionPlan.FREE,
            isActive = true,
            expiryDate = null,
            trialUsed = false
        )
    )
    val subscriptionStatus = _subscriptionStatus.asStateFlow()

    val isPremium: Boolean get() = _subscriptionStatus.value.plan != SubscriptionPlan.FREE

    fun checkSubscriptionStatus(): Flow<Result<SubscriptionStatus>> = flow {
        emit(Result.Loading)
        // In real implementation, this would check Google Play Billing
        emit(Result.Success(_subscriptionStatus.value))
    }

    fun purchaseSubscription(activity: Activity, plan: SubscriptionPlan): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        // In real implementation, this would launch Google Play Billing flow
        emit(Result.Error(AppError.BillingError("Billing not configured")))
    }

    fun restorePurchases(): Flow<Result<SubscriptionStatus>> = flow {
        emit(Result.Loading)
        // In real implementation, this would restore purchases from Google Play
        emit(Result.Success(_subscriptionStatus.value))
    }

    fun getAvailablePlans(): List<SubscriptionPlan> = listOf(
        SubscriptionPlan.MONTHLY,
        SubscriptionPlan.YEARLY
    )
}
