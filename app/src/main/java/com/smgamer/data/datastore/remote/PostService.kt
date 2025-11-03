package com.smgamer.data.datastore.remote

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.smgamer.data.mappers.toDomain
import com.smgamer.data.model.LikeDto
import com.smgamer.data.model.PostDto
import com.smgamer.domain.model.Post
import com.smgamer.domain.model.PostData
import com.smgamer.domain.model.User
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch
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


    // --- getAllPosts (existing) ---
    fun getAllPostsFlow(): Flow<List<PostData>> = callbackFlow {
        val userCache = mutableMapOf<String, User>()
        val postDataMap = mutableMapOf<String, PostData>()
        val activeLikeJobs = mutableMapOf<String, Job>()

        val queryRef = postsCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)

        val postListener = queryRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val posts = snapshot?.toObjects(Post::class.java) ?: emptyList()

            // Si no hay posts, lo limpio
            if (posts.isEmpty()) {
                postDataMap.clear()
                activeLikeJobs.values.forEach { it.cancel() }
                activeLikeJobs.clear()
                trySend(emptyList())
                return@addSnapshotListener
            }

            // Cancelar jobs de posts que ya no existen
            val currentIds = posts.map { it.id }
            val removedIds = activeLikeJobs.keys - currentIds.toSet()
            removedIds.forEach { id ->
                activeLikeJobs[id]?.cancel()
                activeLikeJobs.remove(id)
                postDataMap.remove(id)
            }

            // Escuchar likes y usuarios de cada post
            posts.forEach { post ->
                if (activeLikeJobs[post.id] == null) {
                    activeLikeJobs[post.id] = launch {

                        /**  ARREGLAR  **/

//                        val user: User? = userCache[post.idUser] ?: getUserById(post.idUser)
//                            ?.toDomain()
//                            ?.also { userCache[post.idUser] = it }
//
//                        combine(
//                            getLikesByPostIdFlow(post.id),
//                            getCommentsByPostFlow(post.id)
//                        ) { likes, comments ->
//                            PostData(
//                                post = post,
//                                user = user,
//                                likes = likes.map { it.toDomain() },
//                                comments = comments.map { it.toDomain() }
//                            )
//                        }.collectLatest { postData ->
//                            postDataMap[post.id] = postData
//                            trySend(postDataMap.values.sortedByDescending { it.post.timestamp })
//                        }
                    }
                }
            }
        }

        awaitClose {
            postListener.remove()
            activeLikeJobs.values.forEach { it.cancel() }
        }
    }



    fun getPostsByTitleFlow(query: String): Flow<List<PostData>> = callbackFlow {
        val userCache = mutableMapOf<String, User>()
        val postDataMap = mutableMapOf<String, PostData>()
        val activeLikeJobs = mutableMapOf<String, Job>()


        val queryRef = postsCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)

        val postListener = queryRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val allPosts = snapshot?.toObjects(Post::class.java) ?: emptyList()

            // filtro en memoria (insensible a mayúsculas)
            val posts = if (query.isBlank()) {
                allPosts
            } else {
                allPosts.filter {
                    it.title.contains(query, ignoreCase = true)
                }
            }

            // Si no hay resultados, limpio todo
            if (posts.isEmpty()) {
                postDataMap.clear()
                activeLikeJobs.values.forEach { it.cancel() }
                activeLikeJobs.clear()
                trySend(emptyList())
                return@addSnapshotListener
            }

            // Cancelo jobs de posts que ya no están
            val currentIds = posts.map { it.id }
            val removedIds = activeLikeJobs.keys - currentIds.toSet()
            removedIds.forEach { id ->
                activeLikeJobs[id]?.cancel()
                activeLikeJobs.remove(id)
                postDataMap.remove(id)
            }

            // Procesa cada post y escucha sus likes
            posts.forEach { post ->
                if (activeLikeJobs[post.id] == null) {
                    activeLikeJobs[post.id] = launch {

                        /**  ARREGLAR  **/

//                        val user: User? = userCache[post.idUser] ?: getUserById(post.idUser)
//                            ?.toDomain()
//                            ?.also { userCache[post.idUser] = it }
//
//                        combine(
//                            getLikesByPostIdFlow(post.id),
//                            getCommentsByPostFlow(post.id)
//                        ) { likes, comments ->
//                            PostData(
//                                post = post,
//                                user = user,
//                                likes = likes.map { it.toDomain() },
//                                comments = comments.map { it.toDomain() }
//                            )
//                        }.collectLatest { postData ->
//                            postDataMap[post.id] = postData
//                            trySend(postDataMap.values.sortedByDescending { it.post.timestamp })
//                        }
                    }
                }
            }
        }

        awaitClose {
            postListener.remove()
            activeLikeJobs.values.forEach { it.cancel() }
        }
    }




    fun getPostsByCategoryFlow(category: String): Flow<List<PostData>> = callbackFlow {
        val userCache = mutableMapOf<String, User>()
        val postDataMap = mutableMapOf<String, PostData>()
        val activeLikeJobs = mutableMapOf<String, Job>()

        // 🔸 Filtramos por categoría
        val queryRef = postsCollection
            .whereEqualTo("category", category)
        //.orderBy("timestamp", Query.Direction.DESCENDING)

        val postListener = queryRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val posts = snapshot?.toObjects(Post::class.java) ?: emptyList()

            // Si no hay posts, limpio todo
            if (posts.isEmpty()) {
                postDataMap.clear()
                activeLikeJobs.values.forEach { it.cancel() }
                activeLikeJobs.clear()
                trySend(emptyList())
                return@addSnapshotListener
            }

            // Cancelo jobs de posts que ya no existen
            val currentIds = posts.map { it.id }
            val removedIds = activeLikeJobs.keys - currentIds.toSet()
            removedIds.forEach { id ->
                activeLikeJobs[id]?.cancel()
                activeLikeJobs.remove(id)
                postDataMap.remove(id)
            }

            // Escucho likes, comentarios y usuario por cada post
            posts.forEach { post ->
                if (activeLikeJobs[post.id] == null) {
                    activeLikeJobs[post.id] = launch {

                        /**  ARREGLAR  **/

//                        val user: User? = userCache[post.idUser] ?: getUserById(post.idUser)
//                            ?.toDomain()
//                            ?.also { userCache[post.idUser] = it }
//
//                        combine(
//                            getLikesByPostIdFlow(post.id),
//                            getCommentsByPostFlow(post.id)
//                        ) { likes, comments ->
//                            PostData(
//                                post = post,
//                                user = user,
//                                likes = likes.map { it.toDomain() },
//                                comments = comments.map { it.toDomain() }
//                            )
//                        }.collectLatest { postData ->
//                            postDataMap[post.id] = postData
//                            trySend(
//                                postDataMap.values.sortedByDescending { it.post.timestamp }
//                            )
//                        }
                    }
                }
            }
        }

        awaitClose {
            postListener.remove()
            activeLikeJobs.values.forEach { it.cancel() }
        }
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
    }.distinctUntilChangedBy { it.map { post -> post.id } } // ✅ Esto asegura que Compose detecte cambios



    // FirestoreService.kt (añadir)
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

}