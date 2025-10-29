package com.smgamer.data.repository

import com.smgamer.data.datastore.remote.FirestoreService
import com.smgamer.domain.repository.LikeRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class LikeRepositoryImpl @Inject constructor(
    private val firestoreService: FirestoreService
): LikeRepository {
    override suspend fun likePost(postId: String, userId: String) {
        firestoreService.createLike(userId, postId)
    }

    override suspend fun unlikePost(postId: String, userId: String) {
        val likes = firestoreService.getLikesByPostAndUserFlow(postId, userId).first()
        likes.forEach { firestoreService.deleteLike(it.id) }
    }
}