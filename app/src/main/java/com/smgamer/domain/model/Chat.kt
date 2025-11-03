package com.smgamer.domain.model

data class Chat (
    val id: String = "",
    val idUserA: String = "",
    val idUserB: String = "",
    val idNotification: Int = 0,
    val isWriting: Boolean = false,
    val ids: List<String> = emptyList(),
    val timestamp: Long = 0
)