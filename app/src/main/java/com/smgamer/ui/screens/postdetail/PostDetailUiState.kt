package com.smgamer.ui.screens.postdetail

import com.smgamer.domain.model.CommentWithUser
import com.smgamer.domain.model.PostData
import com.smgamer.domain.model.User

data class PostDetailUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val postData: PostData? = null,
    val commentsWithUsers: List<CommentWithUser> = emptyList(),
    val error: String? = null
)
