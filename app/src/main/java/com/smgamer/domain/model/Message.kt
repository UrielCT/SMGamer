package com.smgamer.domain.model

data class Message(
    val id: String = "",
    val idSender: String = "",
    val idReceiver: String = "",
    val idChat: String = "",
    val message: String = "",
    val viewed: Boolean = false,
    val timestamp: Long = 0
)
