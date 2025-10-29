package com.smgamer.domain.usecases.posts

import com.smgamer.domain.repository.PostRepository
import javax.inject.Inject

class DeletePostUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(postId: String): Result<Unit> = repository.deletePost(postId)
}