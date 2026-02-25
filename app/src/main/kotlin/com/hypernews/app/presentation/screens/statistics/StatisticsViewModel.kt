package com.hypernews.app.presentation.screens.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hypernews.app.data.premium.ReadingStatistics
import com.hypernews.app.data.premium.StatisticsManager
import com.hypernews.app.data.remote.firebase.AuthManager
import com.hypernews.app.domain.common.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StatisticsUiState(
    val isLoading: Boolean = false,
    val statistics: ReadingStatistics = ReadingStatistics(),
    val error: String? = null
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val statisticsManager: StatisticsManager,
    private val authManager: AuthManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        loadStatistics()
    }

    private fun loadStatistics() {
        val userId = authManager.getCurrentUser()?.uid ?: return
        
        viewModelScope.launch {
            statisticsManager.getStatistics(userId).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is Result.Success -> {
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                statistics = result.data
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
                }
            }
        }
    }
}
