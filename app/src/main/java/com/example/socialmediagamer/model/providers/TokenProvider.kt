package com.example.socialmediagamer.model.providers

import com.example.socialmediagamer.model.models.Token
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class TokenProvider {

    // crea una nueva coleccion en firebase firestore
    private val mCollection = FirebaseFirestore.getInstance().collection("Tokens")

    fun create(idUser:String){
        if (idUser == null){
            return
        }
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            val token: Token = Token(it)
            mCollection.document(idUser).set(token)
        }
    }

    fun getToken(idUser: String):Task<DocumentSnapshot>{
        return mCollection.document(idUser).get()
    }

}