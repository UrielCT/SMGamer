package com.smgamer.ui.screens.home

import com.smgamer.domain.model.PostData
import com.smgamer.domain.model.User

data class HomeUiState(
    val user: User? = null,
    val posts: List<PostData> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
)

