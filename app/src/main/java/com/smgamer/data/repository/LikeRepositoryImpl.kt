package com.smgamer.data.repository

import com.smgamer.data.datastore.remote.LikeService
import com.smgamer.domain.repository.LikeRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class LikeRepositoryImpl @Inject constructor(
    private val likeService: LikeService
): LikeRepository {
    override suspend fun likePost(postId: String, userId: String) {
        likeService.createLike(userId, postId)
    }

    override suspend fun unlikePost(postId: String, userId: String) {
        val likes = likeService.getLikesByPostAndUserFlow(postId, userId).first()
        likes.forEach { likeService.deleteLike(it.id) }
    }
}