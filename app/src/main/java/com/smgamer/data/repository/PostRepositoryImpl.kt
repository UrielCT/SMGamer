package com.smgamer.data.repository

import com.smgamer.data.datastore.remote.FirestoreService
import com.smgamer.data.mappers.toDomain
import com.smgamer.domain.model.Post
import com.smgamer.domain.model.PostData
import com.smgamer.domain.repository.PostRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val firestoreService: FirestoreService,
): PostRepository {

    override suspend fun createPost(
        idUser: String,
        title: String,
        description: String,
        category: String,
        images: List<String>,
    ): Result<Post> {
        return try {
            val postDto = firestoreService.createPost(idUser, title, description, category, images )
            Result.success(postDto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getAllPostsFlow(): Flow<List<PostData>> {
        return firestoreService.getAllPostsFlow()
    }


    override fun getPostsByTitleFlow(query: String): Flow<List<PostData>> {
        return firestoreService.getPostsByTitleFlow(query)
    }

    override suspend fun deletePost(postId: String) {
        firestoreService.deletePost(postId)
    }

    override fun getPostsByCategoryFlow(category: String): Flow<List<PostData>> =
        firestoreService.getPostsByCategoryFlow(category)



    override fun getPostsByUserIdFlow(userId: String): Flow<List<Post>> =
        firestoreService.getPostsByUserIdFlow(userId)


    override suspend fun getPostById(postId: String): Result<Post?> {
        return try {
            val dto = firestoreService.getPostById(postId)
            Result.success(dto?.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getPostByIdFlow(postId: String): Flow<PostData?> {
        return firestoreService.getPostByIdFlow(postId)
            .flatMapLatest { postDto ->
                if (postDto == null) {
                    // Si el post no existe, emito null
                    flowOf(null)
                } else {
                    // Convertir PostDto -> Post domain si hace falta
                    val postDomainFlow = flowOf(postDto.toDomain())

                    // Flujos para user, likes y comments (desde FirestoreService)
                    val userFlow = firestoreService.getUserByIdFlow(postDto.idUser)
                        .map { it?.toDomain() } // user puede ser null

                    val likesFlow = firestoreService.getLikesByPostIdFlow(postId)
                        .map { list -> list.map { it.toDomain() } }

                    val commentsFlow = firestoreService.getCommentsByPostFlow(postId)
                        .map { list -> list.map { it.toDomain() } }

                    combine(
                        postDomainFlow,
                        userFlow,
                        likesFlow,
                        commentsFlow
                    ) { post, user, likes, comments ->
                        PostData(
                            post = post,
                            user = user,
                            likes = likes,
                            comments = comments
                        )
                    }
                }
            }.distinctUntilChanged()
    }


}










//    override suspend fun getAllPosts(): Result<List<Post>> {
//        return try {
//            val posts = firestoreService.getAllPosts().map { it.toDomain() }
//            Result.success(posts)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }



//    override fun getAllPostsFlow(): Flow<List<PostData>> = callbackFlow {
//        val userCache = mutableMapOf<String, User>()
//        val activeLikeJobs = mutableMapOf<String, Job>()
//        val postDataMap = mutableMapOf<String, PostData>()
//
//        val postListener = firestoreService.postsCollection
//            .orderBy("timestamp", Query.Direction.DESCENDING)
//            .addSnapshotListener { snapshot, error ->
//                if (error != null) {
//                    close(error)
//                    return@addSnapshotListener
//                }
//
//                val posts = snapshot?.toObjects(Post::class.java) ?: emptyList()
//
//                // 🔹 Cancelar listeners de likes de posts eliminados
//                val currentIds = posts.map { it.id }
//                val removedIds = activeLikeJobs.keys - currentIds.toSet()
//                removedIds.forEach { id ->
//                    activeLikeJobs[id]?.cancel()
//                    activeLikeJobs.remove(id)
//                    postDataMap.remove(id)
//                }
//
//                // 🔹 Crear o mantener listeners por post
//                posts.forEach { post ->
//                    if (activeLikeJobs[post.id] == null) {
//                        activeLikeJobs[post.id] = launch {
//                            // ✅ Traer usuario con cache y control de null
//                            val user: User? = userCache[post.idUser] ?: firestoreService
//                                .getUserById(post.idUser)
//                                ?.toDomain()
//                                ?.also { userCache[post.idUser] = it }
//
//                            // ✅ Escuchar los likes del post
//                            firestoreService.getLikesByPostIdFlow(post.id)
//                                .collectLatest { likes ->
//                                    postDataMap[post.id] = PostData(
//                                        post = post,
//                                        user = user,
//                                        likes = likes.map { it.toDomain() }
//                                    )
//                                    trySend(postDataMap.values.toList())
//                                }
//                        }
//                    }
//                }
//            }
//
//        awaitClose {
//            postListener.remove()
//            activeLikeJobs.values.forEach { it.cancel() }
//        }
//    }


//    override fun getPostsByTitleFlow(query: String): Flow<List<PostData>> = callbackFlow {
//        val userCache = mutableMapOf<String, User>()
//        val postDataMap = mutableMapOf<String, PostData>()
//        val activeLikeJobs = mutableMapOf<String, Job>()
//
//        // 🔹 Seleccionamos la query dependiendo del texto
//        val queryRef = if (query.isBlank()) {
//            firestoreService.postsCollection
//                .orderBy("timestamp", Query.Direction.DESCENDING)
//        } else {
//            firestoreService.postsCollection
//                .orderBy("title")
//                .startAt(query.lowercase())
//                .endAt("${query.lowercase()}\uF8FF")
//        }
//
//        val postListener = queryRef.addSnapshotListener { snapshot, error ->
//            if (error != null) {
//                close(error)
//                return@addSnapshotListener
//            }
//
//            val posts = snapshot?.toObjects(Post::class.java)?.sortedByDescending { it.timestamp }
//                ?: emptyList()
//
//            // 🔹 Si no hay resultados, limpiamos la lista actual
//            if (posts.isEmpty()) {
//                postDataMap.clear()
//                activeLikeJobs.values.forEach { it.cancel() }
//                activeLikeJobs.clear()
//                trySend(emptyList())
//                return@addSnapshotListener
//            }
//
//            // 🔹 Cancelar jobs de posts que ya no están
//            val currentIds = posts.map { it.id }
//            val removedIds = activeLikeJobs.keys - currentIds.toSet()
//            removedIds.forEach { id ->
//                activeLikeJobs[id]?.cancel()
//                activeLikeJobs.remove(id)
//                postDataMap.remove(id)
//            }
//
//            // 🔹 Procesar cada post y sus likes
//            posts.forEach { post ->
//                if (activeLikeJobs[post.id] == null) {
//                    activeLikeJobs[post.id] = launch {
//                        val user: User? = userCache[post.idUser] ?: firestoreService
//                            .getUserById(post.idUser)
//                            ?.toDomain()
//                            ?.also { userCache[post.idUser] = it }
//
//                        firestoreService.getLikesByPostIdFlow(post.id)
//                            .collectLatest { likes ->
//                                postDataMap[post.id] = PostData(
//                                    post = post,
//                                    user = user,
//                                    likes = likes.map { it.toDomain() }
//                                )
//                                // 🔹 Emitimos lista actualizada en cada cambio
//                                trySend(postDataMap.values.sortedByDescending { it.post.timestamp })
//                            }
//                    }
//                }
//            }
//        }
//
//        awaitClose {
//            postListener.remove()
//            activeLikeJobs.values.forEach { it.cancel() }
//        }
//
//    }

//    override fun getPostsByTitleFlow(query: String): Flow<List<PostData>> = callbackFlow {
//        val userCache = mutableMapOf<String, User>()
//        val postDataMap = mutableMapOf<String, PostData>()
//        val activeLikeJobs = mutableMapOf<String, Job>()
//
//        val postListener = firestoreService.postsCollection
//            .orderBy("titleLowercase")
//            .startAt(query.lowercase())
//            .endAt("${query.lowercase()}\uF8FF")
//            .addSnapshotListener { snapshot, error ->
//                if (error != null) {
//                    close(error)
//                    return@addSnapshotListener
//                }
//
//                val posts = snapshot?.toObjects(Post::class.java)?.sortedByDescending { it.timestamp } ?: emptyList()
//
//                val currentIds = posts.map { it.id }
//                val removedIds = activeLikeJobs.keys - currentIds.toSet()
//                removedIds.forEach { id ->
//                    activeLikeJobs[id]?.cancel()
//                    activeLikeJobs.remove(id)
//                    postDataMap.remove(id)
//                }
//
//                posts.forEach { post ->
//                    if (activeLikeJobs[post.id] == null) {
//                        activeLikeJobs[post.id] = launch {
//
//                            // ✅ Traer usuario con cache y control de null
//                            val user: User? = userCache[post.idUser] ?: firestoreService
//                                .getUserById(post.idUser)
//                                ?.toDomain()
//                                ?.also { userCache[post.idUser] = it }
//
//                            // ✅ Escuchar los likes del post
//                            firestoreService.getLikesByPostIdFlow(post.id)
//                                .collectLatest { likes ->
//                                    postDataMap[post.id] = PostData(
//                                        post = post,
//                                        user = user,
//                                        likes = likes.map { it.toDomain() }
//                                    )
//                                    trySend(postDataMap.values.toList())
//                                }
//
//                        }
//                    }
//                }
//            }
//
//        awaitClose {
//            postListener.remove()
//            activeLikeJobs.values.forEach { it.cancel() }
//        }
//    }

//    override fun getPostsByTitleFlow(query: String): Flow<List<PostData>> = callbackFlow {
//        val userCache = mutableMapOf<String, User>()
//        val postDataMap = mutableMapOf<String, PostData>()
//        val activeLikeJobs = mutableMapOf<String, Job>()
//
//        if (query.isBlank()) {
//            trySend(emptyList())
//            awaitClose { }
//            return@callbackFlow
//        }
//
//        val listener = firestoreService.postsCollection
//            .orderBy("titleLowercase")
//            .startAt(query.lowercase())
//            .endAt("${query.lowercase()}\uF8FF")
//            .addSnapshotListener { snapshot, error ->
//                if (error != null) {
//                    close(error)
//                    return@addSnapshotListener
//                }
//
//                val posts = snapshot?.toObjects(Post::class.java)
//                    ?.sortedByDescending { it.timestamp }
//                    ?: emptyList()
//
//                val currentIds = posts.map { it.id }
//                val removedIds = activeLikeJobs.keys - currentIds.toSet()
//                removedIds.forEach { id ->
//                    activeLikeJobs[id]?.cancel()
//                    activeLikeJobs.remove(id)
//                    postDataMap.remove(id)
//                }
//
//                posts.forEach { post ->
//                    if (activeLikeJobs[post.id] == null) {
//                        activeLikeJobs[post.id] = launch {
//                            val user: User? = userCache[post.idUser] ?: firestoreService
//                                .getUserById(post.idUser)
//                                ?.toDomain()
//                                ?.also { userCache[post.idUser] = it }
//
//                            firestoreService.getLikesByPostIdFlow(post.id)
//                                .collectLatest { likes ->
//                                    postDataMap[post.id] = PostData(
//                                        post = post,
//                                        user = user,
//                                        likes = likes.map { it.toDomain() }
//                                    )
//                                    trySend(postDataMap.values.toList())
//                                }
//                        }
//                    }
//                }
//            }
//
//        awaitClose {
//            listener.remove()
//            activeLikeJobs.values.forEach { it.cancel() }
//        }
//    }

