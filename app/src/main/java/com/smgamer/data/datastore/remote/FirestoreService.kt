package com.smgamer.data.datastore.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.smgamer.data.model.UserDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val usersCollection = firestore.collection("Users")

    // 🔹 Guarda o actualiza un usuario
    suspend fun saveUser(user: UserDto) {
        usersCollection.document(user.id)
            .set(user)
            .await()
    }

    // 🔹 Crea usuario solo si no existe
    suspend fun createUserIfNotExists(user: UserDto) {
        val doc = usersCollection.document(user.id).get().await()
        if (!doc.exists()) {
            usersCollection.document(user.id)
                .set(user)
                .await()
        }
    }

    // 🔹 Obtiene usuario por ID
    suspend fun getUserById(userId: String): UserDto? {
        val doc = usersCollection.document(userId).get().await()
        return if (doc.exists()) doc.toObject(UserDto::class.java) else null
    }
}