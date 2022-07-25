package com.example.socialmediagamer.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialmediagamer.model.adapters.ChatsAdapter
import com.example.socialmediagamer.databinding.FragmentChatsBinding
import com.example.socialmediagamer.model.models.Chat
import com.example.socialmediagamer.model.providers.AuthProvider
import com.example.socialmediagamer.model.providers.ChatsProvider
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query


class ChatsFragment : Fragment() {

    private var _binding: FragmentChatsBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAdapter:ChatsAdapter
    private lateinit var mChatsProvider: ChatsProvider
    private lateinit var mAuthProvider: AuthProvider


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatsBinding.inflate(inflater, container,false)

        setupRecyclerView()


        mChatsProvider = ChatsProvider()
        mAuthProvider= AuthProvider()


        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val query: Query = mChatsProvider.getAll(mAuthProvider.getUid().toString())
        val opciones : FirestoreRecyclerOptions<Chat> = FirestoreRecyclerOptions.Builder<Chat>().setQuery(query, Chat::class.java).build()
        mAdapter = ChatsAdapter(this.requireContext(),opciones)
        binding.recyclerViewChats.adapter = mAdapter
        mAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()

        mAdapter.stopListening()
    }


    override fun onDestroy() {
        super.onDestroy()
        if (mAdapter.getListener() != null){
            mAdapter.getListener().remove()
        }
        if (mAdapter.getListenerLastMessage() != null){
            mAdapter.getListenerLastMessage().remove()
        }
    }

    //  RECYCLER VIEW

    private fun setupRecyclerView(){
        binding.recyclerViewChats.layoutManager = LinearLayoutManager(this.context)
    }




}