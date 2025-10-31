package com.smgamer.domain.usecases.comments

import com.smgamer.domain.model.CommentWithUser
import com.smgamer.domain.repository.CommentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCommentsByPostUseCase @Inject constructor(
    private val repository: CommentRepository
) {
    operator fun invoke(postId: String): Flow<List<CommentWithUser>> {
        return repository.getCommentsByPostFlow(postId)
    }
}