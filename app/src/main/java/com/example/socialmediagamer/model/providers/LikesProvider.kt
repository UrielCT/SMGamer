package com.example.socialmediagamer.model.providers

import com.example.socialmediagamer.model.models.Like
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class LikesProvider {

    private val mCollection = FirebaseFirestore.getInstance().collection("Likes")

    fun create(like: Like):Task<Void>{
        val document = mCollection.document()
        val id:String = document.id
        like.id = id
        return document.set(like)
    }

    fun getLikesByPost(idPost: String):Query{
        return mCollection.whereEqualTo("idPost", idPost)
    }

    fun getLikeByPostandUser(idPost:String,idUser:String):Query{
        return mCollection.whereEqualTo("idPost",idPost).whereEqualTo("idUser",idUser)
    }

    // elimina el documento del like repetido
    fun delete(id:String):Task<Void>{
        return  mCollection.document(id).delete()
    }



}