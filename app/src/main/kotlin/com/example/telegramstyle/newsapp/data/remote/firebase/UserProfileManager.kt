package com.example.telegramstyle.newsapp.data.remote.firebase

import com.example.telegramstyle.newsapp.domain.common.AppError
import com.example.telegramstyle.newsapp.domain.common.Result
import com.example.telegramstyle.newsapp.domain.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileManager @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    companion object {
        private const val USERS_COLLECTION = "users"
        private const val USERNAMES_COLLECTION = "usernames"
        private val USERNAME_PATTERN = Regex("^[a-zA-Z][a-zA-Z0-9_]{2,19}$")
    }
    
    fun createProfile(profile: UserProfile): Flow<Result<UserProfile>> = flow {
        emit(Result.Loading)
        
        if (!isValidUserName(profile.userName)) {
            emit(Result.Error(AppError.ValidationError("Geçersiz kullanıcı adı formatı")))
            return@flow
        }
        
        try {
            firestore.runTransaction { transaction ->
                transaction.set(
                    firestore.collection(USERNAMES_COLLECTION).document(profile.userName.lowercase()), 
                    mapOf("userId" to profile.id)
                )
                transaction.set(
                    firestore.collection(USERS_COLLECTION).document(profile.id), 
                    profile.toMap()
                )
            }.await()
            emit(Result.Success(profile))
        } catch (e: Exception) {
            emit(Result.Error(AppError.DatabaseError("Profil oluşturulamadı: ${e.message}")))
        }
    }
    
    fun getUserProfile(userId: String): Flow<Result<UserProfile>> = flow {
        emit(Result.Loading)
        try {
            val doc = firestore.collection(USERS_COLLECTION).document(userId).get().await()
            if (doc.exists()) {
                emit(Result.Success(doc.toUserProfile()))
            } else {
                emit(Result.Error(AppError.NotFoundError("Profil bulunamadı")))
            }
        } catch (e: Exception) {
            emit(Result.Error(AppError.DatabaseError("Profil alınamadı: ${e.message}")))
        }
    }
    
    fun updateProfile(userId: String, userName: String?, photoUrl: String?): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            val updates = mutableMapOf<String, Any?>()
            userName?.let { updates["userName"] = it }
            photoUrl?.let { updates["profileImageUrl"] = it }
            if (updates.isNotEmpty()) {
                firestore.collection(USERS_COLLECTION).document(userId)
                    .update(updates as Map<String, Any>).await()
            }
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(AppError.DatabaseError("Profil güncellenemedi: ${e.message}")))
        }
    }
    
    fun checkUserNameAvailability(userName: String): Flow<Result<Boolean>> = flow {
        emit(Result.Loading)
        try {
            val exists = firestore.collection(USERNAMES_COLLECTION)
                .document(userName.lowercase()).get().await().exists()
            emit(Result.Success(!exists))
        } catch (e: Exception) {
            emit(Result.Error(AppError.DatabaseError("Kontrol edilemedi: ${e.message}")))
        }
    }
    
    fun deleteProfile(userId: String): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            val profile = firestore.collection(USERS_COLLECTION).document(userId).get().await()
            val userName = profile.getString("userName")
            
            firestore.runTransaction { transaction ->
                transaction.delete(firestore.collection(USERS_COLLECTION).document(userId))
                userName?.let {
                    transaction.delete(firestore.collection(USERNAMES_COLLECTION).document(it.lowercase()))
                }
            }.await()
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(AppError.DatabaseError("Profil silinemedi: ${e.message}")))
        }
    }
    
    fun suggestUserNames(baseName: String): List<String> {
        val base = baseName.take(15).replace(Regex("[^a-zA-Z0-9]"), "")
        return listOf(
            "${base}${(100..999).random()}",
            "${base}_${(10..99).random()}",
            "${base}${System.currentTimeMillis() % 1000}"
        ).filter { isValidUserName(it) }
    }
    
    fun isValidUserName(userName: String): Boolean = USERNAME_PATTERN.matches(userName)
    
    private fun UserProfile.toMap() = mapOf(
        "id" to id, 
        "userName" to userName, 
        "email" to email,
        "profileImageUrl" to profileImageUrl, 
        "createdAt" to createdAt,
        "commentCount" to commentCount, 
        "reactionCount" to reactionCount, 
        "isBanned" to isBanned,
        "banReason" to banReason
    )
    
    private fun com.google.firebase.firestore.DocumentSnapshot.toUserProfile() = UserProfile(
        id = getString("id") ?: id,
        userName = getString("userName") ?: "",
        email = getString("email") ?: "",
        profileImageUrl = getString("profileImageUrl"),
        createdAt = getLong("createdAt") ?: System.currentTimeMillis(),
        commentCount = getLong("commentCount")?.toInt() ?: 0,
        reactionCount = getLong("reactionCount")?.toInt() ?: 0,
        isBanned = getBoolean("isBanned") ?: false,
        banReason = getString("banReason")
    )
}
