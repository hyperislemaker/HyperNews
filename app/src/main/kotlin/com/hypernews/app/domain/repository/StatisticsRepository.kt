package com.hypernews.app.domain.repository

import com.hypernews.app.domain.model.ReadingStatistics
import com.hypernews.app.domain.model.SourceStatistics

/**
 * Repository interface for reading statistics operations (Premium feature).
 * Provides methods for tracking and retrieving user reading statistics.
 */
interface StatisticsRepository {
    
    /**
     * Tracks a reading session for a news item.
     *
     * @param newsId ID of the news item being read
     * @param durationSeconds Duration of the reading session in seconds
     * @return Result indicating success or failure
     */
    suspend fun trackReading(newsId: String, durationSeconds: Long): Result<Unit>
    
    /**
     * Gets weekly reading statistics.
     *
     * @return Result containing weekly statistics
     */
    suspend fun getWeeklyStats(): Result<ReadingStatistics>
    
    /**
     * Gets monthly reading statistics.
     *
     * @return Result containing monthly statistics
     */
    suspend fun getMonthlyStats(): Result<ReadingStatistics>
    
    /**
     * Gets statistics for most read sources.
     *
     * @return Result containing list of source statistics
     */
    suspend fun getMostReadSources(): Result<List<SourceStatistics>>
}
