package com.smgamer.domain.usecases.messages

import com.smgamer.domain.repository.MessageRepository
import javax.inject.Inject

class UpdateMessageViewedUseCase @Inject constructor(private val repo: MessageRepository) {
    suspend operator fun invoke(chatId: String, messageId: String) = repo.updateViewed(chatId, messageId, true)
}