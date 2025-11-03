package com.smgamer.data.model

data class ChatDto(
    val id: String = "",
    val idUserA: String = "",
    val idUserB: String = "",
    val idNotification: Int = 0,
    val isWriting: Boolean = false,
    val ids: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)
