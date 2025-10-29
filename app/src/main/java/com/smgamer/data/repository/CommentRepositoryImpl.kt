package com.smgamer.data.repository

import com.smgamer.data.datastore.remote.FirestoreService
import com.smgamer.data.mappers.toDomain
import com.smgamer.domain.model.Comment
import com.smgamer.domain.repository.CommentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    private val firestoreService: FirestoreService
): CommentRepository {

    override suspend fun createComment(text: String, idUser: String, idPost: String): Result<Comment> {
        return try {
            val dto = firestoreService.createComment(text, idUser, idPost)
            Result.success(dto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCommentsByPostFlow(postId: String): Flow<List<Comment>> {
        return firestoreService.getCommentsByPostFlow(postId)
            .map { list -> list.map { it.toDomain() } }
    }
}