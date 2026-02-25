package com.hypernews.app.data.remote.firebase

import com.hypernews.app.domain.common.AppError
import com.hypernews.app.domain.common.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthManager @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    fun getCurrentUser(): FirebaseUser? = firebaseAuth.currentUser
    val isLoggedIn: Boolean get() = getCurrentUser() != null
    
    fun observeAuthState(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth -> trySend(auth.currentUser) }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }
    
    fun signInWithGoogle(): Flow<Result<FirebaseUser>> = flow {
        emit(Result.Loading)
        // Note: In real implementation, this would use Google Sign-In SDK
        // For now, emit error as it requires Activity context
        emit(Result.Error(AppError.AuthError("Google Sign-In requires Activity context")))
    }
    
    fun signInWithEmail(email: String, password: String): Flow<Result<FirebaseUser>> = flow {
        emit(Result.Loading)
        try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            result.user?.let { 
                emit(Result.Success(it)) 
            } ?: emit(Result.Error(AppError.AuthError("Giriş başarısız")))
        } catch (e: Exception) {
            emit(Result.Error(AppError.AuthError(e.message ?: "Giriş başarısız")))
        }
    }
    
    fun signUpWithEmail(email: String, password: String): Flow<Result<FirebaseUser>> = flow {
        emit(Result.Loading)
        try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { 
                emit(Result.Success(it)) 
            } ?: emit(Result.Error(AppError.AuthError("Kayıt başarısız")))
        } catch (e: Exception) {
            emit(Result.Error(AppError.AuthError(e.message ?: "Kayıt başarısız")))
        }
    }
    
    fun signOut() = firebaseAuth.signOut()
    
    suspend fun deleteAccount(): Result<Unit> = try {
        getCurrentUser()?.delete()?.await()
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(AppError.AuthError(e.message ?: "Hesap silinemedi"))
    }
}
