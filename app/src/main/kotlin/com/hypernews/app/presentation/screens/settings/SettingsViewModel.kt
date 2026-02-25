package com.hypernews.app.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hypernews.app.data.cache.ImageCacheManager
import com.hypernews.app.data.local.dao.AppSettingsDao
import com.hypernews.app.data.local.dao.RssFeedDao
import com.hypernews.app.data.local.entity.AppSettingsEntity
import com.hypernews.app.data.remote.firebase.AuthManager
import com.hypernews.app.domain.model.ThemePreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val isLoggedIn: Boolean = false,
    val userName: String? = null,
    val userEmail: String? = null,
    val feedCount: Int = 0,
    val notificationsEnabled: Boolean = true,
    val breakingNewsNotificationsEnabled: Boolean = true,
    val themePreference: ThemePreference = ThemePreference.SYSTEM,
    val hyperIsleEnabled: Boolean = true,
    val updateIntervalMinutes: Int = 15,
    val cacheSize: String = "0 MB",
    val appVersion: String = "1.0.0"
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authManager: AuthManager,
    private val appSettingsDao: AppSettingsDao,
    private val rssFeedDao: RssFeedDao,
    private val imageCacheManager: ImageCacheManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    companion object {
        private const val KEY_NOTIFICATIONS = "notifications_enabled"
        private const val KEY_BREAKING_NEWS = "breaking_news_enabled"
        private const val KEY_THEME = "theme"
        private const val KEY_HYPERISLE = "hyperisle_enabled"
        private const val KEY_UPDATE_INTERVAL = "update_interval"
    }

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            val currentUser = authManager.getCurrentUser()
            
            rssFeedDao.getAllFeeds().collect { feeds ->
                _uiState.update { state ->
                    state.copy(
                        isLoggedIn = currentUser != null,
                        userName = currentUser?.displayName,
                        userEmail = currentUser?.email,
                        feedCount = feeds.size
                    )
                }
            }
        }

        viewModelScope.launch {
            val notifications = appSettingsDao.getValue(KEY_NOTIFICATIONS)?.toBoolean() ?: true
            val breakingNews = appSettingsDao.getValue(KEY_BREAKING_NEWS)?.toBoolean() ?: true
            val theme = appSettingsDao.getValue(KEY_THEME)?.let { 
                runCatching { ThemePreference.valueOf(it) }.getOrNull() 
            } ?: ThemePreference.SYSTEM
            val hyperIsle = appSettingsDao.getValue(KEY_HYPERISLE)?.toBoolean() ?: true
            val interval = appSettingsDao.getValue(KEY_UPDATE_INTERVAL)?.toIntOrNull() ?: 15

            _uiState.update { state ->
                state.copy(
                    notificationsEnabled = notifications,
                    breakingNewsNotificationsEnabled = breakingNews,
                    themePreference = theme,
                    hyperIsleEnabled = hyperIsle,
                    updateIntervalMinutes = interval
                )
            }
        }

        viewModelScope.launch {
            val cacheSize = imageCacheManager.getCacheSize()
            _uiState.update { it.copy(cacheSize = formatCacheSize(cacheSize)) }
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            appSettingsDao.setValue(AppSettingsEntity(KEY_NOTIFICATIONS, enabled.toString()))
            _uiState.update { it.copy(notificationsEnabled = enabled) }
        }
    }

    fun setBreakingNewsNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            appSettingsDao.setValue(AppSettingsEntity(KEY_BREAKING_NEWS, enabled.toString()))
            _uiState.update { it.copy(breakingNewsNotificationsEnabled = enabled) }
        }
    }

    fun setThemePreference(theme: ThemePreference) {
        viewModelScope.launch {
            appSettingsDao.setValue(AppSettingsEntity(KEY_THEME, theme.name))
            _uiState.update { it.copy(themePreference = theme) }
        }
    }

    fun setHyperIsleEnabled(enabled: Boolean) {
        viewModelScope.launch {
            appSettingsDao.setValue(AppSettingsEntity(KEY_HYPERISLE, enabled.toString()))
            _uiState.update { it.copy(hyperIsleEnabled = enabled) }
        }
    }

    fun setUpdateInterval(minutes: Int) {
        viewModelScope.launch {
            appSettingsDao.setValue(AppSettingsEntity(KEY_UPDATE_INTERVAL, minutes.toString()))
            _uiState.update { it.copy(updateIntervalMinutes = minutes) }
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            imageCacheManager.clearCache()
            _uiState.update { it.copy(cacheSize = "0 MB") }
        }
    }

    private fun formatCacheSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            else -> "${bytes / (1024 * 1024)} MB"
        }
    }
}
