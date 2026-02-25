package com.hypernews.app.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hypernews.app.data.local.dao.NewsItemDao
import com.hypernews.app.data.remote.firebase.AuthManager
import com.hypernews.app.data.remote.firebase.UserProfileManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val userName: String? = null,
    val email: String? = null,
    val profileImageUrl: String? = null,
    val commentCount: Int = 0,
    val reactionCount: Int = 0,
    val favoriteCount: Int = 0
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authManager: AuthManager,
    private val userProfileManager: UserProfileManager,
    private val newsItemDao: NewsItemDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            val currentUser = authManager.getCurrentUser()
            currentUser?.let { user ->
                _uiState.update { state ->
                    state.copy(
                        userName = user.displayName,
                        email = user.email,
                        profileImageUrl = user.photoUrl?.toString()
                    )
                }

                // Kullanıcı istatistiklerini yükle
                userProfileManager.getUserProfile(user.uid).collect { result ->
                    if (result is com.hypernews.app.domain.common.Result.Success) {
                        _uiState.update { state ->
                            state.copy(
                                commentCount = result.data.commentCount,
                                reactionCount = result.data.reactionCount
                            )
                        }
                    }
                }
            }
        }

        viewModelScope.launch {
            newsItemDao.getFavorites().collect { favorites ->
                _uiState.update { it.copy(favoriteCount = favorites.size) }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authManager.signOut()
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            authManager.getCurrentUser()?.let { user ->
                userProfileManager.deleteProfile(user.uid)
                authManager.deleteAccount()
            }
        }
    }
}
