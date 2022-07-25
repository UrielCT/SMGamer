package com.example.socialmediagamer.model.providers

import com.example.socialmediagamer.model.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

// LOGICA PARA ALMACENAR, OBTENER, ACTUALIZAR O ELIMINAR DATOS DE FIRESTORE

class UsersProvider {

    private val mCollection: CollectionReference = FirebaseFirestore.getInstance().collection("Users")


    fun getUser(id:String): Task<DocumentSnapshot> {
        return mCollection.document(id).get()
    }

    fun getUserRealTime(id:String): DocumentReference {
        return mCollection.document(id)
    }

    fun create(user: User):Task<Void>{
        return mCollection.document(user.id).set(user)
    }


    fun update(user: User):Task<Void>{
        var map: Map<String,Any> = hashMapOf(
            "username" to user.username,
            "phone" to user.phone,
            "timestamp" to Date().time,
            "password" to user.password,
            "image_profile" to user.image_profile,
            "image_cover" to user.image_cover
        )
        return mCollection.document(user.id).update(map)
    }

    fun updateOnline(idUser: String,status:Boolean):Task<Void>{
            var map: Map<String,Any> = hashMapOf(
                "online" to status,
                "lastConnect" to Date().time,

            )
            return mCollection.document(idUser).update(map)
        }


}