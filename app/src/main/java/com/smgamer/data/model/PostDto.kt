package com.smgamer.data.model

data class PostDto(
    val id: String = "",
    val idUser: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val images: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

