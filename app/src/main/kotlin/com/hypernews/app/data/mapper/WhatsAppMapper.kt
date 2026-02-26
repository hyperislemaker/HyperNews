package com.hypernews.app.data.mapper

import com.hypernews.app.data.local.entity.WhatsAppChannelEntity
import com.hypernews.app.data.local.entity.WhatsAppMessageEntity
import com.hypernews.app.domain.model.WhatsAppChannel
import com.hypernews.app.domain.model.WhatsAppMessage

fun WhatsAppChannelEntity.toDomain(): WhatsAppChannel = WhatsAppChannel(
    channelId = channelId,
    channelName = channelName,
    channelIcon = channelIcon,
    lastMessagePreview = lastMessagePreview,
    lastMessageTime = lastMessageTime,
    unreadCount = unreadCount
)

fun WhatsAppChannel.toEntity(): WhatsAppChannelEntity = WhatsAppChannelEntity(
    channelId = channelId,
    channelName = channelName,
    channelIcon = channelIcon,
    lastMessagePreview = lastMessagePreview,
    lastMessageTime = lastMessageTime,
    unreadCount = unreadCount
)

fun WhatsAppMessageEntity.toDomain(): WhatsAppMessage = WhatsAppMessage(
    id = id,
    channelId = channelId,
    content = content,
    timestamp = timestamp,
    isRead = isRead
)

fun WhatsAppMessage.toEntity(): WhatsAppMessageEntity = WhatsAppMessageEntity(
    id = id,
    channelId = channelId,
    content = content,
    timestamp = timestamp,
    isRead = isRead
)
