package com.smgamer.domain.repository

import com.smgamer.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    suspend fun sendMessage(message: Message)
    fun getMessagesByChatFlow(chatId: String): Flow<List<Message>>
    fun getMessagesByChatAndSenderFlow(chatId: String, senderId: String): Flow<List<Message>>
    fun getLastMessagesByChatAndSenderFlow(chatId: String, senderId: String, limit: Int = 3): Flow<List<Message>>
    fun getLastMessageByChatFlow(chatId: String): Flow<Message?>
    suspend fun updateViewed(chatId: String, messageId: String, viewed: Boolean)
    suspend fun markMessagesAsViewed(chatId: String, userId: String)

}