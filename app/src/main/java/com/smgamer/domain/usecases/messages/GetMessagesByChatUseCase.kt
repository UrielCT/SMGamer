package com.smgamer.domain.usecases.messages

import com.smgamer.domain.repository.MessageRepository
import javax.inject.Inject

class GetMessagesByChatUseCase @Inject constructor(private val repository: MessageRepository) {
    operator fun invoke(chatId: String) = repository.getMessagesByChatFlow(chatId)
}