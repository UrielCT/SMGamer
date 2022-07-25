package com.example.socialmediagamer.view.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialmediagamer.view.activities.EditProfileActivity
import com.example.socialmediagamer.view.activities.PostDetailActivity
import com.example.socialmediagamer.model.adapters.MyPostsAdapter
import com.example.socialmediagamer.databinding.FragmentProfileBinding
import com.example.socialmediagamer.model.models.Post
import com.example.socialmediagamer.model.providers.AuthProvider
import com.example.socialmediagamer.model.providers.PostProvider
import com.example.socialmediagamer.model.providers.UsersProvider
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment(), MyPostsAdapter.OnPostClickListener {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var mUsersProvider: UsersProvider
    private lateinit var mAuthProvider: AuthProvider
    private lateinit var mPostProvider: PostProvider
    private lateinit var mAdapter: MyPostsAdapter

    private lateinit var mListener :ListenerRegistration



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container,false)// Inflate the layout for this fragment

        mUsersProvider = UsersProvider()
        mAuthProvider = AuthProvider()
        mPostProvider = PostProvider()

        setupRecyclerView()


        binding.llEditProfile.setOnClickListener {
            goToEditProfile()
        }

        getUser()
        getPostNumber()
        checkIfExistPost()
        return binding.root
    }


    private fun checkIfExistPost(){
        mListener = mPostProvider.getPostByUser(mAuthProvider.getUid().toString()).addSnapshotListener { value, error ->
            if (value != null){
                val numberpost:String = value?.size().toString()
                if (numberpost != "0"){
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

    override fun onStart() {
        super.onStart()
        val query: Query = mPostProvider.getPostByUser(mAuthProvider.getUid().toString())
        val opciones : FirestoreRecyclerOptions<Post> = FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post::class.java).build()
        mAdapter = MyPostsAdapter(context, opciones,this)
        binding.recyclerViewMyPost.adapter = mAdapter
        mAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        mAdapter.stopListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mListener != null) {
            mListener.remove()
        }
    }


//  RECYCLER VIEW

    private fun setupRecyclerView(){
        binding.recyclerViewMyPost.layoutManager = LinearLayoutManager(this.context)
    }








    private fun goToEditProfile(){
        startActivity(Intent(this.context,EditProfileActivity::class.java))
    }


    private fun getPostNumber(){
        mPostProvider.getPostByUser(mAuthProvider.getUid()!!).get().addOnSuccessListener {
            val number:Int = it.size()
            binding.txtPostNumber.text = number.toString()
        }
    }


    private fun getUser(){
        mUsersProvider.getUser(mAuthProvider.getUid()!!).addOnSuccessListener {
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

    override fun onItemClick(postId: String) {
        val intent= Intent(this.context, PostDetailActivity::class.java)
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("id",postId)
        startActivity(intent)
    }

}