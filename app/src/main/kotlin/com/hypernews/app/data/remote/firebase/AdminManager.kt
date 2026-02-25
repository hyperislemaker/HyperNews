package com.hypernews.app.data.remote.firebase

import com.hypernews.app.domain.common.AppError
import com.hypernews.app.domain.common.Result
import com.hypernews.app.domain.model.AppStatistics
import com.hypernews.app.domain.model.ReportedComment
import com.hypernews.app.domain.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminManager @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authManager: AuthManager
) {
    companion object {
        private const val ADMINS_COLLECTION = "admins"
        private const val USERS_COLLECTION = "users"
        private const val REPORTED_COMMENTS_COLLECTION = "reported_comments"
        private const val NEWS_COLLECTION = "news_items"
    }
    
    private suspend fun isAdmin(): Boolean {
        val user = authManager.getCurrentUser() ?: return false
        return try {
            firestore.collection(ADMINS_COLLECTION).document(user.uid).get().await().exists()
        } catch (e: Exception) {
            false
        }
    }
    
    fun getReportedComments(): Flow<Result<List<ReportedComment>>> = flow {
        emit(Result.Loading)
        if (!isAdmin()) {
            emit(Result.Error(AppError.AuthError("Admin yetkisi gerekli")))
            return@flow
        }
        
        try {
            val docs = firestore.collection(REPORTED_COMMENTS_COLLECTION)
                .whereEqualTo("status", "pending").get().await()
            val reports = docs.documents.mapNotNull { doc ->
                ReportedComment(
                    id = doc.id,
                    commentId = doc.getString("commentId") ?: "",
                    newsId = doc.getString("newsId") ?: "",
                    reporterUserName = doc.getString("reporterUserName") ?: "",
                    commentContent = doc.getString("commentContent") ?: "",
                    reason = doc.getString("reason") ?: "",
                    reportCount = doc.getLong("reportCount")?.toInt() ?: 1,
                    reportedAt = doc.getLong("reportedAt") ?: System.currentTimeMillis()
                )
            }
            emit(Result.Success(reports))
        } catch (e: Exception) {
            emit(Result.Error(AppError.DatabaseError("Raporlar alınamadı: ${e.message}")))
        }
    }
    
    fun deleteComment(newsId: String, commentId: String): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        if (!isAdmin()) {
            emit(Result.Error(AppError.AuthError("Admin yetkisi gerekli")))
            return@flow
        }
        
        try {
            firestore.collection(NEWS_COLLECTION).document(newsId)
                .collection("comments").document(commentId).delete().await()
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(AppError.DatabaseError("Yorum silinemedi: ${e.message}")))
        }
    }
    
    fun rejectReport(reportId: String): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        if (!isAdmin()) {
            emit(Result.Error(AppError.AuthError("Admin yetkisi gerekli")))
            return@flow
        }
        
        try {
            firestore.collection(REPORTED_COMMENTS_COLLECTION).document(reportId)
                .update(mapOf("status" to "rejected", "resolvedAt" to System.currentTimeMillis())).await()
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(AppError.DatabaseError("Rapor reddedilemedi: ${e.message}")))
        }
    }
    
    fun banUser(userId: String): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        if (!isAdmin()) {
            emit(Result.Error(AppError.AuthError("Admin yetkisi gerekli")))
            return@flow
        }
        
        try {
            firestore.collection(USERS_COLLECTION).document(userId)
                .update("isBanned", true).await()
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(AppError.DatabaseError("Kullanıcı engellenemedi: ${e.message}")))
        }
    }
    
    fun unbanUser(userId: String): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        if (!isAdmin()) {
            emit(Result.Error(AppError.AuthError("Admin yetkisi gerekli")))
            return@flow
        }
        
        try {
            firestore.collection(USERS_COLLECTION).document(userId)
                .update("isBanned", false).await()
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(AppError.DatabaseError("Engel kaldırılamadı: ${e.message}")))
        }
    }
    
    fun getBannedUsers(): Flow<Result<List<UserProfile>>> = flow {
        emit(Result.Loading)
        if (!isAdmin()) {
            emit(Result.Error(AppError.AuthError("Admin yetkisi gerekli")))
            return@flow
        }
        
        try {
            val docs = firestore.collection(USERS_COLLECTION)
                .whereEqualTo("isBanned", true).get().await()
            val users = docs.documents.map { doc ->
                UserProfile(
                    id = doc.id,
                    userName = doc.getString("userName") ?: "",
                    email = doc.getString("email") ?: "",
                    profileImageUrl = doc.getString("profileImageUrl"),
                    isBanned = true,
                    banReason = doc.getString("banReason")
                )
            }
            emit(Result.Success(users))
        } catch (e: Exception) {
            emit(Result.Error(AppError.DatabaseError("Engelli kullanıcılar alınamadı: ${e.message}")))
        }
    }
    
    fun getAppStatistics(): Flow<Result<AppStatistics>> = flow {
        emit(Result.Loading)
        if (!isAdmin()) {
            emit(Result.Error(AppError.AuthError("Admin yetkisi gerekli")))
            return@flow
        }
        
        try {
            val usersCount = firestore.collection(USERS_COLLECTION).get().await().size()
            val pendingReports = firestore.collection(REPORTED_COMMENTS_COLLECTION)
                .whereEqualTo("status", "pending").get().await().size()
            
            emit(Result.Success(AppStatistics(
                totalUsers = usersCount,
                totalComments = 0,
                totalReactions = 0,
                totalReports = pendingReports,
                activeUsers = 0,
                mostActiveUsers = emptyList()
            )))
        } catch (e: Exception) {
            emit(Result.Error(AppError.DatabaseError("İstatistikler alınamadı: ${e.message}")))
        }
    }
}
