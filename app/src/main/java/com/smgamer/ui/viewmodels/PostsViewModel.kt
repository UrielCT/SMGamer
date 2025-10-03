package com.smgamer.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PostsViewModel @Inject constructor(): ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _loading = MutableLiveData(false)

    // createPost

    // getAllPosts

    // getPostById

    // updatePost

    // deletePost



}