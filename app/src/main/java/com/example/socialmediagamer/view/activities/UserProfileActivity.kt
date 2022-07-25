package com.example.socialmediagamer.view.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialmediagamer.model.adapters.MyPostsAdapter
import com.example.socialmediagamer.databinding.ActivityUserProfileBinding
import com.example.socialmediagamer.model.models.Post
import com.example.socialmediagamer.model.providers.AuthProvider
import com.example.socialmediagamer.model.providers.PostProvider
import com.example.socialmediagamer.model.providers.UsersProvider
import com.example.socialmediagamer.utils.ViewedMessageHelper
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso

class UserProfileActivity : AppCompatActivity(), MyPostsAdapter.OnPostClickListener {

    private lateinit var binding: ActivityUserProfileBinding

    private lateinit var mUsersProvider: UsersProvider
    private lateinit var mAuthProvider: AuthProvider
    private lateinit var mPostProvider: PostProvider
    private lateinit var mAdapter: MyPostsAdapter


    private lateinit var mExtraIdUser: String


    private lateinit var mListener : ListenerRegistration





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mUsersProvider = UsersProvider()
        mAuthProvider = AuthProvider()
        mPostProvider = PostProvider()

        //setSupportActionBar(binding.toolbar)
        //supportActionBar!!.setTitle("")
        //supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        setupRecyclerView()

        mExtraIdUser = intent.getStringExtra("idUser").toString()

        if (mAuthProvider.getUid() == mExtraIdUser){
            binding.fabChat.isEnabled = false
        }


        binding.fabChat.setOnClickListener {
            goToChatActivity()
        }

        //binding.btnBack.setOnClickListener { finish() }

        getUser()
        getPostNumber()
        checkIfExistPost()
    }





    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            finish()
        }
        return true
    }



    override fun onStart() {
        super.onStart()
        val query: Query = mPostProvider.getPostByUser(mExtraIdUser)
        val opciones : FirestoreRecyclerOptions<Post> = FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post::class.java).build()
        mAdapter = MyPostsAdapter(this, opciones,this)
        binding.recyclerViewMyPost.adapter = mAdapter
        mAdapter.startListening()
        ViewedMessageHelper().updateOnline(true,this)
    }



    override fun onPause() {
        super.onPause()
        ViewedMessageHelper().updateOnline(false,this)
    }



    override fun onStop() {
        super.onStop()
        mAdapter.stopListening()

    }

    override fun onDestroy() {
        super.onDestroy()
        if (mListener != null){
            mListener.remove()
        }
    }

    override fun onItemClick(postId: String) {
        val intent= Intent(this, PostDetailActivity::class.java)
        intent.putExtra("id",postId)
        startActivity(intent)
    }




    private fun getPostNumber(){
        mPostProvider.getPostByUser(mExtraIdUser).get().addOnSuccessListener {
            val number:Int = it.size()
            binding.txtPostNumber.text = number.toString()
        }
    }


    private fun getUser(){
        mUsersProvider.getUser(mExtraIdUser).addOnSuccessListener {
            if (it.exists()){
                if (it.contains("email")){
                    val email = it.getString("email")
                    binding.txtEmail.text = email
                }
                if (it.contains("phone")){
                    val phone = it.getString("phone")
                    binding.txtPhone.text = phone
                }
                if (it.contains("username")){
                    val username = it.getString("username")
                    binding.txtUsername.text = username
                }
                if (it.contains("image_profile")){
                    val imageProfile = it.getString("image_profile")
                    if (!imageProfile.isNullOrEmpty()){
                        Picasso.get().load(imageProfile).fit().into(binding.imProfile)
                    }
                }
                if (it.contains("image_cover")){
                    val imageCover = it.getString("image_cover")
                    if (!imageCover.isNullOrEmpty()){
                        Picasso.get().load(imageCover).fit().into(binding.imCover)
                    }
                }
            }
        }
    }


    private fun setupRecyclerView(){
        binding.recyclerViewMyPost.layoutManager = LinearLayoutManager(this)
    }




    private fun checkIfExistPost(){
        mListener = mPostProvider.getPostByUser(mExtraIdUser).addSnapshotListener { value, error ->
            if (value != null){
                val numberpost:String= value?.size().toString()
                if (numberpost != "1"){
                    binding.txtPostExist.text = "Publicaciones"
                    binding.txtPostExist.setTextColor(Color.RED)
                }
                else{
                    binding.txtPostExist.text = "No hay publicaciones"
                    binding.txtPostExist.setTextColor(Color.GRAY)
                }
            }

        }
    }



    private fun goToChatActivity(){
        val intent:Intent = Intent(this,ChatActivity::class.java)
        intent.putExtra("idUser1", mAuthProvider.getUid())
        intent.putExtra("idUser2",mExtraIdUser)
        startActivity(intent)
    }


}