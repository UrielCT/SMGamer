package com.example.socialmediagamer.view.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialmediagamer.R
import com.example.socialmediagamer.view.activities.MainActivity
import com.example.socialmediagamer.view.activities.PostActivity
import com.example.socialmediagamer.view.activities.PostDetailActivity
import com.example.socialmediagamer.model.adapters.PostsAdapter
import com.example.socialmediagamer.databinding.FragmentHomeBinding
import com.example.socialmediagamer.model.models.Post
import com.example.socialmediagamer.model.providers.AuthProvider
import com.example.socialmediagamer.model.providers.PostProvider
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.mancj.materialsearchbar.MaterialSearchBar


class HomeFragment : Fragment(), PostsAdapter.OnPostClickListener, MaterialSearchBar.OnSearchActionListener {

    private var _binding:FragmentHomeBinding? = null
    private val binding get() = _binding!!


    private lateinit var mAuthProvider: AuthProvider
    private lateinit var mPostProvider: PostProvider
    private lateinit var mPostAdapter: PostsAdapter
    private var mPostAdapterSearch : PostsAdapter? = null


    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container,false)

        setHasOptionsMenu(true)
        setupRecyclerView()

        mAuthProvider = AuthProvider()
        mPostProvider = PostProvider()



        binding.fab.setOnClickListener {
            goToPost()
        }


        binding.searchBar.setOnSearchActionListener(this)


        binding.searchBar.inflateMenu(R.menu.main_menu)

        binding.searchBar.menu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.itemLogout-> {
                    logOut()
                    true
                }
                else -> onOptionsItemSelected(it)
            }
        }
        return binding.root
    }



    private fun searchByTitle(title:String){
        val query:Query = mPostProvider.getPostByTitle(title)
        val opciones : FirestoreRecyclerOptions<Post> = FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post::class.java).build()
        mPostAdapterSearch = PostsAdapter(context,opciones,this)
        mPostAdapterSearch!!.notifyDataSetChanged()
        binding.recyclerViewHome.adapter = mPostAdapterSearch
        mPostAdapterSearch!!.startListening()
    }


    private fun getAllPost(){
        val query:Query = mPostProvider.gatAll()
        val opciones : FirestoreRecyclerOptions<Post> = FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post::class.java).build()
        mPostAdapter = PostsAdapter(context,opciones,this)
        mPostAdapter.notifyDataSetChanged()
        binding.recyclerViewHome.adapter = mPostAdapter
        mPostAdapter.startListening()
    }


    override fun onStart() {
        super.onStart()
        getAllPost()
    }

    override fun onStop() {
        super.onStop()
        mPostAdapter.stopListening()
        if (mPostAdapterSearch != null){
            mPostAdapterSearch!!.stopListening()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        if (mPostAdapter.getListener() != null){
            mPostAdapter.getListener().remove()
        }
    }




    private fun goToPost(){
        startActivity(Intent(this.context,PostActivity::class.java))
    }







    private fun logOut(){
        mAuthProvider.logout()
        val intent= Intent(this.context,MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }


    //  RECYCLER VIEW

    private fun setupRecyclerView(){
        binding.recyclerViewHome.layoutManager = LinearLayoutManager(this.context)
    }




    override fun onItemClick(postId: String) {
        val intent= Intent(this.context, PostDetailActivity::class.java)
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("id",postId)
        startActivity(intent)
    }





    override fun onSearchStateChanged(enabled: Boolean) {
        if (!enabled){
            getAllPost()
        }
    }

    override fun onSearchConfirmed(text: CharSequence?) {
        searchByTitle(text.toString().lowercase())
    }

    override fun onButtonClicked(buttonCode: Int) {
    }
}