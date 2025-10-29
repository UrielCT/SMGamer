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

    //suspend fun getAllPosts(): Result<List<Post>>
    //fun getAllPostsFlow(): Flow<List<Post>>
    //fun getPostsByTitleFlow(query: String): Flow<List<PostData>>


    suspend fun deletePost(postId: String): Result<Unit>
    suspend fun getPostById(postId: String): Result<Post?>
    fun getPostsByCategoryAndTimestampFlow(category: String): Flow<List<Post>>
    fun getPostsByUserIdFlow(userId: String): Flow<List<Post>>

    fun getAllPostsFlow(): Flow<List<PostData>>
    fun getPostsByTitleFlow(query: String): Flow<List<PostData>>

}