package com.smgamer.ui.screens.userprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
                        isLoading = false,
                        error = null
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
//                .onStart {
//                    _uiState.update { it.copy(isLoading = true, error = null) }
//                }
                .catch { e ->
                    _uiState.update {
                        it.copy(error = e.message ?: "Error al cargar posts", isLoading = false)
                    }
                }
                .collectLatest { posts ->
                    _uiState.update {
                        it.copy(posts = posts, isLoading = false, error = null)
                    }
                }
        }
    }


    fun deletePost(postId: String) {
        viewModelScope.launch {
            try {
                deletePostUseCase(postId)
                _uiState.update { state ->
                    state.copy(posts = state.posts.filterNot { it.id == postId })
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.message ?: "Error al eliminar post")
                }
            }
        }
    }

}
