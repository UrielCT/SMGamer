package com.smgamer.data.repository

import com.smgamer.data.datastore.remote.FirestoreService
import com.smgamer.data.datastore.remote.MessageService
import com.smgamer.data.mappers.toDomain
import com.smgamer.data.mappers.toDto
import com.smgamer.domain.model.Message
import com.smgamer.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val messageService: MessageService
) : MessageRepository {


    override suspend fun sendMessage(message: Message) =
        messageService.sendMessage(message.toDto())

    override fun getMessagesByChatFlow(chatId: String): Flow<List<Message>> =
        messageService.getMessagesByChatFlow(chatId).map { it.map { dto -> dto.toDomain() } }

    override fun getMessagesByChatAndSenderFlow(chatId: String, senderId: String): Flow<List<Message>> =
        messageService.getMessagesByChatAndSenderFlow(chatId, senderId).map { it.map { dto -> dto.toDomain() } }

    override fun getLastMessagesByChatAndSenderFlow(chatId: String, senderId: String, limit: Int): Flow<List<Message>> =
        messageService.getLastMessagesByChatAndSenderFlow(chatId, senderId, limit).map { it.map { dto -> dto.toDomain() } }

    override fun getLastMessageByChatFlow(chatId: String): Flow<Message?> =
        messageService.getLastMessageByChatFlow(chatId).map { it?.toDomain() }


    override suspend fun updateViewed(chatId: String, messageId: String, viewed: Boolean) =
        messageService.updateViewed(chatId, messageId, viewed)

    override suspend fun markMessagesAsViewed(chatId: String, userId: String) {
        messageService.markMessagesAsViewed(chatId, userId)
    }

    //private val messagesRef = realtimeDatabase.getReference("messages")

//    override suspend fun sendMessage(message: Message) {
//        val ref = messagesRef.child(message.idChat).push()
//        val id = ref.key ?: return
//        ref.setValue(message.copy(id = id))
//    }


//    override fun getMessagesByChatFlow(chatId: String): Flow<List<Message>> = callbackFlow {
//        val listener = messagesRef.child(chatId)
//            .addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val messages = snapshot.children.mapNotNull { it.getValue(Message::class.java) }
//                    trySend(messages.sortedByDescending { it.timestamp })
//                }
//                override fun onCancelled(error: DatabaseError) { close(error.toException()) }
//            })
//        awaitClose { messagesRef.child(chatId).removeEventListener(listener) }
//    }



//    override fun getMessagesByChatAndSenderFlow(chatId: String, senderId: String): Flow<List<Message>> =
//        getMessagesByChatFlow(chatId).map { list -> list.filter { it.idSender == senderId } }




//    override fun getLastMessagesByChatAndSenderFlow(chatId: String, senderId: String, limit: Int): Flow<List<Message>> =
//        getMessagesByChatAndSenderFlow(chatId, senderId).map { it.take(limit) }

//    override fun getLastMessageByChatFlow(chatId: String): Flow<Message?> =
//        getMessagesByChatFlow(chatId).map { it.maxByOrNull { msg -> msg.timestamp } }


//    override suspend fun updateViewed(chatId: String, messageId: String, viewed: Boolean) {
//        messagesRef.child(chatId).child(messageId).child("viewed").setValue(viewed)
//    }

}