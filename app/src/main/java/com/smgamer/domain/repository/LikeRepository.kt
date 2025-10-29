package com.smgamer.domain.repository

interface LikeRepository {
    suspend fun likePost(postId: String, userId: String)
    suspend fun unlikePost(postId: String, userId: String)
}