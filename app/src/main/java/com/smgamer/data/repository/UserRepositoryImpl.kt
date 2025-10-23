package com.smgamer.data.repository

import com.google.firebase.auth.AuthCredential
import com.smgamer.data.datastore.remote.FirebaseAuthService
import com.smgamer.data.datastore.remote.FirestoreService
import com.smgamer.data.mappers.toDomain
import com.smgamer.domain.repository.UserRepository
import com.smgamer.domain.model.User
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val authService: FirebaseAuthService,
    private val firestoreService: FirestoreService
): UserRepository {

    override suspend fun signInWithGoogle(credential: AuthCredential): Result<User> {
        return try {
            val userDto = authService.signInWithGoogle(credential)
            firestoreService.createUserIfNotExists(userDto)
            Result.success(userDto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<User> {
        return try {
            val userDto = authService.signInWithEmail(email, password)
            Result.success(userDto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUpWithEmail(
        email: String,
        password: String,
        username: String,
        phone: String
    ): Result<User> {
        return try {
            val userDto = authService.signUpWithEmail(email, password, username, phone)
            firestoreService.createUserIfNotExists(userDto)
            Result.success(userDto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createUser(email: String, password: String, username: String, phone: String): Result<User> {
        return try {
            val userDto = authService.createUser(email, password, username, phone)
            firestoreService.saveUser(userDto)
            Result.success(userDto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): User? {
        return authService.getCurrentUser()?.toDomain()
    }
}