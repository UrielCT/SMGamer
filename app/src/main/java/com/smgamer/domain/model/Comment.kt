package com.smgamer.domain.model

data class Comment(
    val id: String = "",
    val comment: String = "",
    val idPost: String = "",
    val idUser: String = "",
    val timestamp: Long = 0
)
