package com.smgamer.data.datastore.remote

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.smgamer.data.mappers.toDomain
import com.smgamer.data.model.ChatDto
import com.smgamer.data.model.CommentDto
import com.smgamer.data.model.LikeDto
import com.smgamer.data.model.MessageDto
import com.smgamer.data.model.PostDto
import com.smgamer.data.model.UserDto
import com.smgamer.domain.model.Message
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreService @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuthService,
    private val realtimeDatabase: FirebaseDatabase
) {
    //private val usersCollection = firestore.collection("Users")
    //private val postsCollection = firestore.collection("Posts")
    //private val likesCollection = firestore.collection("Likes")
    //private val commentsCollection = firestore.collection("Comments")
    //private val chatsCollection = firestore.collection("Chats")
    //private val messagesCollection = realtimeDatabase.getReference("Messages")

//    /********** USERS *********/
//
//    // Corrige usuarios que tengan el campo viejo "online" y lo reemplaza por "isOnline"
//    suspend fun fixUsersOnlineField() {
//        try {
//            val snapshot = usersCollection.get().await()
//            for (doc in snapshot.documents) {
//                val data = doc.data ?: continue
//                val hasOldField = data.containsKey("online")
//                val hasNewField = data.containsKey("isOnline")
//
//                if (hasOldField && !hasNewField) {
//                    val onlineValue = data["online"] as? Boolean ?: false
//                    doc.reference.update(
//                        mapOf(
//                            "isOnline" to onlineValue,
//                            "online" to com.google.firebase.firestore.FieldValue.delete()
//                        )
//                    ).await()
//                } else if (hasOldField && hasNewField) {
//                    // Limpia el campo viejo si aún existe
//                    doc.reference.update("online", com.google.firebase.firestore.FieldValue.delete()).await()
//                }
//            }
//            println(" Campos 'online' reemplazados por 'isOnline' correctamente.")
//        } catch (e: Exception) {
//            println(" Error al corregir campos duplicados: ${e.message}")
//        }
//    }
//
//
//    // Guardar o actualiza usuario
//    suspend fun saveUser(user: UserDto) {
//        usersCollection.document(user.id).set(user).await()
//    }
//
//    // Crear usuario solo si no existe
//    suspend fun createUserIfNotExists(user: UserDto) {
//        val doc = usersCollection.document(user.id).get().await()
//        if (!doc.exists()) {
//            usersCollection.document(user.id).set(user).await()
//        }
//    }
//
//    // cambiar a Flow
//    // traer usuario por ID
//    suspend fun getUserById(userId: String): UserDto? {
//        return try {
//            usersCollection.document(userId).get().await().toObject(UserDto::class.java)
//        } catch (e: Exception) {
//            null
//        }
//    }
//
//
//    fun getUserByIdFlow(userId: String): Flow<UserDto?> = callbackFlow {
//        val listener = usersCollection
//            .document(userId)
//            .addSnapshotListener { snapshot, error ->
//                if (error != null) {
//                    close(error)
//                    return@addSnapshotListener
//                }
//
//                val user = snapshot?.toObject(UserDto::class.java)?.copy(id = snapshot.id)
//                trySend(user).isSuccess
//            }
//
//        awaitClose { listener.remove() }
//    }
//
//
//
//
//
//    // actualizar usuario
//    suspend fun updateUser(userDto: UserDto) {
//        usersCollection.document(userDto.id).set(userDto).await()
//    }
//
//    suspend fun updateUserOnlineStatus(userId: String, isOnline: Boolean) {
//        val updateData = if (isOnline) {
//            mapOf("isOnline" to true)
//        } else {
//            mapOf(
//                "isOnline" to false,
//                "lastConnection" to System.currentTimeMillis()
//            )
//        }
//        usersCollection.document(userId).update(updateData).await()
//    }
//
//
//    private val userStatusRef: DatabaseReference
//        get() = realtimeDatabase.reference
//            .child("users_status")
//            .child(auth.getCurrentUser()?.uid ?: "")
//
//    fun setupRealtimeConnectionTracking() {
//        val connectedRef = realtimeDatabase.getReference(".info/connected")
//        connectedRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val connected = snapshot.getValue(Boolean::class.java) ?: false
//                if (connected) {
//                    userStatusRef.onDisconnect().updateChildren(
//                        mapOf(
//                            "online" to false,
//                            "lastSeen" to ServerValue.TIMESTAMP
//                        )
//                    )
//                    userStatusRef.updateChildren(
//                        mapOf(
//                            "online" to true,
//                            "lastSeen" to ServerValue.TIMESTAMP
//                        )
//                    )
//                }
//            }
//            override fun onCancelled(error: DatabaseError) {}
//        })
//    }
//
//    fun setUserOnlineStatus(isOnline: Boolean) {
//        val status = mapOf(
//            "online" to isOnline,
//            "lastSeen" to ServerValue.TIMESTAMP
//        )
//        userStatusRef.updateChildren(status)
//    }
//
//    fun observeUserOnlineStatus(
//        userId: String,
//        onStatusChange: (Boolean, Long) -> Unit
//    ): ValueEventListener {
//        val ref = realtimeDatabase.reference.child("users_status").child(userId)
//        val listener = object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val online = snapshot.child("online").getValue(Boolean::class.java) ?: false
//                val lastSeen = snapshot.child("lastSeen").getValue(Long::class.java) ?: 0L
//                onStatusChange(online, lastSeen)
//            }
//            override fun onCancelled(error: DatabaseError) {}
//        }
//        ref.addValueEventListener(listener)
//        return listener
//    }
//
//    fun removeListener(userId: String, listener: ValueEventListener) {
//        realtimeDatabase.reference.child("users_status").child(userId)
//            .removeEventListener(listener)
//    }



//    /********** POSTS *********/
//
//    // Crear post
//    suspend fun createPost(
//        idUser: String,
//        title: String,
//        description: String,
//        category: String,
//        images: List<String>,
//    ): PostDto {
//        val id = postsCollection.document().id
//        val post = PostDto(
//            id = id,
//            idUser = idUser,
//            title = title,
//            description = description,
//            category = category,
//            images = images,
//            timestamp = System.currentTimeMillis()
//        )
//        postsCollection.document(id).set(post).await()
//        return post
//    }
//
//
//    // --- deletePost ---
//    suspend fun deletePost(postId: String) {
//        postsCollection.document(postId).delete().await()
//    }
//
//    // --- getPostById ---
//    suspend fun getPostById(postId: String): PostDto? {
//        val doc = postsCollection.document(postId).get().await()
//        return if (doc.exists()) doc.toObject(PostDto::class.java)?.copy(id = doc.id) else null
//    }
//
//
//    // --- getAllPosts (existing) ---
//    fun getAllPostsFlow(): Flow<List<PostData>> = callbackFlow {
//        val userCache = mutableMapOf<String, User>()
//        val postDataMap = mutableMapOf<String, PostData>()
//        val activeLikeJobs = mutableMapOf<String, Job>()
//
//        val queryRef = postsCollection
//            .orderBy("timestamp", Query.Direction.DESCENDING)
//
//        val postListener = queryRef.addSnapshotListener { snapshot, error ->
//            if (error != null) {
//                close(error)
//                return@addSnapshotListener
//            }
//
//            val posts = snapshot?.toObjects(Post::class.java) ?: emptyList()
//
//            // Si no hay posts, lo limpio
//            if (posts.isEmpty()) {
//                postDataMap.clear()
//                activeLikeJobs.values.forEach { it.cancel() }
//                activeLikeJobs.clear()
//                trySend(emptyList())
//                return@addSnapshotListener
//            }
//
//            // Cancelar jobs de posts que ya no existen
//            val currentIds = posts.map { it.id }
//            val removedIds = activeLikeJobs.keys - currentIds.toSet()
//            removedIds.forEach { id ->
//                activeLikeJobs[id]?.cancel()
//                activeLikeJobs.remove(id)
//                postDataMap.remove(id)
//            }
//
//            // Escuchar likes y usuarios de cada post
//            posts.forEach { post ->
//                if (activeLikeJobs[post.id] == null) {
//                    activeLikeJobs[post.id] = launch {
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
//                    }
//                }
//            }
//        }
//
//        awaitClose {
//            postListener.remove()
//            activeLikeJobs.values.forEach { it.cancel() }
//        }
//    }
//
//
//
//    fun getPostsByTitleFlow(query: String): Flow<List<PostData>> = callbackFlow {
//        val userCache = mutableMapOf<String, User>()
//        val postDataMap = mutableMapOf<String, PostData>()
//        val activeLikeJobs = mutableMapOf<String, Job>()
//
//
//        val queryRef = postsCollection
//            .orderBy("timestamp", Query.Direction.DESCENDING)
//
//        val postListener = queryRef.addSnapshotListener { snapshot, error ->
//            if (error != null) {
//                close(error)
//                return@addSnapshotListener
//            }
//
//            val allPosts = snapshot?.toObjects(Post::class.java) ?: emptyList()
//
//            // filtro en memoria (insensible a mayúsculas)
//            val posts = if (query.isBlank()) {
//                allPosts
//            } else {
//                allPosts.filter {
//                    it.title.contains(query, ignoreCase = true)
//                }
//            }
//
//            // Si no hay resultados, limpio todo
//            if (posts.isEmpty()) {
//                postDataMap.clear()
//                activeLikeJobs.values.forEach { it.cancel() }
//                activeLikeJobs.clear()
//                trySend(emptyList())
//                return@addSnapshotListener
//            }
//
//            // Cancelo jobs de posts que ya no están
//            val currentIds = posts.map { it.id }
//            val removedIds = activeLikeJobs.keys - currentIds.toSet()
//            removedIds.forEach { id ->
//                activeLikeJobs[id]?.cancel()
//                activeLikeJobs.remove(id)
//                postDataMap.remove(id)
//            }
//
//            // Procesa cada post y escucha sus likes
//            posts.forEach { post ->
//                if (activeLikeJobs[post.id] == null) {
//                    activeLikeJobs[post.id] = launch {
//                        val user: User? = userCache[post.idUser] ?: getUserById(post.idUser)
//                            ?.toDomain()
//                            ?.also { userCache[post.idUser] = it }
//
//                        combine(
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
//                    }
//                }
//            }
//        }
//
//        awaitClose {
//            postListener.remove()
//            activeLikeJobs.values.forEach { it.cancel() }
//        }
//    }
//
//
//
//
//    fun getPostsByCategoryFlow(category: String): Flow<List<PostData>> = callbackFlow {
//        val userCache = mutableMapOf<String, User>()
//        val postDataMap = mutableMapOf<String, PostData>()
//        val activeLikeJobs = mutableMapOf<String, Job>()
//
//        // 🔸 Filtramos por categoría
//        val queryRef = postsCollection
//            .whereEqualTo("category", category)
//            //.orderBy("timestamp", Query.Direction.DESCENDING)
//
//        val postListener = queryRef.addSnapshotListener { snapshot, error ->
//            if (error != null) {
//                close(error)
//                return@addSnapshotListener
//            }
//
//            val posts = snapshot?.toObjects(Post::class.java) ?: emptyList()
//
//            // Si no hay posts, limpio todo
//            if (posts.isEmpty()) {
//                postDataMap.clear()
//                activeLikeJobs.values.forEach { it.cancel() }
//                activeLikeJobs.clear()
//                trySend(emptyList())
//                return@addSnapshotListener
//            }
//
//            // Cancelo jobs de posts que ya no existen
//            val currentIds = posts.map { it.id }
//            val removedIds = activeLikeJobs.keys - currentIds.toSet()
//            removedIds.forEach { id ->
//                activeLikeJobs[id]?.cancel()
//                activeLikeJobs.remove(id)
//                postDataMap.remove(id)
//            }
//
//            // Escucho likes, comentarios y usuario por cada post
//            posts.forEach { post ->
//                if (activeLikeJobs[post.id] == null) {
//                    activeLikeJobs[post.id] = launch {
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
//                    }
//                }
//            }
//        }
//
//        awaitClose {
//            postListener.remove()
//            activeLikeJobs.values.forEach { it.cancel() }
//        }
//    }
//
//
//    // --- getPostsByUserIdFlow (filter by idUser, ordered desc by timestamp) ---
//    fun getPostsByUserIdFlow(userId: String): Flow<List<Post>> = callbackFlow {
//        val listener = postsCollection
//            .whereEqualTo("idUser", userId)
//            .orderBy("timestamp", Query.Direction.DESCENDING)
//            .addSnapshotListener { snapshot, error ->
//                if (error != null) { close(error); return@addSnapshotListener }
//
//                val posts = snapshot?.documents
//                    ?.mapNotNull { it.toObject(PostDto::class.java)?.toDomain()?.copy(id = it.id) }
//                    ?: emptyList()
//
//                trySend(posts).isSuccess
//            }
//
//        awaitClose { listener.remove() }
//    }.distinctUntilChangedBy { it.map { post -> post.id } } // ✅ Esto asegura que Compose detecte cambios
//
//
//
//    // FirestoreService.kt (añadir)
//    fun getPostByIdFlow(postId: String): Flow<PostDto?> = callbackFlow {
//        val listener = postsCollection
//            .document(postId)
//            .addSnapshotListener { snapshot, error ->
//                if (error != null) {
//                    close(error)
//                    return@addSnapshotListener
//                }
//                val dto = snapshot?.toObject(PostDto::class.java)?.copy(id = snapshot.id)
//                trySend(dto).isSuccess
//            }
//
//        awaitClose { listener.remove() }
//    }





//    /********** LIKES *********/
//
//    // create like
//    suspend fun createLike(
//        idUser: String,
//        idPost: String,
//    ): LikeDto {
//        val id = likesCollection.document().id
//        val like = LikeDto(
//            id = id,
//            idUser = idUser,
//            idPost = idPost,
//            timestamp = System.currentTimeMillis()
//        )
//        likesCollection.document(id).set(like).await()
//        return like
//    }
//
//
//    //get likes by postID
//    fun getLikesByPostIdFlow(postId: String): Flow<List<LikeDto>> = callbackFlow {
//        val listener = likesCollection
//            .whereEqualTo("idPost", postId)
//            //.orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
//            .addSnapshotListener { snapshot, error ->
//                if (error != null) { close(error); return@addSnapshotListener }
//                val likes = snapshot?.documents?.mapNotNull { it.toObject(LikeDto::class.java)?.copy(id = it.id) } ?: emptyList()
//                trySend(likes).isSuccess
//            }
//        awaitClose { listener.remove() }
//    }
//
//    // get likes by postId and userId
//    fun getLikesByPostAndUserFlow(postId: String, userId: String): Flow<List<LikeDto>> = callbackFlow {
//        val listener = likesCollection
//            .whereEqualTo("idPost", postId)
//            .whereEqualTo("idUser", userId)
//            .addSnapshotListener { snapshot, error ->
//                if (error != null) { close(error); return@addSnapshotListener }
//
//                val likes = snapshot?.documents?.mapNotNull {
//                    it.toObject(LikeDto::class.java)?.copy(id = it.id)
//                } ?: emptyList()
//                trySend(likes).isSuccess
//            }
//        awaitClose { listener.remove() }
//    }
//
//    // delete
//    suspend fun deleteLike(likeId: String) {
//        likesCollection.document(likeId).delete().await()
//    }



//    /********** COMMENTS *********/
//
//    // create like
//    suspend fun createComment(
//        text:String,
//        idUser: String,
//        idPost: String,
//    ): CommentDto {
//        val id = commentsCollection.document().id
//        val comment = CommentDto(
//            id = id,
//            comment = text,
//            idUser = idUser,
//            idPost = idPost,
//            timestamp = System.currentTimeMillis()
//        )
//        commentsCollection.document(id).set(comment).await()
//        return comment
//    }
//
//    fun getCommentsByPostFlow(postId: String): Flow<List<CommentDto>> = callbackFlow {
//        val listener = commentsCollection
//            .whereEqualTo("idPost", postId)
//            //.orderBy("timestamp", Query.Direction.ASCENDING)
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





    /*********  CHATS  **********/

//    suspend fun createChat(chatDto: ChatDto) {
//        chatsCollection.document(chatDto.id).set(chatDto).await()
//    }
//
//    suspend fun getChatByUsers(idUserA: String, idUserB: String): ChatDto? {
//        val query = chatsCollection
//            .whereArrayContains("ids", idUserA)
//            .get().await()
//        val found = query.documents.mapNotNull { it.toObject(ChatDto::class.java)?.copy(id = it.id) }
//            .find { dto -> dto.ids.containsAll(listOf(idUserA, idUserB)) }
//        return found
//    }
//
//    fun getChatByIdFlow(chatId: String): Flow<ChatDto?> = callbackFlow {
//        val listener = chatsCollection.document(chatId).addSnapshotListener { snapshot, error ->
//            if (error != null) { close(error); return@addSnapshotListener }
//            val dto = snapshot?.toObject(ChatDto::class.java)?.copy(id = snapshot.id)
//            trySend(dto).isSuccess
//        }
//        awaitClose { listener.remove() }
//    }
//
////    fun getChatsByUserFlow(userId: String): Flow<List<ChatDto>> = callbackFlow {
////        val listener = chatsCollection
////            .whereArrayContains("ids", userId)
////            .orderBy("timestamp", Query.Direction.DESCENDING)
////            .addSnapshotListener { snapshot, error ->
////                if (error != null) { close(error); return@addSnapshotListener }
////                val list = snapshot?.toObjects(ChatDto::class.java)?.map { it.copy(id = it.id) } ?: emptyList()
////                trySend(list).isSuccess
////            }
////        awaitClose { listener.remove() }
////    }
//
//    fun getChatsByUserFlow(userId: String): Flow<List<ChatDto>> = callbackFlow {
//        val listener = chatsCollection
//            .whereArrayContains("ids", userId)
//            .addSnapshotListener { snapshot, error ->
//                if (error != null) {
//                    close(error)
//                    return@addSnapshotListener
//                }
//
//                val chats = snapshot?.documents?.mapNotNull {
//                    it.toObject(ChatDto::class.java)?.copy(id = it.id)
//                }?.sortedByDescending { it.timestamp } ?: emptyList()
//
//                trySend(chats).isSuccess
//            }
//
//        awaitClose { listener.remove() }
//    }






    /*********** MESSAGES ***********/

//    suspend fun sendMessage(messageDto: MessageDto) {
//        val ref = messagesCollection.child(messageDto.idChat).push()
//        val id = ref.key ?: return
//        ref.setValue(messageDto.copy(id = id)).await()
//    }
//
////    fun getMessagesByChatFlow(chatId: String): Flow<List<MessageDto>> = callbackFlow {
////        val listener = messagesCollection.child(chatId)
////            .addValueEventListener(object : ValueEventListener {
////                override fun onDataChange(snapshot: DataSnapshot) {
////                    val list = snapshot.children.mapNotNull { it.getValue(MessageDto::class.java) }
////                    trySend(list.sortedByDescending { it.timestamp }).isSuccess
////                }
////                override fun onCancelled(error: DatabaseError) {
////                    close(error.toException())
////                }
////            })
////        awaitClose { messagesCollection.child(chatId).removeEventListener(listener) }
////    }
//
////    fun getMessagesByChatFlow(chatId: String): Flow<List<MessageDto>> = callbackFlow {
////        val listener = firestore.collection("messages")
////            .whereEqualTo("idChat", chatId)
////            .orderBy("timestamp", Query.Direction.DESCENDING)
////            .addSnapshotListener { snapshot, error ->
////                if (error != null) {
////                    close(error)
////                    return@addSnapshotListener
////                }
////                val messages = snapshot?.documents?.mapNotNull { it.toObject(MessageDto::class.java) } ?: emptyList()
////                trySend(messages)
////            }
////        awaitClose { listener.remove() }
////    }
//    fun getMessagesByChatFlow(chatId: String): Flow<List<MessageDto>> = callbackFlow {
//        val ref = messagesCollection.child(chatId)
//        val listener = object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val list = snapshot.children
//                    .mapNotNull { it.getValue(MessageDto::class.java)?.copy(id = it.key ?: "") }
//                    .sortedByDescending { it.timestamp }
//                trySend(list).isSuccess
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                close(error.toException())
//            }
//        }
//        ref.addValueEventListener(listener)
//        awaitClose { ref.removeEventListener(listener) }
//    }
//
//    suspend fun markMessagesAsViewed(chatId: String, userId: String) {
//        val messagesRef = messagesCollection.child(chatId)
//        val snapshot = messagesRef.get().await()
//        snapshot.children.forEach { messageSnapshot ->
//            val msg = messageSnapshot.getValue(MessageDto::class.java)
//            if (msg != null && msg.idReceiver == userId && !msg.viewed) {
//                messageSnapshot.ref.child("viewed").setValue(true)
//            }
//        }
//    }
//
//
//    suspend fun updateViewed(chatId: String, messageId: String, viewed: Boolean) {
//        messagesCollection.child(chatId).child(messageId).child("viewed").setValue(viewed).await()
//    }
//
//    fun getLastMessageByChatFlow(chatId: String): Flow<MessageDto?> =
//        getMessagesByChatFlow(chatId).map { list -> list.maxByOrNull { it.timestamp } }
//
//
//
//    fun getMessagesByChatAndSenderFlow(chatId: String, senderId: String): Flow<List<MessageDto>> =
//        getMessagesByChatFlow(chatId).map { list -> list.filter { it.idSender == senderId } }
//
//    fun getLastMessagesByChatAndSenderFlow(chatId: String, senderId: String, limit: Int = 3): Flow<List<MessageDto>> =
//        getMessagesByChatAndSenderFlow(chatId, senderId).map { it.take(limit) }
}