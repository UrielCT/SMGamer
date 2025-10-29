package com.smgamer.data.model

data class LikeDto(
    val id: String = "",
    val idPost: String = "",
    val idUser: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
