package com.smgamer.ui.screens.filteredposts

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smgamer.domain.model.User
import com.smgamer.domain.usecases.GetCurrentUserUseCase
import com.smgamer.domain.usecases.likes.LikePostUseCase
import com.smgamer.domain.usecases.likes.UnlikePostUseCase
import com.smgamer.domain.usecases.posts.GetPostsByCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilteredPostsViewModel @Inject constructor(
    private val getPostsByCategoryUseCase: GetPostsByCategoryUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val likePostUseCase: LikePostUseCase,
    private val unlikePostUseCase: UnlikePostUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FilteredPostsUiState())
    val uiState: StateFlow<FilteredPostsUiState> = _uiState

    private val _user = mutableStateOf<User?>(null)
    val user: State<User?> = _user

    private var currentJob: Job? = null

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            try {
                _user.value = getCurrentUserUseCase()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun loadPostsByCategory(category: String) {
        // Cancela si ya hay una suscripción activa
        currentJob?.cancel()

        currentJob = getPostsByCategoryUseCase(category)
            .onStart {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            }
            .onEach { posts ->
                _uiState.value = _uiState.value.copy(
                    posts = posts,
                    isLoading = false,
                    error = null
                )
            }
            .catch { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
            .launchIn(viewModelScope)
    }

    fun toggleLike(postId: String, userId: String) {
        viewModelScope.launch {
            val post = _uiState.value.posts.find { it.post.id == postId } ?: return@launch
            if (post.isLikedBy(userId)) {
                unlikePostUseCase(postId, userId)
            } else {
                likePostUseCase(postId, userId)
            }
        }
    }

}