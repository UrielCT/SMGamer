package com.smgamer.domain.model

data class PostData(
    val post: Post,
    val user: User?,
    val likes: List<Like>,
    val comments: List<Comment>
){
    val likesCount: Int get() = likes.size
    fun isLikedBy(userId: String) = likes.any { it.idUser == userId }
}
