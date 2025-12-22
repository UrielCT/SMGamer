package com.smgamer.ui.screens.chats

import com.smgamer.domain.model.ChatData
import com.smgamer.domain.model.User

data class ChatsUiState(
    val currentUser: User? = null,
    val chats: List<ChatData> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
