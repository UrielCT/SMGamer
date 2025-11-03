package com.smgamer.data.repository

import com.smgamer.data.datastore.remote.ChatService
import com.smgamer.data.mappers.toDomain
import com.smgamer.data.model.ChatDto
import com.smgamer.domain.model.Chat
import com.smgamer.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatService: ChatService
) : ChatRepository {

    //private val chatsCollection = firestore.collection("Chats")

//    override suspend fun createChatIfNotExists(idUserA: String, idUserB: String): Chat {
//        val existingDto = firestoreService.getChatByUsers(idUserA, idUserB)
//        if (existingDto != null) return existingDto.toDomain()
//
//        val id = firestoreService.createNewChatDocumentId() // or generate id manually
//        val dto = ChatDto(
//            id = id,
//            idUserA = idUserA,
//            idUserB = idUserB,
//            ids = listOf(idUserA, idUserB),
//            timestamp = System.currentTimeMillis()
//        )
//        firestoreService.createChat(dto)
//        return dto.toDomain()
//    }

    private fun deterministicChatId(a: String, b: String): String =
        listOf(a, b).sorted().joinToString("_")

    override suspend fun createChatIfNotExists(idUserA: String, idUserB: String): Chat {
        val existingDto = chatService.getChatByUsers(idUserA, idUserB)
        if (existingDto != null) return existingDto.toDomain()

        // 🔹 Generar ID manualmente (siempre igual sin importar el orden)
        //val id = listOf(idUserA, idUserB).sorted().joinToString("_")
        val id = deterministicChatId(idUserA, idUserB)

        val dto = ChatDto(
            id = id,
            idUserA = idUserA,
            idUserB = idUserB,
            ids = listOf(idUserA, idUserB),
            timestamp = System.currentTimeMillis()
        )

        chatService.createChat(dto)
        return dto.toDomain()
    }

    override fun getChatByIdFlow(chatId: String): Flow<Chat?> =
        chatService.getChatByIdFlow(chatId).map { it?.toDomain() }

    override fun getChatsByUserFlow(userId: String): Flow<List<Chat>> =
        chatService.getChatsByUserFlow(userId).map { list -> list.map { it.toDomain() } }


    override suspend fun getChatByUsers(idUserA: String, idUserB: String): Chat? =
        chatService.getChatByUsers(idUserA, idUserB)?.toDomain()


//    override suspend fun createChatIfNotExists(idUserA: String, idUserB: String): Chat {
//        val query = chatsCollection
//            .whereArrayContains("ids", idUserA)
//            .get().await()
//
//        val existing = query.documents
//            .mapNotNull { it.toObject(Chat::class.java)?.copy(id = it.id) }
//            .find { it.ids.containsAll(listOf(idUserA, idUserB)) }
//
//        if (existing != null) return existing
//
//        val id = chatsCollection.document().id
//        val newChat = Chat(
//            id = id,
//            idUserA = idUserA,
//            idUserB = idUserB,
//            ids = listOf(idUserA, idUserB)
//        )
//
//        chatsCollection.document(id).set(newChat).await()
//        return newChat
//    }



//    override fun getChatsByUserFlow(userId: String): Flow<List<Chat>> = callbackFlow {
//        val listener = chatsCollection
//            .whereArrayContains("ids", userId)
//            .orderBy("timestamp", Query.Direction.DESCENDING)
//            .addSnapshotListener { snapshot, error ->
//                if (error != null) { close(error); return@addSnapshotListener }
//                val chats = snapshot?.toObjects(Chat::class.java)?.map { it.copy(id = snapshot.id) } ?: emptyList()
//                trySend(chats)
//            }
//
//        awaitClose { listener.remove() }
//    }


//    override suspend fun getChatByUsers(idUserA: String, idUserB: String): Chat? {
//        val query = chatsCollection
//            .whereArrayContains("ids", idUserA)
//            .get().await()
//
//        return query.documents
//            .mapNotNull { it.toObject(Chat::class.java)?.copy(id = it.id) }
//            .find { it.ids.containsAll(listOf(idUserA, idUserB)) }
//    }
}