package com.smgamer.domain.usecases.chats

import com.smgamer.domain.repository.ChatRepository
import javax.inject.Inject

class CreateChatUseCase @Inject constructor(private val repository: ChatRepository) {
    suspend operator fun invoke(idUserA: String, idUserB: String) =
        repository.createChatIfNotExists(idUserA, idUserB)
}