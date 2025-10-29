package com.smgamer.domain.usecases.likes

import com.smgamer.domain.repository.LikeRepository
import com.smgamer.domain.repository.PostRepository
import javax.inject.Inject

class LikePostUseCase @Inject constructor(
    private val repository: LikeRepository
) {
    suspend operator fun invoke(postId: String, userId: String) = repository.likePost(postId, userId)
}