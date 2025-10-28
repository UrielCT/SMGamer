package com.smgamer.domain.model

data class Post(
    val id: String = "",
    val idUser: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val images: List<String> = emptyList(),
    val timestamp: Long = 0
)