package com.example.socialmediagamer.model.providers

import com.example.socialmediagamer.model.models.Message
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MessageProvider {

    private val mCollection: CollectionReference = FirebaseFirestore.getInstance().collection("Messages")

    fun create(message:Message):Task<Void>{
        val document:DocumentReference = mCollection.document()
        message.id = document.id
        return document.set(message)
    }

    fun getMessageByChat(idChat:String):Query{
        return mCollection.whereEqualTo("idChat",idChat).orderBy("timestamp", Query.Direction.ASCENDING)
    }

    fun getMessageByChatAndSender(idChat:String,idSender:String):Query{
        return mCollection.whereEqualTo("idChat",idChat).whereEqualTo("idSender", idSender).whereEqualTo("viewed",false)
    }

    fun getLastThreeMessageByChatAndSender(idChat:String,idSender:String):Query{
        return mCollection
            .whereEqualTo("idChat",idChat)
            .whereEqualTo("idSender", idSender)
            .whereEqualTo("viewed",false)
            .orderBy("timestamp",Query.Direction.DESCENDING)
            .limit(3)


    }

    fun getLastMessage(idChat:String):Query{
        return mCollection.whereEqualTo("idChat",idChat).orderBy("timestamp",Query.Direction.DESCENDING).limit(1)
    }

    fun getLastMessageSender(idChat:String, idSender: String):Query{
        return mCollection.whereEqualTo("idChat",idChat).whereEqualTo("idSender",idSender).orderBy("timestamp",Query.Direction.DESCENDING).limit(1)
    }

    fun updateViewed(idDocument:String,state:Boolean):Task<Void>{
        val map: Map<String, Any> = hashMapOf(
            "viewed" to state
        )
        return mCollection.document(idDocument).update(map)
    }
}