package com.example.telegramstyle.newsapp.presentation.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.telegramstyle.newsapp.data.local.dao.NewsItemDao
import com.example.telegramstyle.newsapp.data.mapper.toModel
import com.example.telegramstyle.newsapp.domain.model.NewsItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val newsItemDao: NewsItemDao
) : ViewModel() {
    
    val favorites: StateFlow<List<NewsItem>> = newsItemDao.getFavorites()
        .map { entities -> entities.map { it.toModel() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    fun removeFavorite(newsId: String) {
        viewModelScope.launch {
            newsItemDao.toggleFavorite(newsId)
        }
    }
}
