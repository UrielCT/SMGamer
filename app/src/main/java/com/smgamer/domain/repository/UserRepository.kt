package com.smgamer.domain.repository

import com.google.firebase.auth.AuthCredential
import com.smgamer.domain.model.User

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
}
