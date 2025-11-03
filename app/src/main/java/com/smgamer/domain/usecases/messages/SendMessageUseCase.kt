package com.smgamer.domain.usecases.messages

import com.smgamer.domain.model.Message
import com.smgamer.domain.repository.MessageRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(private val repository: MessageRepository) {
    suspend operator fun invoke(message: Message) = repository.sendMessage(message)
}