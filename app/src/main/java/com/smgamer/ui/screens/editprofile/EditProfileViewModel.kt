package com.smgamer.ui.screens.editprofile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smgamer.domain.usecases.GetCurrentUserUseCase
import com.smgamer.domain.usecases.UpdateUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState(isLoading = true))
    val uiState: StateFlow<EditProfileUiState> = _uiState

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val user = getCurrentUserUseCase()
                _uiState.update {
                    it.copy(user = user, isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.message ?: "Error al cargar el perfil", isLoading = false)
                }
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
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val currentUser = _uiState.value.user ?: return@launch

                val updatedUser = updateUserProfileUseCase(
                    context = context,
                    currentUser = currentUser,
                    name = name,
                    email = email,
                    phone = phone,
                    profileUri = profileUri,
                    coverUri = coverUri
                )

                _uiState.update {
                    it.copy(user = updatedUser, isLoading = false)
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.message ?: "Error al actualizar perfil", isLoading = false)
                }
            }
        }
    }
}