package com.hypernews.app.presentation.screens.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hypernews.app.data.remote.firebase.AuthManager
import com.hypernews.app.data.remote.firebase.UserProfileManager
import com.hypernews.app.domain.common.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val isNewUser: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authManager: AuthManager,
    private val userProfileManager: UserProfileManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun signInWithGoogle(activityContext: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = authManager.signInWithGoogle(activityContext)) {
                is Result.Success -> {
                    val user = result.data
                    checkIfNewUser(user.uid)
                }
                is Result.Error -> {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            error = result.error.message ?: "Google ile giriş başarısız"
                        )
                    }
                }
                is Result.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            authManager.signInWithEmail(email, password).collect { result ->
                when (result) {
                    is Result.Success -> {
                        val user = result.data
                        checkIfNewUser(user.uid)
                    }
                    is Result.Error -> {
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                error = getErrorMessage(result.error)
                            )
                        }
                    }
                    is Result.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    fun signUpWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            authManager.signUpWithEmail(email, password).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                isLoggedIn = true,
                                isNewUser = true
                            )
                        }
                    }
                    is Result.Error -> {
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                error = getErrorMessage(result.error)
                            )
                        }
                    }
                    is Result.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    private suspend fun checkIfNewUser(userId: String) {
        userProfileManager.getUserProfile(userId).collect { result ->
            when (result) {
                is Result.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            isLoggedIn = true,
                            isNewUser = false
                        )
                    }
                }
                is Result.Error -> {
                    // Profil bulunamadı = yeni kullanıcı
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            isLoggedIn = true,
                            isNewUser = true
                        )
                    }
                }
                is Result.Loading -> {}
            }
        }
    }

    private fun getErrorMessage(error: Throwable): String {
        return when {
            error.message?.contains("email", ignoreCase = true) == true -> 
                "Geçersiz e-posta adresi"
            error.message?.contains("password", ignoreCase = true) == true -> 
                "Şifre en az 6 karakter olmalı"
            error.message?.contains("user", ignoreCase = true) == true -> 
                "Kullanıcı bulunamadı"
            error.message?.contains("network", ignoreCase = true) == true -> 
                "İnternet bağlantısı yok"
            else -> error.message ?: "Bir hata oluştu"
        }
    }
}
