package com.example.socialmediagamer.model.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.socialmediagamer.R
import com.example.socialmediagamer.base.BaseViewHolder
import com.example.socialmediagamer.model.models.Comment
import com.example.socialmediagamer.model.providers.UsersProvider
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.squareup.picasso.Picasso

class CommentAdapter(val context: Context?, options: FirestoreRecyclerOptions<Comment>) : FirestoreRecyclerAdapter<Comment, BaseViewHolder<*>>(options) {

    private lateinit var mUsersProvider: UsersProvider



    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int, comment: Comment) {
        when(holder){
            is CommentAdapter.CommentViewholder -> holder.bind(comment, position)
            else -> IllegalArgumentException("se olvido de pasar el viewholder en el bind")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return CommentViewholder(LayoutInflater.from(context).inflate(R.layout.item_comment, parent,false))
    }




    inner class CommentViewholder(itemView:View) : BaseViewHolder<Comment>(itemView){



        override fun bind(item: Comment, position: Int) {
            mUsersProvider = UsersProvider()

            val document: DocumentSnapshot = snapshots.getSnapshot(position)
            val commentId:String = document.id
            val userId :String =document.getString("idUser").toString()



            itemView.findViewById<TextView>(R.id.txt_comentario).text = item.comment

            getUserInfo(userId,itemView)

        }
    }

    private fun getUserInfo(idUser:String,itemView: View){
        val commentUserImage : ImageView = itemView.findViewById(R.id.im_comment)
        val commentUsername : TextView = itemView.findViewById(R.id.txt_username)

        mUsersProvider.getUser(idUser).addOnSuccessListener {
            if (it.exists()){
                if (it.contains("username")){
                    val username = it.getString("username")
                    commentUsername.text = username!!.uppercase()
                }
                if (it.contains("image_profile")){
                    val imageProfile =it.getString("image_profile")
                    if (imageProfile != null){
                        if (imageProfile.isNotEmpty()){
                            Picasso.get().load(imageProfile).fit().into(commentUserImage)
                        }
                    }
                }
            }
        }
    }


}