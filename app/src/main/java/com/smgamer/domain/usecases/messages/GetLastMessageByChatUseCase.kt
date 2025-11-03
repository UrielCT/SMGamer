package com.smgamer.domain.usecases.messages

import com.smgamer.domain.repository.MessageRepository
import javax.inject.Inject

class GetLastMessageByChatUseCase @Inject constructor(private val repository: MessageRepository) {
    operator fun invoke(chatId: String) = repository.getLastMessageByChatFlow(chatId)
}