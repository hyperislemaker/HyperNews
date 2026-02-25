package com.example.telegramstyle.newsapp.domain.repository

import com.example.telegramstyle.newsapp.domain.model.UserProfile

/**
 * Repository interface for user account and profile operations.
 * Provides methods for managing user authentication and profile data.
 */
interface UserRepository {
    
    /**
     * Gets the currently authenticated user.
     *
     * @return Result containing the current user profile or null if not signed in
     */
    suspend fun getCurrentUser(): Result<UserProfile?>
    
    /**
     * Gets a user profile by user ID.
     *
     * @param userId Firebase UID of the user
     * @return Result containing the user profile or error
     */
    suspend fun getUserProfile(userId: String): Result<UserProfile>
    
    /**
     * Updates the current user's profile.
     *
     * @param userName New username (nullable to keep current)
     * @param photoUrl New photo URL (nullable to keep current)
     * @return Result containing the updated profile or error
     */
    suspend fun updateProfile(userName: String?, photoUrl: String?): Result<UserProfile>
    
    /**
     * Deletes the current user's account and all associated data.
     *
     * @return Result indicating success or failure
     */
    suspend fun deleteAccount(): Result<Unit>
    
    /**
     * Checks if a user is currently signed in.
     *
     * @return true if user is signed in, false otherwise
     */
    fun isUserSignedIn(): Boolean
}
