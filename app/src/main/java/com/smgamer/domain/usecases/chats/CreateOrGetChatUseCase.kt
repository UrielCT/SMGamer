package com.smgamer.domain.usecases.chats

import com.smgamer.domain.model.Chat
import com.smgamer.domain.repository.ChatRepository
import javax.inject.Inject

class CreateOrGetChatUseCase @Inject constructor(private val chatRepository: ChatRepository) {
    suspend operator fun invoke(meId: String, otherId: String): Chat =
        chatRepository.createChatIfNotExists(meId, otherId)
}