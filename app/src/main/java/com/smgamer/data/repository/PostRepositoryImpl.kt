package com.smgamer.data.repository

import com.smgamer.data.datastore.remote.FirestoreService
import com.smgamer.data.mappers.toDomain
import com.smgamer.domain.model.Post
import com.smgamer.domain.model.User
import com.smgamer.domain.repository.PostRepository
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val firestoreService: FirestoreService,
): PostRepository {


    override suspend fun createPost(
        idUser: String,
        title: String,
        description: String,
        category: String,
        images: List<String>,
    ): Result<Post> {
        return try {
            val postDto = firestoreService.createPost(idUser, title, description, category, images )
            Result.success(postDto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}