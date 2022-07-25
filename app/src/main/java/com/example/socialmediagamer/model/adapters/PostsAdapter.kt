package com.example.socialmediagamer.model.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.socialmediagamer.R
import com.example.socialmediagamer.base.BaseViewHolder
import com.example.socialmediagamer.model.models.Like
import com.example.socialmediagamer.model.models.Post
import com.example.socialmediagamer.model.providers.AuthProvider
import com.example.socialmediagamer.model.providers.LikesProvider
import com.example.socialmediagamer.model.providers.UsersProvider
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.squareup.picasso.Picasso
import java.util.*

class PostsAdapter(private val context: Context?, options: FirestoreRecyclerOptions<Post>? = null, private val itemClickListener: OnPostClickListener? = null,var mTVnumberFilter:TextView? = null) : FirestoreRecyclerAdapter<Post,BaseViewHolder<*>>(options!!) {


    private val mUsersProvider:UsersProvider = UsersProvider()
    private val mLikesProvider:LikesProvider = LikesProvider()
    private val mAuthProvider:AuthProvider = AuthProvider()

    private lateinit var mListener :ListenerRegistration


    interface OnPostClickListener{
        fun onItemClick(postId: String)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int, post: Post) {
        when(holder){
            is PostsViewholder -> holder.bind(post,position)
            else -> IllegalArgumentException("se olvido de pasar el viewholder en el bind")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {

        return PostsViewholder(LayoutInflater.from(context).inflate(R.layout.item_post, parent,false))
    }


    inner class PostsViewholder(itemView:View) : BaseViewHolder<Post>(itemView){
        val postCard: ImageView = itemView.findViewById(R.id.im_postCard)



        override fun bind(item: Post, position: Int) {

            val document:DocumentSnapshot = snapshots.getSnapshot(position)
            val postId:String = document.id
            val userId :String =document.getString("idUser").toString()

            if (mTVnumberFilter != null){
                val numberFilter:Int = snapshots.size
                mTVnumberFilter!!.text = numberFilter.toString()
            }

            itemView.setOnClickListener { itemClickListener!!.onItemClick(postId) }


            itemView.findViewById<TextView>(R.id.txt_title_postcard).text = item.title.uppercase()
            itemView.findViewById<TextView>(R.id.txt_description_postcard).text = item.descripcion

            if (item.image1 !=null){
                if (!item.image1.isEmpty()){
                    Picasso.get().load(item.image1).fit().into(postCard)
                }
            }

            itemView.findViewById<ImageView>(R.id.im_like).setOnClickListener {
                val like = Like()
                like.idUser = mAuthProvider.getUid().toString()
                like.idPost = postId
                like.timestamp = Date().time
                like(like,itemView)
            }


            getUserInfo(userId,itemView)

            getNumberLikesByPost(postId,itemView)

            checkIfLikeExist(postId, mAuthProvider.getUid().toString(),itemView)



        }

    }





    private fun getNumberLikesByPost(idPost:String,view: View){
         mListener = mLikesProvider.getLikesByPost(idPost).addSnapshotListener { value, error ->
             if (value != null){
                 var numberLikes = (value?.size() ?: error)
                 view.findViewById<TextView>(R.id.txt_likes).text = "$numberLikes Me gustas"
             }
        }
    }


    private fun like(like:Like,view: View){
        mLikesProvider.getLikeByPostandUser(like.idPost,mAuthProvider.getUid().toString()).get().addOnSuccessListener {
            val numberDocuments:Int = it.size()
            if (numberDocuments > 0){
                val idLike:String = it.documents[0].id
                view.findViewById<ImageView>(R.id.im_like).setImageResource(R.drawable.icon_like_grey)
                mLikesProvider.delete(idLike)
            }
            else{
                view.findViewById<ImageView>(R.id.im_like).setImageResource(R.drawable.icon_like_blue)
                mLikesProvider.create(like)
            }
        }
    }




    private fun checkIfLikeExist(idPost: String,idUser: String,view: View){
        mLikesProvider.getLikeByPostandUser(idPost,idUser).get().addOnSuccessListener {
            val numberDocuments:Int = it.size()
            if (numberDocuments > 0){
                view.findViewById<ImageView>(R.id.im_like).setImageResource(R.drawable.icon_like_blue)
            }
            else{
                view.findViewById<ImageView>(R.id.im_like).setImageResource(R.drawable.icon_like_grey)
            }
        }
    }




    private fun getUserInfo(idUser:String,view: View){
        mUsersProvider.getUser(idUser).addOnSuccessListener {
            if (it.exists()){
                if(it.contains("username")){
                    val username:String = it.getString("username").toString()
                    view.findViewById<TextView>(R.id.txt_username_postcard).text = "By: ${username.toUpperCase()}"
                }
            }
        }

    }


    fun getListener():ListenerRegistration{
        return mListener
    }



}