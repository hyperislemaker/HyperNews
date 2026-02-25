package com.example.telegramstyle.newsapp.data.premium

import com.example.telegramstyle.newsapp.domain.common.AppError
import com.example.telegramstyle.newsapp.domain.common.Result
import com.example.telegramstyle.newsapp.domain.model.NewsItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReadingListManager @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    companion object {
        private const val READING_LIST_COLLECTION = "reading_lists"
    }

    fun addToReadingList(userId: String, newsItem: NewsItem): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            firestore.collection(READING_LIST_COLLECTION)
                .document(userId)
                .collection("items")
                .document(newsItem.id)
                .set(mapOf(
                    "newsId" to newsItem.id,
                    "title" to newsItem.title,
                    "addedAt" to System.currentTimeMillis(),
                    "isRead" to false
                )).await()
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(AppError.DatabaseError("Okuma listesine eklenemedi")))
        }
    }

    fun removeFromReadingList(userId: String, newsId: String): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            firestore.collection(READING_LIST_COLLECTION)
                .document(userId)
                .collection("items")
                .document(newsId)
                .delete().await()
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(AppError.DatabaseError("Okuma listesinden kaldırılamadı")))
        }
    }

    fun markAsRead(userId: String, newsId: String): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            firestore.collection(READING_LIST_COLLECTION)
                .document(userId)
                .collection("items")
                .document(newsId)
                .update("isRead", true).await()
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(AppError.DatabaseError("İşaretlenemedi")))
        }
    }

    fun getReadingList(userId: String): Flow<Result<List<ReadingListItem>>> = flow {
        emit(Result.Loading)
        try {
            val docs = firestore.collection(READING_LIST_COLLECTION)
                .document(userId)
                .collection("items")
                .orderBy("addedAt")
                .get().await()
            
            val items = docs.documents.map { doc ->
                ReadingListItem(
                    newsId = doc.getString("newsId") ?: "",
                    title = doc.getString("title") ?: "",
                    addedAt = doc.getLong("addedAt") ?: 0,
                    isRead = doc.getBoolean("isRead") ?: false
                )
            }
            emit(Result.Success(items))
        } catch (e: Exception) {
            emit(Result.Error(AppError.DatabaseError("Okuma listesi alınamadı")))
        }
    }
}

data class ReadingListItem(
    val newsId: String,
    val title: String,
    val addedAt: Long,
    val isRead: Boolean
)
