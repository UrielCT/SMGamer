package com.smgamer.domain.usecases

import com.smgamer.domain.model.ChatData
import com.smgamer.domain.repository.ChatRepository
import com.smgamer.domain.repository.MessageRepository
import com.smgamer.domain.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class GetChatsWithDetailsUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository,
    private val messageRepository: MessageRepository
) {
    operator fun invoke(currentUserId: String): Flow<List<ChatData>> =
        chatRepository.getChatsByUserFlow(currentUserId)
            .flatMapLatest { chats ->
                if (chats.isEmpty()) return@flatMapLatest flowOf(emptyList())

                combine(
                    chats.map { chat ->
                        val otherId = chat.ids.firstOrNull { it != currentUserId }

                        if (otherId != null) {
                            combine(
                                userRepository.getUserByIdFlow(otherId),
                                messageRepository.getLastMessageByChatFlow(chat.id),
                                messageRepository.getMessagesByChatFlow(chat.id)
                                    .map { messages ->
                                        // Contar mensajes no leídos (no del usuario actual)
                                        messages.count { it.idSender != currentUserId && !it.viewed }
                                    }
                            ) { user, lastMessage, unreadCount ->
                                ChatData(
                                    chat = chat,
                                    otherUser = user,
                                    lastMessage = lastMessage,
                                    unreadCount = unreadCount
                                )
                            }
                        } else {
                            flowOf(ChatData(chat, null, null, 0))
                        }
                    }
                ) { it.toList().sortedByDescending { chatData ->
                    chatData.lastMessage?.timestamp ?: chatData.chat.timestamp
                } }
            }
}