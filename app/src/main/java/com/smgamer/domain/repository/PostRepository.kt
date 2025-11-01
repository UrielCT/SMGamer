package com.smgamer.domain.repository

import com.smgamer.domain.model.Post
import com.smgamer.domain.model.PostData
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    suspend fun createPost(
        idUser:String,
        title: String,
        description: String,
        category: String,
        images:List<String>,
    ): Result<Post>

    suspend fun deletePost(postId: String)
    suspend fun getPostById(postId: String): Result<Post?>

    fun getPostByIdFlow(postId:String): Flow<PostData?>

    fun getPostsByCategoryFlow(category: String): Flow<List<PostData>>

    fun getPostsByUserIdFlow(userId: String): Flow<List<Post>>

    fun getAllPostsFlow(): Flow<List<PostData>>
    fun getPostsByTitleFlow(query: String): Flow<List<PostData>>

}