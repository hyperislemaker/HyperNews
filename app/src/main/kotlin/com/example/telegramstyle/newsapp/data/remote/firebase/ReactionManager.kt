package com.example.telegramstyle.newsapp.data.remote.firebase

import com.example.telegramstyle.newsapp.domain.common.AppError
import com.example.telegramstyle.newsapp.domain.common.Result
import com.example.telegramstyle.newsapp.domain.model.Reaction
import com.example.telegramstyle.newsapp.domain.model.ReactionType
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReactionManager @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authManager: AuthManager
) {
    companion object {
        private const val NEWS_COLLECTION = "news_items"
        private const val REACTIONS_COLLECTION = "reactions"
    }
    
    suspend fun addReaction(newsId: String, type: ReactionType): Result<Unit> {
        val user = authManager.getCurrentUser() ?: return Result.Error(AppError.AuthError("Tepki vermek için giriş yapmalısınız"))
        
        return try {
            val reactionRef = firestore.collection(NEWS_COLLECTION).document(newsId)
                .collection(REACTIONS_COLLECTION).document(user.uid)
            val newsRef = firestore.collection(NEWS_COLLECTION).document(newsId)
            
            firestore.runTransaction { transaction ->
                val existingReaction = transaction.get(reactionRef)
                val oldType = existingReaction.getString("type")?.let { ReactionType.valueOf(it) }
                
                if (oldType == type) {
                    // Toggle off - remove reaction
                    transaction.delete(reactionRef)
                    transaction.update(newsRef, "reactionCounts.${type.name}", FieldValue.increment(-1))
                } else {
                    // Add new or change reaction
                    if (oldType != null) {
                        transaction.update(newsRef, "reactionCounts.${oldType.name}", FieldValue.increment(-1))
                    }
                    transaction.set(reactionRef, mapOf(
                        "userId" to user.uid, "newsId" to newsId, "type" to type.name,
                        "createdAt" to Instant.now().toEpochMilli()
                    ))
                    transaction.update(newsRef, "reactionCounts.${type.name}", FieldValue.increment(1))
                }
            }.await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(AppError.DatabaseError("Tepki eklenemedi: ${e.message}"))
        }
    }
    
    suspend fun removeReaction(newsId: String): Result<Unit> {
        val user = authManager.getCurrentUser() ?: return Result.Error(AppError.AuthError("Giriş yapmalısınız"))
        
        return try {
            val reactionRef = firestore.collection(NEWS_COLLECTION).document(newsId)
                .collection(REACTIONS_COLLECTION).document(user.uid)
            val newsRef = firestore.collection(NEWS_COLLECTION).document(newsId)
            
            firestore.runTransaction { transaction ->
                val existingReaction = transaction.get(reactionRef)
                val oldType = existingReaction.getString("type")?.let { ReactionType.valueOf(it) }
                
                if (oldType != null) {
                    transaction.delete(reactionRef)
                    transaction.update(newsRef, "reactionCounts.${oldType.name}", FieldValue.increment(-1))
                }
            }.await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(AppError.DatabaseError("Tepki kaldırılamadı: ${e.message}"))
        }
    }
    
    suspend fun getUserReaction(newsId: String): Result<ReactionType?> {
        val user = authManager.getCurrentUser() ?: return Result.Success(null)
        
        return try {
            val doc = firestore.collection(NEWS_COLLECTION).document(newsId)
                .collection(REACTIONS_COLLECTION).document(user.uid).get().await()
            val type = doc.getString("type")?.let { ReactionType.valueOf(it) }
            Result.Success(type)
        } catch (e: Exception) {
            Result.Error(AppError.DatabaseError("Tepki alınamadı: ${e.message}"))
        }
    }
    
    fun getReactionCounts(newsId: String): Flow<Map<ReactionType, Int>> = callbackFlow {
        val listener = firestore.collection(NEWS_COLLECTION).document(newsId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                @Suppress("UNCHECKED_CAST")
                val counts = (snapshot?.get("reactionCounts") as? Map<String, Long>)
                    ?.mapKeys { ReactionType.valueOf(it.key) }
                    ?.mapValues { it.value.toInt() }
                    ?: emptyMap()
                trySend(counts)
            }
        awaitClose { listener.remove() }
    }
}
