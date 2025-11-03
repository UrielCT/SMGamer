package com.smgamer.data.repository

import com.smgamer.data.datastore.remote.CommentService
import com.smgamer.data.datastore.remote.FirestoreService
import com.smgamer.data.datastore.remote.UserService
import com.smgamer.data.mappers.toDomain
import com.smgamer.domain.model.Comment
import com.smgamer.domain.model.CommentWithUser
import com.smgamer.domain.model.User
import com.smgamer.domain.repository.CommentRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    private val commentService: CommentService,
    private val userService: UserService,
): CommentRepository {

    override suspend fun createComment(text: String, idUser: String, idPost: String): Result<Comment> {
        return try {
            val dto = commentService.createComment(text, idUser, idPost)
            Result.success(dto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getCommentsByPostFlow(postId: String): Flow<List<CommentWithUser>> {
        return commentService.getCommentsByPostFlow(postId)
            .flatMapLatest { commentDtoList ->
                if (commentDtoList.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    // Lista de userIds únicos para evitar pedir el mismo user varias veces
                    val userIds = commentDtoList.map { it.idUser }.distinct()

                    // Crear un flow por cada userId
                    val userFlows: List<Flow<User?>> = userIds.map { id ->
                        userService.getUserByIdFlow(id).map { it?.toDomain() }
                    }

                    // Combinar todos los user flows en un único array de usuarios
                    combine(userFlows) { usersArray ->
                        val userMap: Map<String, User?> = userIds.zip(usersArray.asList()).toMap()

                        // Mapear cada comentario a su user correspondiente
                        commentDtoList.mapNotNull { commentDto ->
                            val user = userMap[commentDto.idUser]
                            if (user != null) {
                                CommentWithUser(comment = commentDto.toDomain(), user = user)
                            } else {
                                // Si el user no existe (rara vez), lo descartamos o podrías devolver con un user vacío
                                null
                            }
                        }
                    }
                }
            }
            .distinctUntilChanged()
    }
}