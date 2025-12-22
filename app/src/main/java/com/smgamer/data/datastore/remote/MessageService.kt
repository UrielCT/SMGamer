package com.smgamer.data.datastore.remote

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.smgamer.data.model.MessageDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageService @Inject constructor(
    private val realtimeDatabase: FirebaseDatabase
) {

    private val messagesCollection = realtimeDatabase.getReference("Messages")


    suspend fun sendMessage(messageDto: MessageDto) {
        val ref = messagesCollection.child(messageDto.idChat).push()
        val id = ref.key ?: return
        ref.setValue(messageDto.copy(id = id)).await()
    }

//    fun getMessagesByChatFlow(chatId: String): Flow<List<MessageDto>> = callbackFlow {
//        val listener = messagesCollection.child(chatId)
//            .addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val list = snapshot.children.mapNotNull { it.getValue(MessageDto::class.java) }
//                    trySend(list.sortedByDescending { it.timestamp }).isSuccess
//                }
//                override fun onCancelled(error: DatabaseError) {
//                    close(error.toException())
//                }
//            })
//        awaitClose { messagesCollection.child(chatId).removeEventListener(listener) }
//    }

    //    fun getMessagesByChatFlow(chatId: String): Flow<List<MessageDto>> = callbackFlow {
//        val listener = firestore.collection("messages")
//            .whereEqualTo("idChat", chatId)
//            .orderBy("timestamp", Query.Direction.DESCENDING)
//            .addSnapshotListener { snapshot, error ->
//                if (error != null) {
//                    close(error)
//                    return@addSnapshotListener
//                }
//                val messages = snapshot?.documents?.mapNotNull { it.toObject(MessageDto::class.java) } ?: emptyList()
//                trySend(messages)
//            }
//        awaitClose { listener.remove() }
//    }
    fun getMessagesByChatFlow(chatId: String): Flow<List<MessageDto>> = callbackFlow {
        val ref = messagesCollection.child(chatId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children
                    .mapNotNull { it.getValue(MessageDto::class.java)?.copy(id = it.key ?: "") }
                    .sortedByDescending { it.timestamp }
                trySend(list).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun markMessagesAsViewed(chatId: String, userId: String) {
        val messagesRef = messagesCollection.child(chatId)
        val snapshot = messagesRef.get().await()
        snapshot.children.forEach { messageSnapshot ->
            val msg = messageSnapshot.getValue(MessageDto::class.java)
            if (msg != null && msg.idReceiver == userId && !msg.viewed) {
                messageSnapshot.ref.child("viewed").setValue(true)
            }
        }
    }


    suspend fun updateViewed(chatId: String, messageId: String, viewed: Boolean) {
        messagesCollection.child(chatId).child(messageId).child("viewed").setValue(viewed).await()
    }

    fun getLastMessageByChatFlow(chatId: String): Flow<MessageDto?> =
        getMessagesByChatFlow(chatId).map { list -> list.maxByOrNull { it.timestamp } }



    fun getMessagesByChatAndSenderFlow(chatId: String, senderId: String): Flow<List<MessageDto>> =
        getMessagesByChatFlow(chatId).map { list -> list.filter { it.idSender == senderId } }

    fun getLastMessagesByChatAndSenderFlow(chatId: String, senderId: String, limit: Int = 3): Flow<List<MessageDto>> =
        getMessagesByChatAndSenderFlow(chatId, senderId).map { it.take(limit) }
}