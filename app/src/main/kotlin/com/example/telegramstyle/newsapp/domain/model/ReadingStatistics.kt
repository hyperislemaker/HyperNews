package com.example.telegramstyle.newsapp.domain.model

/**
 * Domain model representing reading statistics for a time period.
 *
 * @property totalArticlesRead Total number of articles read
 * @property totalReadingTimeSeconds Total reading time in seconds
 * @property averageReadingTimeSeconds Average reading time per article in seconds
 * @property dailyBreakdown Map of day to articles read count
 */
data class ReadingStatistics(
    val totalArticlesRead: Int,
    val totalReadingTimeSeconds: Long,
    val averageReadingTimeSeconds: Long,
    val dailyBreakdown: Map<String, Int>
)
