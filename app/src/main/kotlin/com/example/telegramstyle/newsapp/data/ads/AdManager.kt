package com.example.telegramstyle.newsapp.data.ads

import android.content.Context
import com.example.telegramstyle.newsapp.data.billing.SubscriptionManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val subscriptionManager: SubscriptionManager
) {
    companion object {
        const val NATIVE_AD_INTERVAL = 5 // Her 5 haberde bir
        const val BANNER_AD_UNIT_ID = "ca-app-pub-xxxxx/banner"
        const val NATIVE_AD_UNIT_ID = "ca-app-pub-xxxxx/native"
    }

    val shouldShowAds: Boolean
        get() = !subscriptionManager.isPremium

    fun shouldShowNativeAd(position: Int): Boolean {
        if (!shouldShowAds) return false
        return position > 0 && position % NATIVE_AD_INTERVAL == 0
    }

    fun initialize() {
        // In real implementation, initialize AdMob SDK
        // MobileAds.initialize(context)
    }

    fun showConsentDialog(onConsentGiven: (Boolean) -> Unit) {
        // In real implementation, show GDPR/KVKK consent dialog
        onConsentGiven(true)
    }
}
