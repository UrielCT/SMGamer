package com.smgamer.data.datastore.remote

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.smgamer.data.model.UserDto
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class FirebaseAuthService @Inject constructor(
    private val auth: FirebaseAuth
) {
    suspend fun signInWithGoogle(credential: AuthCredential): UserDto {
        auth.signInWithCredential(credential).await()
        val user = auth.currentUser ?: throw Exception("No user found")
        return user.toUserDto()
    }

    suspend fun signInWithEmail(email: String, password: String): UserDto {
        auth.signInWithEmailAndPassword(email, password).await()
        val user = auth.currentUser ?: throw Exception("No user found")
        return user.toUserDto()
    }

    suspend fun signUpWithEmail(
        email: String,
        password: String,
        username: String,
        phone: String,

    ): UserDto {
        try {
            // 🔹 Intentar iniciar sesión
            auth.signInWithEmailAndPassword(email, password).await()
        } catch (e: Exception) {
            // 🔹 Si falla, crear nuevo usuario
            auth.createUserWithEmailAndPassword(email, password).await()
        }

        val user = auth.currentUser ?: throw Exception("Error al obtener usuario")
        return user.toUserDto(username, phone)
    }

    suspend fun createUser(
        email: String,
        password: String,
        username: String,
        phone: String
    ): UserDto {
        auth.createUserWithEmailAndPassword(email, password).await()
        val user = auth.currentUser ?: throw Exception("User creation failed")
        return user.toUserDto(username, phone)
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    fun logout() { auth.signOut() }
}

private fun FirebaseUser.toUserDto(username: String = displayName ?: "", phone: String = "") = UserDto(
    id = uid,
    email = email ?: "",
    username = username,
    phone = phone,
    profileImage = photoUrl?.toString() ?: "",
    timestamp = Date().time
)