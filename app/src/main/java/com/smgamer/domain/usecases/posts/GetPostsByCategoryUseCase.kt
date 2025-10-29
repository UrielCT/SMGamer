package com.smgamer.domain.usecases.posts

import com.smgamer.domain.model.Post
import com.smgamer.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPostsByCategoryUseCase @Inject constructor(
    private val repository: PostRepository
) {
    operator fun invoke(category: String): Flow<List<Post>> = repository.getPostsByCategoryAndTimestampFlow(category)
}