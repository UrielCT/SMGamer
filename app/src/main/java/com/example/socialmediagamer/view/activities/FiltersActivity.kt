package com.example.socialmediagamer.view.activities

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.socialmediagamer.model.adapters.PostsAdapter
import com.example.socialmediagamer.databinding.ActivityFiltersBinding
import com.example.socialmediagamer.model.models.Post
import com.example.socialmediagamer.model.providers.AuthProvider
import com.example.socialmediagamer.model.providers.PostProvider
import com.example.socialmediagamer.utils.ViewedMessageHelper
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query

class FiltersActivity : AppCompatActivity(), PostsAdapter.OnPostClickListener {

    private lateinit var binding: ActivityFiltersBinding

    private lateinit var mExtraCategory:String


    private lateinit var mAuthProvider: AuthProvider
    private lateinit var mPostProvider: PostProvider
    private lateinit var mPostAdapter: PostsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFiltersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        mAuthProvider = AuthProvider()
        mPostProvider = PostProvider()


        setSupportActionBar(binding.topAppbar.toolbar)
        supportActionBar!!.title = ""
        binding.topAppbar.titulo.text = "Filtros"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        mExtraCategory = intent.getStringExtra("category").toString()

        Toast.makeText(this, "La categoria que selecciono es: $mExtraCategory",Toast.LENGTH_SHORT).show()

    }


    override fun onStart() {
        super.onStart()
        val query: Query = mPostProvider.getPostByCategoryAndTimestamp(mExtraCategory)
        val opciones : FirestoreRecyclerOptions<Post> = FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post::class.java).build()
        mPostAdapter = PostsAdapter(this,opciones,this, binding.txtNumberFilter)
        binding.recyclerViewFilter.adapter = mPostAdapter
        mPostAdapter.startListening()
        ViewedMessageHelper().updateOnline(true,this)
    }

    override fun onStop() {
        super.onStop()
        mPostAdapter.stopListening()
    }


    override fun onPause() {
        super.onPause()
        ViewedMessageHelper().updateOnline(false,this)
    }





    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            finish()
        }
        return true
    }


    private fun setupRecyclerView(){
        binding.recyclerViewFilter.layoutManager = GridLayoutManager(this,2)
    }

    override fun onItemClick(postId: String) {
        TODO("Not yet implemented")
    }


}