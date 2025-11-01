package com.smgamer.ui.screens.userprofile

import com.smgamer.domain.model.Post
import com.smgamer.domain.model.User

data class UserProfileUiState(
    val user: User? = null,
    val posts: List<Post> = emptyList(),
    val isMyUser: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
