package com.smgamer.ui.screens.postdetail

import com.smgamer.domain.model.CommentWithUser
import com.smgamer.domain.model.PostData

data class PostDetailUiState(
    val isLoading: Boolean = false,
    val postData: PostData? = null,
    val commentsWithUsers: List<CommentWithUser> = emptyList(),
    val error: String? = null
)
