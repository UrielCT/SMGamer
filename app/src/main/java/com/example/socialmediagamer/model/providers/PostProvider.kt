package com.example.socialmediagamer.model.providers

import com.example.socialmediagamer.model.models.Post
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class PostProvider {

    private val mCollection :CollectionReference = FirebaseFirestore.getInstance().collection("Posts")


    fun save(post: Post):Task<Void>{
        return mCollection.document().set(post)
    }


    fun gatAll():Query{
        return mCollection.orderBy("timestamp", Query.Direction.DESCENDING)
    }

    fun getPostByCategoryAndTimestamp(category:String):Query{
        return mCollection.whereEqualTo("category",category).orderBy("timestamp", Query.Direction.DESCENDING)
    }

    fun getPostByTitle(title:String):Query{
        return mCollection.orderBy("title").startAt(title).endAt(title + '\uf8ff')
    }


    fun getPostByUser(id:String):Query{
        return mCollection.whereEqualTo("idUser",id)
    }

    fun getPostById(id:String):Task<DocumentSnapshot>{
        return mCollection.document(id).get()
    }

    fun delete(id:String):Task<Void>{
        return mCollection.document(id).delete()
    }

}