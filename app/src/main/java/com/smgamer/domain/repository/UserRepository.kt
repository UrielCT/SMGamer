package com.smgamer.domain.repository

import com.google.firebase.auth.AuthCredential
import com.smgamer.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun signInWithGoogle(credential: AuthCredential): Result<User>
    suspend fun signInWithEmail(email: String, password: String): Result<User>
    suspend fun signUpWithEmail(email: String,
                                password: String,
                                username: String,
                                phone: String): Result<User>
    suspend fun createUser(email: String, password: String, username: String, phone: String): Result<User>
    suspend fun getCurrentUser(): User?
    suspend fun getUserById(userId: String): User?
    suspend fun logout()
    suspend fun updateUser(user: User)

    fun getUserByIdFlow(userId: String): Flow<User?>

    fun setupRealtimeConnectionTracking()
    fun setUserOnlineStatus(isOnline: Boolean)
    fun observeUserOnlineStatus(userId: String, onStatusChange: (Boolean, Long) -> Unit)
}
