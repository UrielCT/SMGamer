package com.smgamer.data.datastore.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.smgamer.data.mappers.toDomain
import com.smgamer.data.model.CommentDto
import com.smgamer.data.model.LikeDto
import com.smgamer.data.model.PostDto
import com.smgamer.data.model.UserDto
import com.smgamer.domain.model.Post
import com.smgamer.domain.model.PostData
import com.smgamer.domain.model.User
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val usersCollection = firestore.collection("Users")
    private val postsCollection = firestore.collection("Posts")
    private val likesCollection = firestore.collection("Likes")
    private val commentsCollection = firestore.collection("Comments")

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

    // cambiar a Flow
    // traer usuario por ID
    suspend fun getUserById(userId: String): UserDto? {
        return try {
            usersCollection.document(userId).get().await().toObject(UserDto::class.java)
        } catch (e: Exception) {
            null
        }
    }


    fun getUserByIdFlow(userId: String): Flow<UserDto?> = callbackFlow {
        val listener = usersCollection
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val user = snapshot?.toObject(UserDto::class.java)?.copy(id = snapshot.id)
                trySend(user).isSuccess
            }

        awaitClose { listener.remove() }
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
//    fun getAllPostsFlow(): Flow<List<PostDto>> = callbackFlow {
//        val listener = postsCollection
//            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
//            .addSnapshotListener { snapshot, error ->
//                if (error != null) {
//                    close(error) // termina el flujo con error
//                    return@addSnapshotListener
//                }
//
//                val posts = snapshot?.documents?.mapNotNull { it.toObject(PostDto::class.java) } ?: emptyList()
//                trySend(posts) // emite los nuevos valores
//            }
//
//        awaitClose { listener.remove() } // se llama cuando el Flow se cancela
//    }


    // --- getPostsByTitleFlow (prefix search, case-insensitive) ---
//    fun getPostsByTitleFlow(query: String): Flow<List<PostDto>> = callbackFlow {
//        if (query.isBlank()) {
//            trySend(emptyList())
//            awaitClose { }
//            return@callbackFlow
//        }
//
//        try {
//            val lower = query.lowercase()
//            val end = "$lower\uF8FF"
//            val listener = postsCollection
//                .orderBy("titleLowercase")
//                .startAt(lower)
//                .endAt(end)
//                .addSnapshotListener { snapshot, error ->
//                    if (error != null) { close(error); return@addSnapshotListener }
//                    val posts = snapshot?.toObjects(PostDto::class.java)?.map { it.copy(id = it.id) } ?: emptyList()
//                    trySend(posts)
//                }
//            awaitClose { listener.remove() }
//        } catch (e: Exception) {
//            close(e)
//        }
//    }


//    fun getAllPostsFlow(): Flow<List<PostData>> = callbackFlow {
//        val userCache = mutableMapOf<String, User>()
//        val postDataMap = mutableMapOf<String, PostData>()
//        val activeJobs = mutableMapOf<String, Job>()
//
//        val postListener = postsCollection
//            .orderBy("timestamp", Query.Direction.DESCENDING)
//            .addSnapshotListener { snapshot, error ->
//                if (error != null) { close(error); return@addSnapshotListener }
//
//                val posts = snapshot?.toObjects(Post::class.java) ?: emptyList()
//                val postIds = posts.map { it.id }
//
//                // Cancel jobs de posts eliminados
//                val removed = activeJobs.keys - postIds.toSet()
//                removed.forEach {
//                    activeJobs[it]?.cancel()
//                    activeJobs.remove(it)
//                    postDataMap.remove(it)
//                }
//
//                posts.forEach { post ->
//                    if (activeJobs[post.id] == null) {
//                        activeJobs[post.id] = launch {
//                            val user = userCache[post.idUser] ?: getUserById(post.idUser)?.toDomain()?.also {
//                                userCache[post.idUser] = it
//                            }
//
//                            combine(
//                                getLikesByPostIdFlow(post.id),
//                                getCommentsByPostFlow(post.id)
//                            ) { likes, comments ->
//                                PostData(
//                                    post = post,
//                                    user = user,
//                                    likes = likes.map { it.toDomain() },
//                                    comments = comments.map { it.toDomain() }
//                                )
//                            }.collectLatest { postData ->
//                                postDataMap[post.id] = postData
//                                trySend(postDataMap.values.sortedByDescending { it.post.timestamp })
//                            }
//                        }
//                    }
//                }
//            }
//
//        awaitClose {
//            postListener.remove()
//            activeJobs.values.forEach { it.cancel() }
//        }
//    }
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

            // Si no hay posts, limpiamos
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
                        val user: User? = userCache[post.idUser] ?: getUserById(post.idUser)
                            ?.toDomain()
                            ?.also { userCache[post.idUser] = it }

