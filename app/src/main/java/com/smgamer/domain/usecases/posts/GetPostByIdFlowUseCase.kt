package com.smgamer.domain.usecases.posts

import com.smgamer.domain.model.PostData
import com.smgamer.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// renombrar por GetPostById

class GetPostByIdFlowUseCase @Inject constructor(
    private val repository: PostRepository
) {
    operator fun invoke(postId:String):Flow<PostData?> = repository.getPostByIdFlow(postId)
}