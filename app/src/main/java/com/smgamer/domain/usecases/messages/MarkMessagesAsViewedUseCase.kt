package com.smgamer.domain.usecases.messages

import com.smgamer.domain.repository.MessageRepository
import javax.inject.Inject

class MarkMessagesAsViewedUseCase @Inject constructor(
    private val repository: MessageRepository
) {
    suspend operator fun invoke(chatId: String, userId: String) {
        repository.markMessagesAsViewed(chatId, userId)
    }
}