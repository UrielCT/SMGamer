package com.smgamer.domain.model

data class ChatData(
    val chat: Chat,
    val otherUser: User?,
    val lastMessage: Message?,
    val unreadCount: Int
)
