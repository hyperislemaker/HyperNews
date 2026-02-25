package com.hypernews.app.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hypernews.app.data.local.dao.NewsItemDao
import com.hypernews.app.data.local.dao.SearchHistoryDao
import com.hypernews.app.data.local.entity.SearchHistoryEntity
import com.hypernews.app.data.mapper.toModel
import com.hypernews.app.domain.model.NewsItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val searchResults: List<NewsItem> = emptyList(),
    val searchHistory: List<String> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val newsItemDao: NewsItemDao,
    private val searchHistoryDao: SearchHistoryDao
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()
    
    init {
        loadSearchHistory()
    }
    
    private fun loadSearchHistory() {
        viewModelScope.launch {
            searchHistoryDao.getHistory(10).collect { history ->
                _uiState.update { it.copy(searchHistory = history.map { h -> h.query }) }
            }
        }
    }
    
    fun search(query: String) {
        if (query.length < 3) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Save to history
            searchHistoryDao.insert(SearchHistoryEntity(query = query, timestamp = System.currentTimeMillis()))
            
            // Search in database
            newsItemDao.searchNews("%$query%").collect { results ->
                _uiState.update { 
                    it.copy(
                        searchResults = results.map { entity -> entity.toModel() },
                        isLoading = false
                    )
                }
            }
        }
    }
    
    fun clearSearch() {
        _uiState.update { it.copy(searchResults = emptyList()) }
    }
    
    fun clearHistory() {
        viewModelScope.launch {
            searchHistoryDao.clearHistory()
        }
    }
    
    fun toggleFavorite(newsId: String) {
        viewModelScope.launch {
            newsItemDao.toggleFavorite(newsId)
        }
    }
}
