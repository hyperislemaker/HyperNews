package com.hypernews.app.presentation.screens.rss

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hypernews.app.data.mapper.toDomain
import com.hypernews.app.data.mapper.toEntity
import com.hypernews.app.data.local.dao.NewsItemDao
import com.hypernews.app.data.local.dao.RssFeedDao
import com.hypernews.app.data.remote.rss.RssFeedManager
import com.hypernews.app.domain.common.Result
import com.hypernews.app.domain.model.RssFeed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RssManagementUiState(
    val feeds: List<RssFeed> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class RssManagementViewModel @Inject constructor(
    private val rssFeedDao: RssFeedDao,
    private val rssFeedManager: RssFeedManager,
    private val newsItemDao: NewsItemDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(RssManagementUiState())
    val uiState: StateFlow<RssManagementUiState> = _uiState.asStateFlow()

    init {
        loadFeeds()
    }

    private fun loadFeeds() {
        viewModelScope.launch {
            rssFeedDao.getAllFeeds().collect { entities ->
                _uiState.update { state ->
                    state.copy(feeds = entities.map { it.toDomain() })
                }
            }
        }
    }

    fun addFeed(url: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val result = rssFeedManager.addFeed("", url)
            when (result) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false, error = null) }
                    onSuccess()
                }
                is Result.Error -> {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            error = result.error.message ?: "Kaynak eklenemedi"
                        )
                    }
                }
                is Result.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    fun deleteFeed(feed: RssFeed) {
        viewModelScope.launch {
            rssFeedDao.delete(feed.toEntity())
            // Silinen kaynağa ait haberleri de temizle
            newsItemDao.deleteNewsFromInactiveFeeds()
        }
    }

    fun toggleNotification(feed: RssFeed) {
        viewModelScope.launch {
            val updated = feed.copy(notificationsEnabled = !feed.notificationsEnabled)
            rssFeedDao.update(updated.toEntity())
        }
    }

    fun toggleActive(feed: RssFeed) {
        viewModelScope.launch {
            val updated = feed.copy(isActive = !feed.isActive)
            rssFeedDao.update(updated.toEntity())
            // Pasif yapılan kaynağın haberlerini temizle
            if (!updated.isActive) {
                newsItemDao.deleteNewsFromInactiveFeeds()
            }
        }
    }

    fun addPresetFeed(name: String, url: String) {
        viewModelScope.launch {
            rssFeedManager.addFeed(name, url)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
