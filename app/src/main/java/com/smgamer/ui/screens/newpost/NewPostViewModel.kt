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
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("NAME_SHADOWING")
@HiltViewModel
class NewPostViewModel @Inject constructor(
    private val createPostUseCase: CreatePostUseCase,
    private val uploadImageToCloudinaryUseCase: UploadImageToCloudinaryUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel()  {

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

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
            _isLoading.value = true
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
                    _isLoading.value = false
                    return@launch
                }

                // 🧾 Crear el post en Firestore
                val result = createPostUseCase(idUser, title, description, category, uploadedUrls)
                result.onSuccess {
                    onSuccess()
                }.onFailure {
                    onError(it.message ?: "Error al crear el post")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Error al subir imágenes")
            } finally {
                _isLoading.value = false
            }
        }
    }
}