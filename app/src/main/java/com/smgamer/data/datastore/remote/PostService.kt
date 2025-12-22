package com.smgamer.data.datastore.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.smgamer.data.mappers.toDomain
import com.smgamer.data.model.PostDto
import com.smgamer.domain.model.Post
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostService @Inject constructor(
    private val firestore: FirebaseFirestore,
) {
    /********** POSTS *********/
    private val postsCollection = firestore.collection("Posts")

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


    // --- deletePost ---
    suspend fun deletePost(postId: String) {
        postsCollection.document(postId).delete().await()
    }

    // --- getPostById ---
    suspend fun getPostById(postId: String): PostDto? {
        val doc = postsCollection.document(postId).get().await()
        return if (doc.exists()) doc.toObject(PostDto::class.java)?.copy(id = doc.id) else null
    }

    // ----  getPostByIdFlow ---
    fun getPostByIdFlow(postId: String): Flow<PostDto?> = callbackFlow {
        val listener = postsCollection
            .document(postId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val dto = snapshot?.toObject(PostDto::class.java)?.copy(id = snapshot.id)
                trySend(dto).isSuccess
            }

        awaitClose { listener.remove() }
    }


    // --- getAllPostsFlow (existing) ---
    fun getAllPostsFlow(): Flow<List<PostDto>> = callbackFlow {
        val listener = postsCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val posts = snapshot?.toObjects(PostDto::class.java) ?: emptyList()
                trySend(posts).isSuccess
            }
        awaitClose { listener.remove() }
    }



    fun getPostsByTitleFlow(query: String): Flow<List<PostDto>> = callbackFlow {
        val listener = postsCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val posts = snapshot?.toObjects(PostDto::class.java)
                    ?.filter { it.title.contains(query, ignoreCase = true) }
                    ?: emptyList()
                trySend(posts).isSuccess
            }
        awaitClose { listener.remove() }
    }


    fun getPostsByCategoryFlow(category: String): Flow<List<PostDto>> = callbackFlow {
        val listener = postsCollection
            .whereEqualTo("category", category)
            //.orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val posts = snapshot?.toObjects(PostDto::class.java) ?: emptyList()
                trySend(posts).isSuccess
            }

        awaitClose { listener.remove() }
    }


    // --- getPostsByUserIdFlow (filter by idUser, ordered desc by timestamp) ---
    fun getPostsByUserIdFlow(userId: String): Flow<List<Post>> = callbackFlow {
        val listener = postsCollection
            .whereEqualTo("idUser", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }

                val posts = snapshot?.documents
                    ?.mapNotNull { it.toObject(PostDto::class.java)?.toDomain()?.copy(id = it.id) }
                    ?: emptyList()

                trySend(posts).isSuccess
            }

        awaitClose { listener.remove() }
    }.distinctUntilChangedBy { it.map { post -> post.id } } // asegura que Compose detecte cambios

}