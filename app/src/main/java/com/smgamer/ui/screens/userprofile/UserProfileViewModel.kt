package com.smgamer.ui.screens.userprofile

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smgamer.domain.model.User
import com.smgamer.domain.usecases.GetCurrentUserUseCase
import com.smgamer.domain.usecases.GetUserByIdUseCase
import com.smgamer.domain.usecases.posts.DeletePostUseCase
import com.smgamer.domain.usecases.posts.GetPostsByUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val getPostsByUserUseCase: GetPostsByUserUseCase,
    private val deletePostUseCase: DeletePostUseCase
) : ViewModel() {

    private val _user = mutableStateOf<User?>(null)
    val user: State<User?> = _user

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    private val _isMyUser = mutableStateOf(false)
    val isMyUser: State<Boolean> = _isMyUser

    private val _uiState = MutableStateFlow(UserProfileUiState(isLoading = true))
    val uiState: StateFlow<UserProfileUiState> = _uiState

    private var postsJob: Job? = null


    fun loadUserProfile(userId: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val currentUser = getCurrentUserUseCase()

                val isMyUser = userId == null || userId == currentUser?.id
                val targetUser = if (isMyUser) currentUser else getUserByIdUseCase(userId!!)

                if (targetUser == null) {
                    _uiState.update { it.copy(error = "Usuario no encontrado", isLoading = false) }
                    return@launch
                }

                // Actualizamos info del usuario
                _uiState.update {
                    it.copy(
                        user = targetUser,
                        isMyUser = isMyUser,
                        isLoading = false
                    )
                }

                // Cargamos los posts
                observeUserPosts(targetUser.id)

            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    private fun observeUserPosts(userId: String) {
        postsJob?.cancel()
        postsJob = viewModelScope.launch {
            getPostsByUserUseCase(userId)
                .catch { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
                .collectLatest { posts ->
                    _uiState.update {
                        it.copy(posts = posts, isLoading = false, error = null)
                    }
                }
        }
    }

//    fun deletePost(postId: String) {
//        viewModelScope.launch {
//            try {
//                deletePostUseCase(postId)
//            } catch (e: Exception) {
//                _uiState.value = _uiState.value.copy(error = e.message)
//            }
//        }
//    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            try {
                // 1️⃣ Eliminar del repositorio / Firestore
                deletePostUseCase(postId)

                // 2️⃣ Actualizar lista localmente para reflejarlo de inmediato
                _uiState.update { currentState ->
                    val updatedPosts = currentState.posts.filterNot { it.id == postId }
                    currentState.copy(posts = updatedPosts)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

}
