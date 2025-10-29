package com.smgamer.domain.usecases.comments

import com.smgamer.domain.model.Comment
import com.smgamer.domain.repository.CommentRepository
import javax.inject.Inject

class CreateCommentUseCase @Inject constructor(
    private val repository: CommentRepository
) {
    suspend operator fun invoke(text: String, idUser: String, idPost: String): Result<Comment> {
        return repository.createComment(text, idUser, idPost)
    }
}