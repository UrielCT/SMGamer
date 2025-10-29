package com.smgamer.domain.usecases.posts

import com.smgamer.domain.model.Post
import com.smgamer.domain.repository.PostRepository
import javax.inject.Inject

class GetPostByIdUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(postId: String): Result<Post?> = repository.getPostById(postId)
}