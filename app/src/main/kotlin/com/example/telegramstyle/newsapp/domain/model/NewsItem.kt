package com.example.telegramstyle.newsapp.domain.model

import java.time.Instant

/**
 * Domain model representing a news item.
 *
 * @property id Unique identifier for the news item
 * @property title The headline/title of the news
 * @property summary Brief summary or description of the news
 * @property imageUrl URL of the news image (nullable if no image available)
 * @property publishedDate When the news was published
 * @property sourceUrl Original URL of the news article
 * @property sourceName Name of the news source (e.g., NTV, Webtekno)
 * @property isBreakingNews Whether this is a breaking news item
 * @property breakingKeywords Keywords that matched for breaking news detection
 * @property isFavorite Whether the user has marked this as favorite
 * @property commentCount Total number of comments on this news
 * @property reactionCounts Map of reaction types to their counts
 */
data class NewsItem(
    val id: String,
    val title: String,
    val summary: String,
    val imageUrl: String?,
    val publishedDate: Instant,
    val sourceUrl: String,
    val sourceName: String,
    val isBreakingNews: Boolean,
    val breakingKeywords: List<String>,
    val isFavorite: Boolean,
    val commentCount: Int,
    val reactionCounts: Map<ReactionType, Int>
)
