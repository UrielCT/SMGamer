package com.smgamer.domain.usecases.posts

import com.smgamer.domain.model.PostData
import com.smgamer.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllPostsUseCase @Inject constructor(
    private val repository: PostRepository
) {
    operator fun invoke(): Flow<List<PostData>> {
        return repository.getAllPostsFlow()
    }
}