package com.smgamer.domain.repository

import com.smgamer.domain.model.Chat
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun createChatIfNotExists(idUserA: String, idUserB: String): Chat
    fun getChatByIdFlow(chatId: String): Flow<Chat?>
    fun getChatsByUserFlow(userId: String): Flow<List<Chat>>
    suspend fun getChatByUsers(idUserA: String, idUserB: String): Chat?
}