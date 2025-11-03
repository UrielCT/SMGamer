package com.smgamer.domain.usecases.chats

import com.smgamer.domain.model.ChatData
import com.smgamer.domain.repository.ChatRepository
import com.smgamer.domain.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

//class GetChatsWithUsersUseCase @Inject constructor(
//    private val chatRepository: ChatRepository,
//    private val userRepository: UserRepository
//) {
//    @OptIn(ExperimentalCoroutinesApi::class)
//    operator fun invoke(currentUserId: String): Flow<List<ChatData>> =
//        chatRepository.getChatsByUserFlow(currentUserId)
//            .flatMapLatest { chats ->
//                if (chats.isEmpty()) return@flatMapLatest flowOf(emptyList())
//
//                combine(
//                    chats.map { chat ->
//                        val otherId = chat.ids.firstOrNull { it != currentUserId }
//                        if (otherId != null) {
//                            userRepository.getUserByIdFlow(otherId)
//                                .map { user -> ChatData(chat, user) }
//                        } else {
//                            flowOf(ChatData(chat, null))
//                        }
//                    }
//                ) { it.toList() }
//            }
//}