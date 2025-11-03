package com.smgamer.data.datastore.remote

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.smgamer.data.model.LikeDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LikeService @Inject constructor(
    private val firestore: FirebaseFirestore,
) {
    /********** LIKES *********/
    private val likesCollection = firestore.collection("Likes")


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
}