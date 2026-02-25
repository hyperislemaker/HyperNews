package com.example.telegramstyle.newsapp.presentation.screens.auth

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.telegramstyle.newsapp.data.remote.firebase.AuthManager
import com.example.telegramstyle.newsapp.data.remote.firebase.ProfileImageManager
import com.example.telegramstyle.newsapp.data.remote.firebase.UserProfileManager
import com.example.telegramstyle.newsapp.domain.common.Result
import com.example.telegramstyle.newsapp.domain.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileSetupUiState(
    val userName: String = "",
    val profileImageUri: Uri? = null,
    val avatarText: String? = null,
    val isCheckingUserName: Boolean = false,
    val isUserNameAvailable: Boolean? = null,
    val userNameError: String? = null,
    val imageSizeError: String? = null,
    val suggestedUserNames: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val isComplete: Boolean = false,
    val error: String? = null
) {
    val canComplete: Boolean
        get() = userName.length >= 3 && 
                isUserNameAvailable == true && 
                userNameError == null
}

@HiltViewModel
class ProfileSetupViewModel @Inject constructor(
    private val authManager: AuthManager,
    private val userProfileManager: UserProfileManager,
    private val profileImageManager: ProfileImageManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileSetupUiState())
    val uiState: StateFlow<ProfileSetupUiState> = _uiState.asStateFlow()

    private var checkUserNameJob: Job? = null

    fun setUserName(userName: String) {
        val sanitized = userName.lowercase().filter { it.isLetterOrDigit() || it == '_' }
        
        _uiState.update { state ->
            state.copy(
                userName = sanitized,
                isUserNameAvailable = null,
                userNameError = validateUserName(sanitized),
                avatarText = if (sanitized.isNotEmpty()) sanitized.first().uppercase() else null
            )
        }

        // Debounce ile kullanıcı adı kontrolü
        checkUserNameJob?.cancel()
        if (sanitized.length >= 3 && _uiState.value.userNameError == null) {
            checkUserNameJob = viewModelScope.launch {
                delay(500)
                checkUserNameAvailability(sanitized)
            }
        }
    }

    private fun validateUserName(userName: String): String? {
        return when {
            userName.length < 3 -> "En az 3 karakter olmalı"
            userName.length > 20 -> "En fazla 20 karakter olabilir"
            !userName.first().isLetter() -> "Harf ile başlamalı"
            else -> null
        }
    }

    private suspend fun checkUserNameAvailability(userName: String) {
        _uiState.update { it.copy(isCheckingUserName = true) }
        
        userProfileManager.checkUserNameAvailability(userName).collect { result ->
            when (result) {
                is Result.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            isCheckingUserName = false,
                            isUserNameAvailable = result.data,
                            suggestedUserNames = if (!result.data) {
                                generateSuggestions(userName)
                            } else emptyList()
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { state ->
                        state.copy(
                            isCheckingUserName = false,
                            error = "Kontrol edilemedi"
                        )
                    }
                }
                is Result.Loading -> {}
            }
        }
    }

    private fun generateSuggestions(baseName: String): List<String> {
        val random = (100..999).random()
        return listOf(
            "${baseName}${random}",
            "${baseName}_${(10..99).random()}",
            "${baseName}${(System.currentTimeMillis() % 1000)}"
        )
    }

    fun setProfileImage(uri: Uri) {
        viewModelScope.launch {
            profileImageManager.validateImageSize(uri).collect { result ->
                when (result) {
                    is Result.Success -> {
                        if (result.data) {
                            _uiState.update { state ->
                                state.copy(
                                    profileImageUri = uri,
                                    imageSizeError = null
                                )
                            }
                        } else {
                            _uiState.update { state ->
                                state.copy(
                                    imageSizeError = "Resim boyutu 2 MB'dan küçük olmalı"
                                )
                            }
                        }
                    }
                    is Result.Error -> {
                        _uiState.update { state ->
                            state.copy(imageSizeError = "Resim yüklenemedi")
                        }
                    }
                    is Result.Loading -> {}
                }
            }
        }
    }

    fun completeSetup() {
        val currentUser = authManager.getCurrentUser() ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Profil resmini yükle
            var profileImageUrl: String? = null
            _uiState.value.profileImageUri?.let { uri ->
                profileImageManager.uploadProfileImage(currentUser.uid, uri).collect { result ->
                    when (result) {
                        is Result.Success -> profileImageUrl = result.data
                        is Result.Error -> {} // Avatar kullanılacak
                        is Result.Loading -> {}
                    }
                }
            }

            // Profili oluştur
            val profile = UserProfile(
                id = currentUser.uid,
                userName = _uiState.value.userName,
                email = currentUser.email ?: "",
                profileImageUrl = profileImageUrl,
                createdAt = System.currentTimeMillis()
            )

            userProfileManager.createProfile(profile).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.update { state ->
                            state.copy(isLoading = false, isComplete = true)
                        }
                    }
                    is Result.Error -> {
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                error = result.error.message ?: "Profil oluşturulamadı"
                            )
                        }
                    }
                    is Result.Loading -> {}
                }
            }
        }
    }
}
