package com.smgamer.domain.repository

import com.smgamer.domain.model.Comment
import kotlinx.coroutines.flow.Flow

interface CommentRepository {
    suspend fun createComment(text: String, idUser: String, idPost: String): Result<Comment>
    fun getCommentsByPostFlow(postId: String): Flow<List<Comment>>
}