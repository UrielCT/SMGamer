package com.smgamer.data.repository

import com.google.firebase.auth.AuthCredential
import com.smgamer.data.datastore.local.UserLocalDataSource
import com.smgamer.data.datastore.remote.FirebaseAuthService
import com.smgamer.data.datastore.remote.FirestoreService
import com.smgamer.data.mappers.toDomain
import com.smgamer.data.mappers.toDto
import com.smgamer.domain.repository.UserRepository
import com.smgamer.domain.model.User
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val authService: FirebaseAuthService,
    private val firestoreService: FirestoreService,
    private val localDataSource: UserLocalDataSource
): UserRepository {

    override suspend fun signInWithGoogle(credential: AuthCredential): Result<User> {
        return try {
            val userDto = authService.signInWithGoogle(credential)
            firestoreService.createUserIfNotExists(userDto)
            val fullUser = firestoreService.getUserById(userDto.id) ?: userDto
            val user = fullUser.toDomain()

            //guardar user localmente
            localDataSource.saveUser(user)

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<User> {
        return try {
            val userDto = authService.signInWithEmail(email, password)
            val fullUser = firestoreService.getUserById(userDto.id) ?: userDto
            val user = fullUser.toDomain()

            //guardar user localmente
            localDataSource.saveUser(user)

            Result.success(user)
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
            val user = userDto.toDomain()

            //guardar user localmente
            localDataSource.saveUser(user)

            Result.success(user)
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
        val localUser = localDataSource.getUser()
        if (localUser != null) return localUser

        val firebaseUser = authService.getCurrentUser() ?: return null
        val firestoreUser = firestoreService.getUserById(firebaseUser.uid)

        val user = firestoreUser?.toDomain() ?: firebaseUser.toDomain()
        localDataSource.saveUser(user)

        return user
    }


    override suspend fun getUserById(userId: String): User? {
        return try {
            firestoreService.getUserById(userId)?.toDomain()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun logout() {
        authService.logout()
        localDataSource.clearUser()
    }

    override suspend fun updateUser(user: User) {
        firestoreService.updateUser(user.toDto())
        localDataSource.saveUser(user)
    }

}