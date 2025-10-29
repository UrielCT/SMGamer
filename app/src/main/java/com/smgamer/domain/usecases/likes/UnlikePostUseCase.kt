package com.smgamer.domain.usecases.likes

import com.smgamer.domain.repository.LikeRepository
import javax.inject.Inject

class UnlikePostUseCase @Inject constructor(
    private val repository: LikeRepository
) {
    suspend operator fun invoke(postId: String, userId: String) = repository.unlikePost(postId, userId)
}