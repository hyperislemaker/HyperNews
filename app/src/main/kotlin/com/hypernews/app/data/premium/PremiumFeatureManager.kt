package com.hypernews.app.data.premium

import com.hypernews.app.data.billing.SubscriptionManager
import com.hypernews.app.data.local.dao.RssFeedDao
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PremiumFeatureManager @Inject constructor(
    private val subscriptionManager: SubscriptionManager,
    private val rssFeedDao: RssFeedDao
) {
    companion object {
        const val FREE_RSS_LIMIT = 5
        const val FREE_KEYWORD_LIMIT = 0
        const val PREMIUM_KEYWORD_LIMIT = 20
        const val FREE_ARCHIVE_DAYS = 30
        const val PREMIUM_ARCHIVE_DAYS = 90
    }

    val isPremium: Boolean get() = subscriptionManager.isPremium

    suspend fun canAddRssFeed(): Boolean {
        if (isPremium) return true
        val currentCount = rssFeedDao.getAllFeeds().first().size
        return currentCount < FREE_RSS_LIMIT
    }

    fun getRemainingRssSlots(): Int {
        return if (isPremium) Int.MAX_VALUE else FREE_RSS_LIMIT
    }

    fun getMaxKeywords(): Int {
        return if (isPremium) PREMIUM_KEYWORD_LIMIT else FREE_KEYWORD_LIMIT
    }

    fun getArchiveDays(): Int {
        return if (isPremium) PREMIUM_ARCHIVE_DAYS else FREE_ARCHIVE_DAYS
    }

    fun hasAdvancedSearch(): Boolean = isPremium
    fun hasCustomThemes(): Boolean = isPremium
    fun hasReadingStats(): Boolean = isPremium
    fun hasReadingList(): Boolean = isPremium
}
