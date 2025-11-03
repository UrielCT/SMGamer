package com.smgamer.data.datastore.remote

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.smgamer.data.model.ChatDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatService @Inject constructor(
    private val firestore: FirebaseFirestore,
) {

    private val chatsCollection = firestore.collection("Chats")


    suspend fun createChat(chatDto: ChatDto) {
        chatsCollection.document(chatDto.id).set(chatDto).await()
    }

    suspend fun getChatByUsers(idUserA: String, idUserB: String): ChatDto? {
        val query = chatsCollection
            .whereArrayContains("ids", idUserA)
            .get().await()
        val found = query.documents.mapNotNull { it.toObject(ChatDto::class.java)?.copy(id = it.id) }
            .find { dto -> dto.ids.containsAll(listOf(idUserA, idUserB)) }
        return found
    }

    fun getChatByIdFlow(chatId: String): Flow<ChatDto?> = callbackFlow {
        val listener = chatsCollection.document(chatId).addSnapshotListener { snapshot, error ->
            if (error != null) { close(error); return@addSnapshotListener }
            val dto = snapshot?.toObject(ChatDto::class.java)?.copy(id = snapshot.id)
            trySend(dto).isSuccess
        }
        awaitClose { listener.remove() }
    }

//    fun getChatsByUserFlow(userId: String): Flow<List<ChatDto>> = callbackFlow {
//        val listener = chatsCollection
//            .whereArrayContains("ids", userId)
//            .orderBy("timestamp", Query.Direction.DESCENDING)
//            .addSnapshotListener { snapshot, error ->
//                if (error != null) { close(error); return@addSnapshotListener }
//                val list = snapshot?.toObjects(ChatDto::class.java)?.map { it.copy(id = it.id) } ?: emptyList()
//                trySend(list).isSuccess
//            }
//        awaitClose { listener.remove() }
//    }

    fun getChatsByUserFlow(userId: String): Flow<List<ChatDto>> = callbackFlow {
        val listener = chatsCollection
            .whereArrayContains("ids", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val chats = snapshot?.documents?.mapNotNull {
                    it.toObject(ChatDto::class.java)?.copy(id = it.id)
                }?.sortedByDescending { it.timestamp } ?: emptyList()

                trySend(chats).isSuccess
            }

        awaitClose { listener.remove() }
    }
}