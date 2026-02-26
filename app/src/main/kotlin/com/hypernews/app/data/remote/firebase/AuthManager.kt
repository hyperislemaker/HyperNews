package com.hypernews.app.data.remote.firebase

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.hypernews.app.domain.common.AppError
import com.hypernews.app.domain.common.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthManager @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "AuthManager"
        private const val WEB_CLIENT_ID = "306625625799-19rnfublglq1vmsov5t8ve5mqv05nk9f.apps.googleusercontent.com"
    }
    
    private val credentialManager = CredentialManager.create(context)
    
    fun getCurrentUser(): FirebaseUser? = firebaseAuth.currentUser
    val isLoggedIn: Boolean get() = getCurrentUser() != null
    
    fun observeAuthState(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth -> trySend(auth.currentUser) }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }
    
    private fun generateNonce(): String {
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
    
    suspend fun signInWithGoogle(activityContext: Context): Result<FirebaseUser> {
        return try {
            val nonce = generateNonce()
            
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(WEB_CLIENT_ID)
                .setAutoSelectEnabled(false)
                .setNonce(nonce)
                .build()
            
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
            
            Log.d(TAG, "Requesting Google credential...")
            val result = credentialManager.getCredential(activityContext, request)
            Log.d(TAG, "Got credential response")
            handleSignInResult(result)
        } catch (e: NoCredentialException) {
            Log.e(TAG, "No credential available", e)
            Result.Error(AppError.AuthError("Google hesabı bulunamadı. Lütfen cihazınıza bir Google hesabı ekleyin."))
        } catch (e: GetCredentialCancellationException) {
            Log.e(TAG, "Credential request cancelled", e)
            Result.Error(AppError.AuthError("Giriş iptal edildi"))
        } catch (e: GetCredentialException) {
            Log.e(TAG, "GetCredentialException: ${e.type}", e)
            Result.Error(AppError.AuthError("Google ile giriş başarısız: ${e.message}"))
        } catch (e: Exception) {
            Log.e(TAG, "Unknown error during Google sign-in", e)
            Result.Error(AppError.AuthError(e.message ?: "Google ile giriş başarısız"))
        }
    }
    
    private suspend fun handleSignInResult(result: GetCredentialResponse): Result<FirebaseUser> {
        val credential = result.credential
        
        return when (credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val idToken = googleIdTokenCredential.idToken
                    
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()
                    
                    authResult.user?.let {
                        Result.Success(it)
                    } ?: Result.Error(AppError.AuthError("Google ile giriş başarısız"))
                } else {
                    Result.Error(AppError.AuthError("Beklenmeyen credential tipi"))
                }
            }
            else -> Result.Error(AppError.AuthError("Beklenmeyen credential tipi"))
        }
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
