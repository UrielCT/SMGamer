package com.smgamer.data.datastore.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.smgamer.data.model.PostDto
import com.smgamer.data.model.UserDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val usersCollection = firestore.collection("Users")
    private val postsCollection = firestore.collection("Posts")

    /********** USERS *********/

    // Guardar o actualiza usuario
    suspend fun saveUser(user: UserDto) {
        usersCollection.document(user.id).set(user).await()
    }

    // Crear usuario solo si no existe
    suspend fun createUserIfNotExists(user: UserDto) {
        val doc = usersCollection.document(user.id).get().await()
        if (!doc.exists()) {
            usersCollection.document(user.id).set(user).await()
        }
    }

    // traer usuario por ID
    suspend fun getUserById(userId: String): UserDto? {
        val doc = usersCollection.document(userId).get().await()
        return if (doc.exists()) doc.toObject(UserDto::class.java) else null
    }

    // actualizar usuario
    suspend fun updateUser(userDto: UserDto) {
        usersCollection.document(userDto.id).set(userDto).await()
    }


    /********** POSTS *********/

    // Crear post
    suspend fun createPost(
        idUser: String,
        title: String,
        description: String,
        category: String,
        images: List<String>,
    ): PostDto {
        val id = postsCollection.document().id
        val post = PostDto(
            id = id,
            idUser = idUser,
            title = title,
            description = description,
            category = category,
            images = images,
            timestamp = System.currentTimeMillis()
        )
        postsCollection.document(id).set(post).await()
        return post
    }

}