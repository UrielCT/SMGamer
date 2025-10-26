package com.smgamer.ui.screens.editprofile

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smgamer.domain.model.User
import com.smgamer.domain.usecases.GetCurrentUserUseCase
import com.smgamer.domain.usecases.UpdateUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
) : ViewModel() {

    private val _user = mutableStateOf<User?>(null)
    val user: State<User?> = _user

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentUser = getCurrentUserUseCase()
                _user.value = currentUser
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateUserProfile(
        context: Context,
        name: String,
        email: String,
        phone: String,
        profileUri: Uri?,
        coverUri: Uri?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val currentUser = _user.value ?: return@launch
                val updated = updateUserProfileUseCase(
                    context, currentUser, name, email, phone, profileUri, coverUri
                )
                _user.value = updated
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Error al actualizar perfil")
            } finally {
                _isLoading.value = false
            }
        }
    }
}