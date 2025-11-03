package com.smgamer.data.model

data class MessageDto(
    val id: String = "",
    val idSender: String = "",
    val idReceiver: String = "",
    val idChat: String = "",
    val message: String = "",
    val viewed: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
