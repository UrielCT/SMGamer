package com.smgamer.domain.usecases.chats

import com.smgamer.domain.repository.ChatRepository
import javax.inject.Inject

class GetChatFlowUseCase @Inject constructor(private val chatRepository: ChatRepository) {
    operator fun invoke(chatId: String) = chatRepository.getChatByIdFlow(chatId)
}