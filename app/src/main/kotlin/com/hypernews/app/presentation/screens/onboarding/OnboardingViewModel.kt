package com.hypernews.app.presentation.screens.onboarding

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hypernews.app.data.local.dao.AppSettingsDao
import com.hypernews.app.data.local.dao.NewsItemDao
import com.hypernews.app.data.local.dao.RssFeedDao
import com.hypernews.app.data.local.entity.AppSettingsEntity
import com.hypernews.app.data.local.entity.RssFeedEntity
import com.hypernews.app.data.mapper.toEntity
import com.hypernews.app.data.remote.rss.RssFeedManager
import com.hypernews.app.domain.common.Result
import com.hypernews.app.worker.NewsSyncWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingUiState(
    val defaultFeeds: List<DefaultRssFeed> = emptyList(),
    val selectedFeeds: Set<String> = emptySet(),
    val hasNotificationPermission: Boolean = false,
    val isLoading: Boolean = false,
    val isComplete: Boolean = false
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val rssFeedDao: RssFeedDao,
    private val appSettingsDao: AppSettingsDao,
    private val newsItemDao: NewsItemDao,
    private val rssFeedManager: RssFeedManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        loadDefaultFeeds()
    }

    private fun loadDefaultFeeds() {
        val feeds = listOf(
            // Genel Haberler
            DefaultRssFeed("NTV", "https://www.ntv.com.tr/son-dakika.rss", "Genel Haberler"),
            DefaultRssFeed("Sözcü", "https://www.sozcu.com.tr/rss/tum-haberler.xml", "Genel Haberler"),
            DefaultRssFeed("Hürriyet", "https://www.hurriyet.com.tr/rss/anasayfa", "Genel Haberler"),
            
            // Teknoloji
            DefaultRssFeed("Webtekno", "https://www.webtekno.com/rss.xml", "Teknoloji"),
            DefaultRssFeed("ShiftDelete", "https://shiftdelete.net/feed", "Teknoloji"),
            DefaultRssFeed("Technopat", "https://www.technopat.net/feed/", "Teknoloji"),
            
            // Ekonomi
            DefaultRssFeed("Bloomberg HT", "https://www.bloomberght.com/rss", "Ekonomi"),
            DefaultRssFeed("Dünya", "https://www.dunya.com/rss", "Ekonomi"),
            
            // Spor
            DefaultRssFeed("NTV Spor", "https://www.ntvspor.net/rss", "Spor"),
            DefaultRssFeed("Fanatik", "https://www.fanatik.com.tr/rss/anasayfa", "Spor")
        )

        _uiState.update { it.copy(defaultFeeds = feeds) }
    }

    fun toggleFeed(feed: DefaultRssFeed) {
        _uiState.update { state ->
            val newSelected = if (state.selectedFeeds.contains(feed.url)) {
                state.selectedFeeds - feed.url
            } else {
                state.selectedFeeds + feed.url
            }
            state.copy(selectedFeeds = newSelected)
        }
    }

    fun setNotificationPermission(granted: Boolean) {
        _uiState.update { it.copy(hasNotificationPermission = granted) }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Seçilen RSS kaynaklarını kaydet
            val selectedFeeds = _uiState.value.defaultFeeds
                .filter { _uiState.value.selectedFeeds.contains(it.url) }
                .map { feed ->
                    RssFeedEntity(
                        id = java.util.UUID.randomUUID().toString(),
                        url = feed.url,
                        name = feed.name,
                        isActive = true,
                        lastFetchTime = null,
                        notificationsEnabled = true
                    )
                }

            selectedFeeds.forEach { feed ->
                rssFeedDao.insert(feed)
            }

            // Onboarding tamamlandı olarak işaretle
            appSettingsDao.setValue(
                AppSettingsEntity("onboarding_completed", "true")
            )

            // Bildirim ayarını kaydet
            appSettingsDao.setValue(
                AppSettingsEntity("notifications_enabled", _uiState.value.hasNotificationPermission.toString())
            )

            // Haberleri hemen çek
            when (val result = rssFeedManager.fetchAllFeeds()) {
                is Result.Success -> {
                    newsItemDao.insertAll(result.data.map { it.toEntity() })
                }
                else -> { /* Hata olsa bile devam et */ }
            }
            
            // Periyodik senkronizasyonu başlat
            NewsSyncWorker.schedule(context)

            _uiState.update { it.copy(isLoading = false, isComplete = true) }
        }
    }
}
