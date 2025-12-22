package com.smgamer.ui.screens.newpost

data class NewPostUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)
