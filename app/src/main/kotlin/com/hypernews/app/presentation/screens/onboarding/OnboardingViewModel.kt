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
            // Türkiye
            DefaultRssFeed("NTV", "https://www.ntv.com.tr/son-dakika.rss", "Türkiye"),
            DefaultRssFeed("Hürriyet", "https://www.hurriyet.com.tr/rss/gundem", "Türkiye"),
            DefaultRssFeed("Sözcü", "https://www.sozcu.com.tr/rss/gundem.xml", "Türkiye"),
            DefaultRssFeed("CNN Türk", "https://www.cnnturk.com/feed/rss/turkiye/news", "Türkiye"),
            DefaultRssFeed("TRT Haber", "https://www.trthaber.com/sondakika.rss", "Türkiye"),
            DefaultRssFeed("BBC Türkçe", "https://feeds.bbci.co.uk/turkce/rss.xml", "Türkiye"),
            
            // Dünya
            DefaultRssFeed("BBC News", "https://feeds.bbci.co.uk/news/world/rss.xml", "Dünya"),
            DefaultRssFeed("CNN", "http://rss.cnn.com/rss/edition_world.rss", "Dünya"),
            DefaultRssFeed("The Guardian", "https://www.theguardian.com/world/rss", "Dünya"),
            DefaultRssFeed("Al Jazeera", "https://www.aljazeera.com/xml/rss/all.xml", "Dünya"),
            
            // Teknoloji
            DefaultRssFeed("Webtekno", "https://www.webtekno.com/rss.xml", "Teknoloji"),
            DefaultRssFeed("Shiftdelete", "https://shiftdelete.net/feed", "Teknoloji"),
            DefaultRssFeed("TechCrunch", "https://techcrunch.com/feed/", "Teknoloji"),
            DefaultRssFeed("The Verge", "https://www.theverge.com/rss/index.xml", "Teknoloji"),
            
            // Ekonomi
            DefaultRssFeed("Bloomberg HT", "https://www.bloomberght.com/rss", "Ekonomi"),
            DefaultRssFeed("CNBC", "https://www.cnbc.com/id/100003114/device/rss/rss.html", "Ekonomi"),
            
            // Spor
            DefaultRssFeed("NTV Spor", "https://www.ntvspor.net/son-dakika.rss", "Spor"),
            DefaultRssFeed("ESPN", "https://www.espn.com/espn/rss/news", "Spor"),
            DefaultRssFeed("BBC Sport", "https://feeds.bbci.co.uk/sport/rss.xml", "Spor"),
            
            // Bilim
            DefaultRssFeed("NASA", "https://www.nasa.gov/rss/dyn/breaking_news.rss", "Bilim"),
            DefaultRssFeed("Science Daily", "https://www.sciencedaily.com/rss/all.xml", "Bilim")
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
