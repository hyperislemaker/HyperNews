package com.hypernews.app.domain.model

/**
 * Enum representing user's notification source preference.
 */
enum class NotificationSourcePreference {
    /** Only WhatsApp channel notifications */
    WHATSAPP_ONLY,
    /** Only RSS feed notifications */
    RSS_ONLY,
    /** Both WhatsApp and RSS notifications */
    BOTH
}
