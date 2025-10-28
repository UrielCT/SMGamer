package com.smgamer.domain.usecases.posts

import com.smgamer.domain.model.Post
import com.smgamer.domain.repository.PostRepository
import javax.inject.Inject

class CreatePostUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(
        idUser: String,
        title: String,
        description: String,
        category: String,
        images: List<String>,
    ): Result<Post> {
        if (title.isBlank() || description.isBlank() || category.isBlank()) {
            return Result.failure(Exception("Faltan campos obligatorios"))
        }

        return repository.createPost(idUser, title, description, category, images)
    }
}