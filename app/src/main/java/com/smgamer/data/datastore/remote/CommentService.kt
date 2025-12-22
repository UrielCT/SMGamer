package com.smgamer.data.datastore.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.smgamer.data.model.CommentDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentService @Inject constructor(
    private val firestore: FirebaseFirestore,
) {
    private val commentsCollection = firestore.collection("Comments")

    // create comment
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