package com.hypernews.app.presentation.screens.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hypernews.app.data.cache.ConnectivityObserver
import com.hypernews.app.data.cache.ConnectivityStatus
import com.hypernews.app.data.local.dao.NewsItemDao
import com.hypernews.app.data.mapper.toModel
import com.hypernews.app.data.remote.rss.RssFeedManager
import com.hypernews.app.domain.common.Result
import com.hypernews.app.domain.model.NewsItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NewsFeedUiState(
    val news: List<NewsItem> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isOffline: Boolean = false,
    val error: String? = null,
    val selectedSourceFilter: String? = null
)

@HiltViewModel
class NewsFeedViewModel @Inject constructor(
    private val newsItemDao: NewsItemDao,
    private val rssFeedManager: RssFeedManager,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(NewsFeedUiState(isLoading = true))
    val uiState: StateFlow<NewsFeedUiState> = _uiState.asStateFlow()
    
    init {
        observeNews()
        observeConnectivity()
        refresh()
    }
    
    private fun observeNews() {
        viewModelScope.launch {
            newsItemDao.getAllNews().collect { entities ->
                _uiState.update { state ->
                    state.copy(
                        news = entities.map { it.toModel() }.sortedByDescending { it.publishedDate },
                        isLoading = false
                    )
                }
            }
        }
    }
    
    private fun observeConnectivity() {
        viewModelScope.launch {
            connectivityObserver.observe().collect { status ->
                _uiState.update { it.copy(isOffline = status != ConnectivityStatus.Available) }
                if (status == ConnectivityStatus.Available && _uiState.value.news.isEmpty()) {
                    refresh()
                }
            }
        }
    }
    
    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, error = null) }
            
            when (val result = rssFeedManager.fetchAllFeeds()) {
                is Result.Success -> {
                    // News will be updated via Flow observation
                    _uiState.update { it.copy(isRefreshing = false) }
                }
                is Result.Error -> {
                    _uiState.update { 
                        it.copy(
                            isRefreshing = false,
                            error = if (it.news.isEmpty()) result.error.message else null
                        )
                    }
                }
                is Result.Loading -> { /* ignore */ }
            }
        }
    }
    
    fun toggleFavorite(newsId: String) {
        viewModelScope.launch {
            newsItemDao.toggleFavorite(newsId)
        }
    }
    
    fun setSourceFilter(sourceName: String?) {
        _uiState.update { it.copy(selectedSourceFilter = sourceName) }
    }
}
