package com.smgamer.data.repository

import com.smgamer.data.datastore.remote.CommentService
import com.smgamer.data.datastore.remote.LikeService
import com.smgamer.data.datastore.remote.PostService
import com.smgamer.data.datastore.remote.UserService
import com.smgamer.data.mappers.toDomain
import com.smgamer.data.model.PostDto
import com.smgamer.domain.model.Post
import com.smgamer.domain.model.PostData
import com.smgamer.domain.repository.PostRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val postService: PostService,
    private val userService: UserService,
    private val likeService: LikeService,
    private val commentService: CommentService,
): PostRepository {

    override suspend fun createPost(
        idUser: String,
        title: String,
        description: String,
        category: String,
        images: List<String>,
    ): Result<Post> {
        return try {
            val postDto = postService.createPost(idUser, title, description, category, images )
            Result.success(postDto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAllPostsFlow(): Flow<List<PostData>> =
        postService.getAllPostsFlow().flatMapLatest { posts ->
            if (posts.isEmpty()) {
                flowOf(emptyList())
            } else {
                val postDataFlows = posts.map { combinePostData(it) }
                combine(postDataFlows) { it.toList() }
            }
        }


    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getPostsByTitleFlow(query: String): Flow<List<PostData>> =
        postService.getPostsByTitleFlow(query).flatMapLatest { posts ->
            if (posts.isEmpty()) {
                flowOf(emptyList())
            } else {
                val postDataFlows = posts.map { combinePostData(it) }
                combine(postDataFlows) { it.toList() }
            }
        }



    private fun combinePostData(post: PostDto): Flow<PostData> {
        val userFlow = userService.getUserByIdFlow(post.idUser)
        val likesFlow = likeService.getLikesByPostIdFlow(post.id)
        val commentsFlow = commentService.getCommentsByPostFlow(post.id)

        return combine(userFlow, likesFlow, commentsFlow) { user, likes, comments ->
            PostData(
                post = post.toDomain(),
                user = user?.toDomain(),
                likes = likes.map { it.toDomain() },
                comments = comments.map { it.toDomain() }
            )
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getPostsByCategoryFlow(category: String): Flow<List<PostData>> =
        postService.getPostsByCategoryFlow(category).flatMapLatest { posts ->
            if (posts.isEmpty()) {
                flowOf(emptyList())
            } else {
                val postDataFlows = posts.map { combinePostData(it) }
                combine(postDataFlows) { it.toList() }
            }
        }



    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getPostByIdFlow(postId: String): Flow<PostData?> {
        return postService.getPostByIdFlow(postId)
            .flatMapLatest { postDto ->
                if (postDto == null) {
                    // Si el post no existe, emito null
                    flowOf(null)
                } else {
                    combinePostData(postDto)
                }
            }.distinctUntilChanged()
    }


    override suspend fun getPostById(postId: String): Result<Post?> {
        return try {
            val dto = postService.getPostById(postId)
            Result.success(dto?.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getPostsByUserIdFlow(userId: String): Flow<List<Post>> =
        postService.getPostsByUserIdFlow(userId)

    override suspend fun deletePost(postId: String) {
        postService.deletePost(postId)
    }

}