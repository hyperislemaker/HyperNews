package com.example.telegramstyle.newsapp.domain.model

/**
 * Domain model representing reading statistics for a specific news source.
 *
 * @property sourceName Name of the news source
 * @property articlesRead Number of articles read from this source
 * @property totalReadingTimeSeconds Total reading time for this source in seconds
 * @property percentage Percentage of total reading from this source
 */
data class SourceStatistics(
    val sourceName: String,
    val articlesRead: Int,
    val totalReadingTimeSeconds: Long,
    val percentage: Float
)
