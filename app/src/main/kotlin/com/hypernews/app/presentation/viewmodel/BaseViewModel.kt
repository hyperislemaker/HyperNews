package com.hypernews.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Base ViewModel providing common functionality for UI state management.
 * @param S The type of UI state
 */
abstract class BaseViewModel<S>(initialState: S) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<UiEvent>()
    val events = _events.asSharedFlow()

    protected val currentState: S get() = _uiState.value

    protected fun updateState(reducer: S.() -> S) {
        _uiState.value = currentState.reducer()
    }

    protected fun sendEvent(event: UiEvent) {
        viewModelScope.launch {
            _events.emit(event)
        }
    }
}

/**
 * Base interface for UI events (one-time events like navigation, snackbar, etc.)
 */
interface UiEvent

/**
 * Common UI events that can be used across ViewModels.
 */
sealed class CommonUiEvent : UiEvent {
    data class ShowSnackbar(val message: String) : CommonUiEvent()
    data class ShowToast(val message: String) : CommonUiEvent()
    data class Navigate(val route: String) : CommonUiEvent()
    data object NavigateBack : CommonUiEvent()
}
