package com.example.socialmediagamer.model.providers

import com.example.socialmediagamer.model.models.Comment
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class CommentsProvider {

    private var mCollection : CollectionReference = FirebaseFirestore.getInstance().collection("Comments")

    fun create(comment: Comment) : Task<Void>{
        return  mCollection.document().set(comment)
    }


    fun getCommentsByPost(idPost:String): Query {
        return mCollection.whereEqualTo("idPost",idPost)
    }
}