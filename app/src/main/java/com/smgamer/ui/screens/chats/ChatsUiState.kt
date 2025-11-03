package com.smgamer.ui.screens.chats

import com.smgamer.domain.model.Chat
import com.smgamer.domain.model.ChatData

data class ChatsUiState(
    // chats: List<Chat> = emptyList(),
    val chats: List<ChatData> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
