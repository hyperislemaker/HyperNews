package com.example.telegramstyle.newsapp.domain.model

import java.time.Instant

/**
 * Domain model representing an RSS feed source.
 *
 * @property id Unique identifier for the RSS feed
 * @property url The URL of the RSS feed
 * @property name Display name of the feed source
 * @property isActive Whether the feed is currently active for fetching
 * @property lastFetchTime When the feed was last fetched (nullable if never fetched)
 * @property notificationsEnabled Whether notifications are enabled for this feed
 */
data class RssFeed(
    val id: String,
    val url: String,
    val name: String,
    val isActive: Boolean,
    val lastFetchTime: Instant?,
    val notificationsEnabled: Boolean
)
