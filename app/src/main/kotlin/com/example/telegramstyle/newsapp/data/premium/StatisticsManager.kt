package com.example.telegramstyle.newsapp.data.premium

import com.example.telegramstyle.newsapp.domain.common.AppError
import com.example.telegramstyle.newsapp.domain.common.Result
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

data class ReadingStatistics(
    val totalArticlesRead: Int = 0,
    val totalReadingTimeMinutes: Int = 0,
    val weeklyArticles: Int = 0,
    val monthlyArticles: Int = 0,
    val averageDailyReadingMinutes: Int = 0,
    val topSources: List<SourceStat> = emptyList(),
    val topCategories: List<CategoryStat> = emptyList()
)

data class SourceStat(val name: String, val count: Int)
data class CategoryStat(val name: String, val count: Int)

@Singleton
class StatisticsManager @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    companion object {
        private const val STATS_COLLECTION = "user_statistics"
    }

    fun trackArticleRead(userId: String, newsId: String, source: String, readTimeSeconds: Int): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            val statsRef = firestore.collection(STATS_COLLECTION).document(userId)
            
            firestore.runTransaction { transaction ->
                val doc = transaction.get(statsRef)
                val currentTotal = doc.getLong("totalArticlesRead")?.toInt() ?: 0
                val currentTime = doc.getLong("totalReadingTimeMinutes")?.toInt() ?: 0
                
                transaction.set(statsRef, mapOf(
                    "totalArticlesRead" to currentTotal + 1,
                    "totalReadingTimeMinutes" to currentTime + (readTimeSeconds / 60),
                    "lastReadAt" to System.currentTimeMillis()
                ), com.google.firebase.firestore.SetOptions.merge())
            }.await()
            
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(AppError.DatabaseError("İstatistik kaydedilemedi")))
        }
    }

    fun getStatistics(userId: String): Flow<Result<ReadingStatistics>> = flow {
        emit(Result.Loading)
        try {
            val doc = firestore.collection(STATS_COLLECTION).document(userId).get().await()
            
            val stats = ReadingStatistics(
                totalArticlesRead = doc.getLong("totalArticlesRead")?.toInt() ?: 0,
                totalReadingTimeMinutes = doc.getLong("totalReadingTimeMinutes")?.toInt() ?: 0,
                weeklyArticles = doc.getLong("weeklyArticles")?.toInt() ?: 0,
                monthlyArticles = doc.getLong("monthlyArticles")?.toInt() ?: 0,
                averageDailyReadingMinutes = doc.getLong("averageDailyReadingMinutes")?.toInt() ?: 0
            )
            
            emit(Result.Success(stats))
        } catch (e: Exception) {
            emit(Result.Error(AppError.DatabaseError("İstatistikler alınamadı")))
        }
    }
}
