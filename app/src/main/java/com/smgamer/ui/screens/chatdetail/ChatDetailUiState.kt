package com.smgamer.ui.screens.chatdetail

import com.smgamer.domain.model.Chat
import com.smgamer.domain.model.Message
import com.smgamer.domain.model.User

data class ChatDetailUiState(
    val chat: Chat? = null,
    val otherUser: User? = null,
    val messages: List<Message> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
