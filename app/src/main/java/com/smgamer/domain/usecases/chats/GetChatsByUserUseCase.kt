package com.smgamer.domain.usecases.chats

import com.smgamer.domain.repository.ChatRepository
import javax.inject.Inject

class GetChatsByUserUseCase @Inject constructor(private val repository: ChatRepository) {
    operator fun invoke(userId: String) = repository.getChatsByUserFlow(userId)
}