//                        getLikesByPostIdFlow(post.id)
//                            .collectLatest { likes ->
//                                postDataMap[post.id] = PostData(
//                                    post = post,
//                                    user = user,
//                                    likes = likes.map { it.toDomain() }
//                                )
//                                trySend(
//                                    postDataMap.values.sortedByDescending { it.post.timestamp }
//                                )
//                            }

                        combine(
                            getLikesByPostIdFlow(post.id),
                            getCommentsByPostFlow(post.id)
                        ) { likes, comments ->
                            PostData(
                                post = post,
                                user = user,
                                likes = likes.map { it.toDomain() },
                                comments = comments.map { it.toDomain() }
                            )
                        }.collectLatest { postData ->
                            postDataMap[post.id] = postData
                            trySend(postDataMap.values.sortedByDescending { it.post.timestamp })
                        }
                    }
                }
            }
        }

        awaitClose {
            postListener.remove()
            activeLikeJobs.values.forEach { it.cancel() }
        }
    }



//    fun getPostsByTitleFlow(query: String): Flow<List<PostData>> = callbackFlow {
//        val userCache = mutableMapOf<String, User>()
//        val postDataMap = mutableMapOf<String, PostData>()
//        val activeJobs = mutableMapOf<String, Job>()
//
//        val postListener = postsCollection
//            .orderBy("timestamp", Query.Direction.DESCENDING)
//            .addSnapshotListener { snapshot, error ->
//                if (error != null) { close(error); return@addSnapshotListener }
//
//                val allPosts = snapshot?.toObjects(Post::class.java) ?: emptyList()
//                val posts = if (query.isBlank()) allPosts else allPosts.filter {
//                    it.title.contains(query, ignoreCase = true)
//                }
//
//                val postIds = posts.map { it.id }
//                val removed = activeJobs.keys - postIds.toSet()
//                removed.forEach {
//                    activeJobs[it]?.cancel()
//                    activeJobs.remove(it)
//                    postDataMap.remove(it)
//                }
//
//                posts.forEach { post ->
//                    if (activeJobs[post.id] == null) {
//                        activeJobs[post.id] = launch {
//                            val user = userCache[post.idUser] ?: getUserById(post.idUser)?.toDomain()?.also {
//                                userCache[post.idUser] = it
//                            }
//
//                            combine(
//                                getLikesByPostIdFlow(post.id),
//                                getCommentsByPostFlow(post.id)
//                            ) { likes, comments ->
//                                PostData(
//                                    post = post,
//                                    user = user,
//                                    likes = likes.map { it.toDomain() },
//                                    comments = comments.map { it.toDomain() }
//                                )
//                            }.collectLatest { postData ->
//                                postDataMap[post.id] = postData
//                                trySend(postDataMap.values.sortedByDescending { it.post.timestamp })
//                            }
//                        }
//                    }
//                }
//            }
//
//        awaitClose {
//            postListener.remove()
//            activeJobs.values.forEach { it.cancel() }
//        }
//    }
    fun getPostsByTitleFlow(query: String): Flow<List<PostData>> = callbackFlow {
        val userCache = mutableMapOf<String, User>()
        val postDataMap = mutableMapOf<String, PostData>()
        val activeLikeJobs = mutableMapOf<String, Job>()

        // 🔹 Siempre ordenamos por timestamp
        val queryRef = postsCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)

        val postListener = queryRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val allPosts = snapshot?.toObjects(Post::class.java) ?: emptyList()

            // 🔹 Si hay texto, filtramos en memoria (insensible a mayúsculas)
            val posts = if (query.isBlank()) {
                allPosts
            } else {
                allPosts.filter {
                    it.title.contains(query, ignoreCase = true)
                }
            }

            // 🔹 Si no hay resultados, limpiamos todo
            if (posts.isEmpty()) {
                postDataMap.clear()
                activeLikeJobs.values.forEach { it.cancel() }
                activeLikeJobs.clear()
                trySend(emptyList())
                return@addSnapshotListener
            }

            // 🔹 Cancelar jobs de posts que ya no están
            val currentIds = posts.map { it.id }
            val removedIds = activeLikeJobs.keys - currentIds.toSet()
            removedIds.forEach { id ->
                activeLikeJobs[id]?.cancel()
                activeLikeJobs.remove(id)
                postDataMap.remove(id)
            }

            // 🔹 Procesar cada post y escuchar sus likes
            posts.forEach { post ->
                if (activeLikeJobs[post.id] == null) {
                    activeLikeJobs[post.id] = launch {
                        val user: User? = userCache[post.idUser] ?: getUserById(post.idUser)
                            ?.toDomain()
                            ?.also { userCache[post.idUser] = it }

//                        getLikesByPostIdFlow(post.id)
//                            .collectLatest { likes ->
//                                postDataMap[post.id] = PostData(
//                                    post = post,
//                                    user = user,
//                                    likes = likes.map { it.toDomain() },
//
//                                )
//                                trySend(
//                                    postDataMap.values.sortedByDescending { it.post.timestamp }
//                                )
//                            }
                        combine(
                                getLikesByPostIdFlow(post.id),
                                getCommentsByPostFlow(post.id)
                            ) { likes, comments ->
                                PostData(
                                    post = post,
                                    user = user,
                                    likes = likes.map { it.toDomain() },
                                    comments = comments.map { it.toDomain() }
                                )
                            }.collectLatest { postData ->
                                postDataMap[post.id] = postData
                                trySend(postDataMap.values.sortedByDescending { it.post.timestamp })
                            }
                    }
                }
            }
        }

        awaitClose {
            postListener.remove()
            activeLikeJobs.values.forEach { it.cancel() }
        }
    }



    // --- getPostsByCategoryAndTimestampFlow ---
    fun getPostsByCategoryAndTimestampFlow(category: String): Flow<List<PostDto>> = callbackFlow {
        val listener = postsCollection
            .whereEqualTo("category", category)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val posts = snapshot?.documents?.mapNotNull { it.toObject(PostDto::class.java)?.copy(id = it.id) } ?: emptyList()
                trySend(posts).isSuccess
            }
        awaitClose { listener.remove() }
    }

    // --- getPostsByUserIdFlow (filter by idUser, ordered desc by timestamp) ---
    fun getPostsByUserIdFlow(userId: String): Flow<List<PostDto>> = callbackFlow {
        val listener = postsCollection
            .whereEqualTo("idUser", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val posts = snapshot?.documents?.mapNotNull { it.toObject(PostDto::class.java)?.copy(id = it.id) } ?: emptyList()
                trySend(posts).isSuccess
            }
        awaitClose { listener.remove() }
    }






    /********** LIKES *********/

    // create like
    suspend fun createLike(
        idUser: String,
        idPost: String,
    ): LikeDto {
        val id = likesCollection.document().id
        val like = LikeDto(
            id = id,
            idUser = idUser,
            idPost = idPost,
            timestamp = System.currentTimeMillis()
        )
        likesCollection.document(id).set(like).await()
        return like
    }


    //get likes by postID
    fun getLikesByPostIdFlow(postId: String): Flow<List<LikeDto>> = callbackFlow {
        val listener = likesCollection
            .whereEqualTo("idPost", postId)
            //.orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val likes = snapshot?.documents?.mapNotNull { it.toObject(LikeDto::class.java)?.copy(id = it.id) } ?: emptyList()
                trySend(likes).isSuccess
            }
        awaitClose { listener.remove() }
    }

    // get likes by postId and userId
    fun getLikesByPostAndUserFlow(postId: String, userId: String): Flow<List<LikeDto>> = callbackFlow {
        val listener = likesCollection
            .whereEqualTo("idPost", postId)
            .whereEqualTo("idUser", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }

                val likes = snapshot?.documents?.mapNotNull {
                    it.toObject(LikeDto::class.java)?.copy(id = it.id)
                } ?: emptyList()
                trySend(likes).isSuccess
            }
        awaitClose { listener.remove() }
    }

    // delete
    suspend fun deleteLike(likeId: String) {
        likesCollection.document(likeId).delete().await()
    }



    /********** COMMENTS *********/

    // create like
    suspend fun createComment(
        text:String,
        idUser: String,
        idPost: String,
    ): CommentDto {
        val id = commentsCollection.document().id
        val comment = CommentDto(
            id = id,
            comment = text,
            idUser = idUser,
            idPost = idPost,
            timestamp = System.currentTimeMillis()
        )
        commentsCollection.document(id).set(comment).await()
        return comment
    }

//    fun getCommentsByPostFlow(postId: String): Flow<List<CommentDto>> = callbackFlow {
//        val listener = commentsCollection
//            .whereEqualTo("idPost", postId)
//            .addSnapshotListener { snapshot, error ->
//                if (error != null) { close(error); return@addSnapshotListener }
//
//                val comments = snapshot?.documents?.mapNotNull {
//                    it.toObject(CommentDto::class.java)?.copy(id = it.id)
//                } ?: emptyList()
//                trySend(comments).isSuccess
//            }
//        awaitClose { listener.remove() }
//    }

    fun getCommentsByPostFlow(postId: String): Flow<List<CommentDto>> = callbackFlow {
        val listener = commentsCollection
            .whereEqualTo("idPost", postId)
            //.orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }

                val comments = snapshot?.documents?.mapNotNull {
                    it.toObject(CommentDto::class.java)?.copy(id = it.id)
                } ?: emptyList()
                trySend(comments).isSuccess
            }
        awaitClose { listener.remove() }
    }
}