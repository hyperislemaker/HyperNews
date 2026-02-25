package com.example.telegramstyle.newsapp.presentation.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.telegramstyle.newsapp.data.local.dao.NewsItemDao
import com.example.telegramstyle.newsapp.data.mapper.toModel
import com.example.telegramstyle.newsapp.data.remote.firebase.AuthManager
import com.example.telegramstyle.newsapp.data.remote.firebase.CommentManager
import com.example.telegramstyle.newsapp.data.remote.firebase.ReactionManager
import com.example.telegramstyle.newsapp.domain.common.Result
import com.example.telegramstyle.newsapp.domain.model.Comment
import com.example.telegramstyle.newsapp.domain.model.NewsItem
import com.example.telegramstyle.newsapp.domain.model.ReactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NewsDetailUiState(
    val newsItem: NewsItem? = null,
    val comments: List<Comment> = emptyList(),
    val reactionCounts: Map<ReactionType, Int> = emptyMap(),
    val userReaction: ReactionType? = null,
    val isLoading: Boolean = true,
    val isCommentsLoading: Boolean = true,
    val error: String? = null,
    val currentUserId: String? = null
)

@HiltViewModel
class NewsDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val newsItemDao: NewsItemDao,
    private val commentManager: CommentManager,
    private val reactionManager: ReactionManager,
    private val authManager: AuthManager
) : ViewModel() {
    
    private val newsId: String = savedStateHandle.get<String>("newsId") ?: ""
    
    private val _uiState = MutableStateFlow(NewsDetailUiState())
    val uiState: StateFlow<NewsDetailUiState> = _uiState.asStateFlow()
    
    init {
        loadNewsItem()
        loadComments()
        loadReactions()
        _uiState.update { it.copy(currentUserId = authManager.getCurrentUser()?.uid) }
    }
    
    private fun loadNewsItem() {
        viewModelScope.launch {
            val entity = newsItemDao.getById(newsId)
            _uiState.update { 
                it.copy(
                    newsItem = entity?.toModel(),
                    isLoading = false,
                    error = if (entity == null) "Haber bulunamadı" else null
                )
            }
        }
    }
    
    private fun loadComments() {
        viewModelScope.launch {
            commentManager.getComments(newsId).collect { comments ->
                _uiState.update { it.copy(comments = comments, isCommentsLoading = false) }
            }
        }
    }
    
    private fun loadReactions() {
        viewModelScope.launch {
            reactionManager.getReactionCounts(newsId).collect { counts ->
                _uiState.update { it.copy(reactionCounts = counts) }
            }
        }
        viewModelScope.launch {
            when (val result = reactionManager.getUserReaction(newsId)) {
                is Result.Success -> _uiState.update { it.copy(userReaction = result.data) }
                else -> {}
            }
        }
    }
    
    fun toggleFavorite() {
        viewModelScope.launch {
            newsItemDao.toggleFavorite(newsId)
            loadNewsItem()
        }
    }
    
    fun addComment(content: String) {
        viewModelScope.launch {
            commentManager.addComment(newsId, content)
        }
    }
    
    fun editComment(commentId: String, newContent: String) {
        viewModelScope.launch {
            commentManager.editComment(newsId, commentId, newContent)
        }
    }
    
    fun deleteComment(commentId: String) {
        viewModelScope.launch {
            commentManager.deleteComment(newsId, commentId)
        }
    }
    
    fun reportComment(commentId: String) {
        viewModelScope.launch {
            commentManager.reportComment(newsId, commentId, "Uygunsuz içerik")
        }
    }
    
    fun addReaction(type: ReactionType) {
        viewModelScope.launch {
            reactionManager.addReaction(newsId, type)
            loadReactions()
        }
    }
}
