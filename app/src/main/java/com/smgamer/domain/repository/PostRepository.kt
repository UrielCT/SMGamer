package com.smgamer.domain.repository

import com.smgamer.domain.model.Post

interface PostRepository {
    suspend fun createPost(
        idUser:String,
        title: String,
        description: String,
        category: String,
        images:List<String>,
    ): Result<Post>


}