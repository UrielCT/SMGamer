package com.smgamer.ui.screens.newpost

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smgamer.domain.model.User
import com.smgamer.domain.usecases.GetCurrentUserUseCase
import com.smgamer.domain.usecases.cloudinary.UploadImageToCloudinaryUseCase
import com.smgamer.domain.usecases.posts.CreatePostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewPostViewModel @Inject constructor(
    private val createPostUseCase: CreatePostUseCase,
    private val uploadImageToCloudinaryUseCase: UploadImageToCloudinaryUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel()  {

    private val _uiState = MutableStateFlow(NewPostUiState())
    val uiState: StateFlow<NewPostUiState> = _uiState

    private val _currentUser = mutableStateOf<User?>(null)
    val currentUser: State<User?> = _currentUser

    init {
        loadCurrentUser()
    }


    private fun loadCurrentUser() {
        viewModelScope.launch {
            _currentUser.value = getCurrentUserUseCase()
        }
    }

    // createNew post
    fun createPost(
        context: Context,
        title: String,
        description: String,
        category: String,
        images: List<Uri>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        val idUser = _currentUser.value?.id ?: return onError("Usuario no autenticado")

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, isSuccess = false) }
            try {
                // 🧠 Subir imágenes en paralelo
                val uploadedUrls = coroutineScope {
                    images.map { uri ->
                        async {
                            uploadImageToCloudinaryUseCase(context, uri, "smgamer/posts")
                        }
                    }.awaitAll().filterNotNull()
                }

                if (uploadedUrls.isEmpty()) {
                    onError("No se pudieron subir las imágenes")
                    //_isLoading.value = false
                    return@launch
                }

                // 🧾 Crear el post en Firestore
                val result = createPostUseCase(idUser, title, description, category, uploadedUrls)
                result.onSuccess {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                    onSuccess()
                }.onFailure {
                    onError(it.message ?: "Error al crear el post")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Error al subir imágenes")
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun showError(message: String) {
        _uiState.update { it.copy(isLoading = false, error = message, isSuccess = false) }
    }
}