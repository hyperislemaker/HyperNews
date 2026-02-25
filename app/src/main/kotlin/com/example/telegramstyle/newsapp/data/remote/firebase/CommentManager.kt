package com.example.telegramstyle.newsapp.data.remote.firebase

import com.example.telegramstyle.newsapp.domain.common.AppError
import com.example.telegramstyle.newsapp.domain.common.Result
import com.example.telegramstyle.newsapp.domain.model.Comment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentManager @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val profanityFilter: ProfanityFilter,
    private val authManager: AuthManager
) {
    companion object {
        private const val NEWS_COLLECTION = "news_items"
        private const val COMMENTS_COLLECTION = "comments"
        private const val PAGE_SIZE = 20
        const val MAX_COMMENT_LENGTH = 500
        const val MIN_COMMENT_LENGTH = 1
    }
    
    suspend fun addComment(newsId: String, content: String): Result<Comment> {
        val user = authManager.getCurrentUser() ?: return Result.Error(AppError.AuthError("Yorum yapmak için giriş yapmalısınız"))
        
        if (content.length !in MIN_COMMENT_LENGTH..MAX_COMMENT_LENGTH) {
            return Result.Error(AppError.ValidationError("Yorum $MIN_COMMENT_LENGTH-$MAX_COMMENT_LENGTH karakter arasında olmalıdır"))
        }
        
        if (profanityFilter.containsProfanity(content)) {
            return Result.Error(AppError.ValidationError("Yorumunuz uygunsuz içerik barındırıyor"))
        }
        
        val comment = Comment(
            id = UUID.randomUUID().toString(),
            userId = user.uid,
            userName = user.displayName ?: "Anonim",
            userPhotoUrl = user.photoUrl?.toString(),
            text = content,
            timestamp = Instant.now(),
            edited = false,
            editedAt = null,
            isReported = false,
            reportCount = 0
        )
        
        return try {
            firestore.collection(NEWS_COLLECTION).document(newsId)
                .collection(COMMENTS_COLLECTION).document(comment.id)
                .set(comment.toMap(newsId)).await()
            Result.Success(comment)
        } catch (e: Exception) {
            Result.Error(AppError.DatabaseError("Yorum eklenemedi: ${e.message}"))
        }
    }
    
    suspend fun editComment(newsId: String, commentId: String, newContent: String): Result<Unit> {
        val user = authManager.getCurrentUser() ?: return Result.Error(AppError.AuthError("Giriş yapmalısınız"))
        
        if (newContent.length !in MIN_COMMENT_LENGTH..MAX_COMMENT_LENGTH) {
            return Result.Error(AppError.ValidationError("Yorum $MIN_COMMENT_LENGTH-$MAX_COMMENT_LENGTH karakter arasında olmalıdır"))
        }
        
        if (profanityFilter.containsProfanity(newContent)) {
            return Result.Error(AppError.ValidationError("Yorumunuz uygunsuz içerik barındırıyor"))
        }
        
        return try {
            val commentRef = firestore.collection(NEWS_COLLECTION).document(newsId)
                .collection(COMMENTS_COLLECTION).document(commentId)
            val doc = commentRef.get().await()
            
            if (!doc.exists()) return Result.Error(AppError.ValidationError("Yorum bulunamadı"))
            if (doc.getString("userId") != user.uid) return Result.Error(AppError.AuthError("Bu yorumu düzenleme yetkiniz yok"))
            
            commentRef.update(mapOf(
                "text" to newContent,
                "editedAt" to Instant.now().toEpochMilli(),
                "edited" to true
            )).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(AppError.DatabaseError("Yorum düzenlenemedi: ${e.message}"))
        }
    }
    
    suspend fun deleteComment(newsId: String, commentId: String): Result<Unit> {
        val user = authManager.getCurrentUser() ?: return Result.Error(AppError.AuthError("Giriş yapmalısınız"))
        
        return try {
            val commentRef = firestore.collection(NEWS_COLLECTION).document(newsId)
                .collection(COMMENTS_COLLECTION).document(commentId)
            val doc = commentRef.get().await()
            
            if (!doc.exists()) return Result.Error(AppError.ValidationError("Yorum bulunamadı"))
            if (doc.getString("userId") != user.uid) return Result.Error(AppError.AuthError("Bu yorumu silme yetkiniz yok"))
            
            commentRef.delete().await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(AppError.DatabaseError("Yorum silinemedi: ${e.message}"))
        }
    }
    
    fun getComments(newsId: String): Flow<List<Comment>> = callbackFlow {
        val listener = firestore.collection(NEWS_COLLECTION).document(newsId)
            .collection(COMMENTS_COLLECTION)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(PAGE_SIZE.toLong())
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val comments = snapshot?.documents?.mapNotNull { it.toComment() } ?: emptyList()
                trySend(comments)
            }
        awaitClose { listener.remove() }
    }
    
    suspend fun reportComment(newsId: String, commentId: String, reason: String): Result<Unit> {
        val user = authManager.getCurrentUser() ?: return Result.Error(AppError.AuthError("Giriş yapmalısınız"))
        
        return try {
            firestore.collection("reported_comments").document("${newsId}_${commentId}").set(mapOf(
                "newsId" to newsId, "commentId" to commentId, "reportedBy" to user.uid,
                "reason" to reason, "reportedAt" to Instant.now().toEpochMilli(), "status" to "pending"
            )).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(AppError.DatabaseError("Bildirim gönderilemedi: ${e.message}"))
        }
    }
    
    private fun Comment.toMap(newsId: String) = mapOf(
        "id" to id, "newsId" to newsId, "userId" to userId, "userName" to userName,
        "userPhotoUrl" to userPhotoUrl, "text" to text,
        "timestamp" to timestamp.toEpochMilli(), "editedAt" to editedAt?.toEpochMilli(),
        "edited" to edited, "isReported" to isReported, "reportCount" to reportCount
    )
    
    private fun com.google.firebase.firestore.DocumentSnapshot.toComment() = Comment(
        id = getString("id") ?: "",
        userId = getString("userId") ?: "", userName = getString("userName") ?: "",
        userPhotoUrl = getString("userPhotoUrl"),
        text = getString("text") ?: "",
        timestamp = getLong("timestamp")?.let { Instant.ofEpochMilli(it) } ?: Instant.now(),
        editedAt = getLong("editedAt")?.let { Instant.ofEpochMilli(it) },
        edited = getBoolean("edited") ?: false,
        isReported = getBoolean("isReported") ?: false,
        reportCount = getLong("reportCount")?.toInt() ?: 0
    )
}
