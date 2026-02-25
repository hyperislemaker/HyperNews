package com.example.telegramstyle.newsapp.presentation.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.telegramstyle.newsapp.data.remote.firebase.AdminManager
import com.example.telegramstyle.newsapp.domain.common.Result
import com.example.telegramstyle.newsapp.domain.model.ReportedComment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminPanelUiState(
    val isLoading: Boolean = false,
    val reportedComments: List<ReportedComment> = emptyList(),
    val totalUsers: Int = 0,
    val totalComments: Int = 0,
    val totalReactions: Int = 0,
    val activeUsers: Int = 0,
    val bannedUsers: List<BannedUser> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class AdminPanelViewModel @Inject constructor(
    private val adminManager: AdminManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminPanelUiState())
    val uiState: StateFlow<AdminPanelUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        loadReportedComments()
        loadStatistics()
        loadBannedUsers()
    }

    private fun loadReportedComments() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            adminManager.getReportedComments().collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                reportedComments = result.data
                            )
                        }
                    }
                    is Result.Error -> {
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                error = result.error.message
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

    private fun loadStatistics() {
        viewModelScope.launch {
            adminManager.getAppStatistics().collect { result ->
                when (result) {
                    is Result.Success -> {
                        val stats = result.data
                        _uiState.update { state ->
                            state.copy(
                                totalUsers = stats.totalUsers,
                                totalComments = stats.totalComments,
                                totalReactions = stats.totalReactions,
                                activeUsers = stats.activeUsers
                            )
                        }
                    }
                    is Result.Error -> {}
                    is Result.Loading -> {}
                }
            }
        }
    }

    private fun loadBannedUsers() {
        viewModelScope.launch {
            adminManager.getBannedUsers().collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.update { state ->
                            state.copy(
                                bannedUsers = result.data.map { user ->
                                    BannedUser(
                                        id = user.id,
                                        userName = user.userName,
                                        reason = user.banReason ?: "Belirtilmemiş"
                                    )
                                }
                            )
                        }
                    }
                    is Result.Error -> {}
                    is Result.Loading -> {}
                }
            }
        }
    }

    fun deleteComment(report: ReportedComment) {
        viewModelScope.launch {
            adminManager.deleteComment(report.newsId, report.commentId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.update { state ->
                            state.copy(
                                reportedComments = state.reportedComments.filter { it.id != report.id }
                            )
                        }
                    }
                    is Result.Error -> {
                        _uiState.update { it.copy(error = result.error.message) }
                    }
                    is Result.Loading -> {}
                }
            }
        }
    }

    fun rejectReport(report: ReportedComment) {
        viewModelScope.launch {
            adminManager.rejectReport(report.id).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.update { state ->
                            state.copy(
                                reportedComments = state.reportedComments.filter { it.id != report.id }
                            )
                        }
                    }
                    is Result.Error -> {
                        _uiState.update { it.copy(error = result.error.message) }
                    }
                    is Result.Loading -> {}
                }
            }
        }
    }

    fun unbanUser(userId: String) {
        viewModelScope.launch {
            adminManager.unbanUser(userId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.update { state ->
                            state.copy(
                                bannedUsers = state.bannedUsers.filter { it.id != userId }
                            )
                        }
                    }
                    is Result.Error -> {
                        _uiState.update { it.copy(error = result.error.message) }
                    }
                    is Result.Loading -> {}
                }
            }
        }
    }
}
