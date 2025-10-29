package com.smgamer.ui.screens.home

import com.smgamer.domain.model.Post
import com.smgamer.domain.model.PostData

data class HomeUiState(
    //All posts
    val postsWithUsers: List<PostData> = emptyList(),
    val posts: List<Post> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,

    // Search by title
    val searchQuery: String = "",
    val searchResults: List<Post> = emptyList(),
    val searchLoading: Boolean = false,
    val searchError: String? = null,

    // Category filter
    val categoryResults: List<Post> = emptyList(),
    val categoryLoading: Boolean = false,
    val categoryError: String? = null,

    // Posts by user
    val userPosts: List<Post> = emptyList(),
    val userPostsLoading: Boolean = false,
    val userPostsError: String? = null,

    // Selected single post
    val selectedPost: Post? = null,
    val selectedPostLoading: Boolean = false,
    val selectedPostError: String? = null,

    // Delete post status
    val deleteLoading: Boolean = false,
    val deleteSuccess: Boolean = false,
    val deleteError: String? = null
)

