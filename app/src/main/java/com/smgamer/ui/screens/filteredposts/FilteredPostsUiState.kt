package com.smgamer.ui.screens.filteredposts

import com.smgamer.domain.model.PostData
import com.smgamer.domain.model.User

data class FilteredPostsUiState(
    val user: User? = null,
    val posts: List<PostData> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)