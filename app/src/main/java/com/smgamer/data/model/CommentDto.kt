package com.smgamer.data.model

data class CommentDto(
    val id: String = "",
    val comment: String = "",
    val idPost: String = "",
    val idUser: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
