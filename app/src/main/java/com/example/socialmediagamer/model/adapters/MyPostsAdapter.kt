package com.example.socialmediagamer.model.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.socialmediagamer.R
import com.example.socialmediagamer.base.BaseViewHolder
import com.example.socialmediagamer.model.models.Post
import com.example.socialmediagamer.model.providers.AuthProvider
import com.example.socialmediagamer.model.providers.LikesProvider
import com.example.socialmediagamer.model.providers.PostProvider
import com.example.socialmediagamer.model.providers.UsersProvider
import com.example.socialmediagamer.utils.RelativeTime
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.squareup.picasso.Picasso

class MyPostsAdapter(private val context: Context?, options: FirestoreRecyclerOptions<Post>, private val itemClickListener: OnPostClickListener) : FirestoreRecyclerAdapter<Post,BaseViewHolder<*>>(options) {

    private val mUsersProvider:UsersProvider = UsersProvider()
    private val mLikesProvider:LikesProvider = LikesProvider()
    private val mAuthProvider:AuthProvider = AuthProvider()
    private val mPostsProvider:PostProvider = PostProvider()

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
        return PostsViewholder(LayoutInflater.from(context).inflate(R.layout.cardview_my_post, parent,false))
    }


    inner class PostsViewholder(itemView:View) : BaseViewHolder<Post>(itemView){
        private val myPostCardImage: ImageView = itemView.findViewById(R.id.im_circle_my_post)

        override fun bind(item: Post, position: Int) {

            val document:DocumentSnapshot = snapshots.getSnapshot(position)
            val postId:String = document.id
            val userId :String =document.getString("idUser").toString()
            val relativeTime:String = RelativeTime.getTimeAgo(item.timestamp,context).toString()

            itemView.findViewById<TextView>(R.id.txt_title_my_post).text = item.title.uppercase()
            itemView.findViewById<TextView>(R.id.txt_title_relative_time_my_post).text = relativeTime

            if(item.idUser.equals(mAuthProvider.getUid())){
                itemView.findViewById<ImageView>(R.id.im_delete_my_post).visibility = View.VISIBLE
            }
            else{
                itemView.findViewById<ImageView>(R.id.im_delete_my_post).visibility = View.GONE
            }

            itemView.setOnClickListener { itemClickListener.onItemClick(postId) }


            itemView.findViewById<ImageView>(R.id.im_delete_my_post).setOnClickListener {
                showConfirmDelete(postId)
            }

            if (item.image1 !=null){
                if (!item.image1.isEmpty()){
                    Picasso.get().load(item.image1).fit().into(myPostCardImage)
                }
            }
        }
    }

    private fun showConfirmDelete(postId: String){
        val dialog :AlertDialog = AlertDialog.Builder(context)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Eliminar publicadion")
            .setMessage("Estas seguro de realizar esta acción")
            .setPositiveButton("SI",DialogInterface.OnClickListener { dialogInterface, i ->
                deletePost(postId)
            })
            .setNegativeButton("NO",null)
            .show()
    }

    
    private fun deletePost(postId:String){
        mPostsProvider.delete(postId).addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(context,"El post se elimono correctamente", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(context,"No se pudo eliminar el post", Toast.LENGTH_SHORT).show()

            }
        }
    }
}