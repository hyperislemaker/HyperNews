package com.hypernews.app.presentation.screens.whatsapp

import android.content.Context
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hypernews.app.data.local.dao.AppSettingsDao
import com.hypernews.app.data.local.dao.WhatsAppChannelDao
import com.hypernews.app.data.local.entity.AppSettingsEntity
import com.hypernews.app.data.mapper.toDomain
import com.hypernews.app.domain.model.NotificationSourcePreference
import com.hypernews.app.domain.model.WhatsAppChannel
import com.hypernews.app.domain.model.WhatsAppMessage
import com.hypernews.app.service.WhatsAppNotificationListenerService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WhatsAppChannelsUiState(
    val channels: List<WhatsAppChannel> = emptyList(),
    val selectedChannel: WhatsAppChannel? = null,
    val messages: List<WhatsAppMessage> = emptyList(),
    val isLoading: Boolean = false,
    val hasNotificationAccess: Boolean = false,
    val notificationPreference: NotificationSourcePreference = NotificationSourcePreference.BOTH
)

@HiltViewModel
class WhatsAppChannelsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val whatsAppChannelDao: WhatsAppChannelDao,
    private val appSettingsDao: AppSettingsDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(WhatsAppChannelsUiState())
    val uiState: StateFlow<WhatsAppChannelsUiState> = _uiState.asStateFlow()

    companion object {
        const val KEY_NOTIFICATION_SOURCE = "notification_source_preference"
    }

    init {
        checkNotificationAccess()
        loadChannels()
        loadNotificationPreference()
    }

    fun checkNotificationAccess(): Boolean {
        val enabledListeners = Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners"
        ) ?: ""
        
        // Check if our service is in the enabled listeners list
        // The package name includes the actual package (with .debug suffix in debug builds)
        val packageName = context.packageName
        val serviceName = WhatsAppNotificationListenerService::class.java.name
        val hasAccess = enabledListeners.contains(packageName) && 
                        enabledListeners.contains(serviceName.substringAfterLast('.'))
        
        _uiState.update { it.copy(hasNotificationAccess = hasAccess) }
        return hasAccess
    }

    private fun loadChannels() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            whatsAppChannelDao.getAllChannels().collect { entities ->
                _uiState.update { state ->
                    state.copy(
                        channels = entities.map { it.toDomain() },
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun loadNotificationPreference() {
        viewModelScope.launch {
            val value = appSettingsDao.getValue(KEY_NOTIFICATION_SOURCE)
            val preference = try {
                value?.let { NotificationSourcePreference.valueOf(it) } ?: NotificationSourcePreference.BOTH
            } catch (e: Exception) {
                NotificationSourcePreference.BOTH
            }
            _uiState.update { it.copy(notificationPreference = preference) }
        }
    }

    fun setNotificationPreference(preference: NotificationSourcePreference) {
        viewModelScope.launch {
            appSettingsDao.setValue(AppSettingsEntity(KEY_NOTIFICATION_SOURCE, preference.name))
            _uiState.update { it.copy(notificationPreference = preference) }
        }
    }

    fun selectChannel(channel: WhatsAppChannel) {
        viewModelScope.launch {
            _uiState.update { it.copy(selectedChannel = channel, isLoading = true) }
            
            // Mark channel as read
            whatsAppChannelDao.markChannelAsRead(channel.channelId)
            whatsAppChannelDao.markMessagesAsRead(channel.channelId)
            
            // Load messages
            whatsAppChannelDao.getMessagesByChannel(channel.channelId).collect { entities ->
                _uiState.update { state ->
                    state.copy(
                        messages = entities.map { it.toDomain() },
                        isLoading = false
                    )
                }
            }
        }
    }

    fun clearSelectedChannel() {
        _uiState.update { it.copy(selectedChannel = null, messages = emptyList()) }
    }

    fun deleteChannel(channelId: String) {
        viewModelScope.launch {
            whatsAppChannelDao.deleteChannel(channelId)
        }
    }

    fun clearAllChannels() {
        viewModelScope.launch {
            whatsAppChannelDao.deleteAllMessages()
            whatsAppChannelDao.deleteAllChannels()
        }
    }
}
