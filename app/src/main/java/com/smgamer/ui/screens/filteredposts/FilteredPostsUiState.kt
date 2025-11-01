package com.smgamer.ui.screens.filteredposts

import com.smgamer.domain.model.PostData

data class FilteredPostsUiState(
    val posts: List<PostData> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)