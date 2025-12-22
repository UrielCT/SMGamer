package com.smgamer.ui.screens.editprofile

import com.smgamer.domain.model.User

data class EditProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
