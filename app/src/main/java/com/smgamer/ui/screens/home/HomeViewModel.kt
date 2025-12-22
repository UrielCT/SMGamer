package com.smgamer.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smgamer.domain.usecases.likes.UnlikePostUseCase
import com.smgamer.domain.usecases.GetCurrentUserUseCase
import com.smgamer.domain.usecases.LogOutUseCase
import com.smgamer.domain.usecases.likes.LikePostUseCase
import com.smgamer.domain.usecases.posts.GetAllPostsUseCase
import com.smgamer.domain.usecases.posts.GetPostsByTitleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllPostsUseCase: GetAllPostsUseCase,
    private val getPostsByTitleUseCase: GetPostsByTitleUseCase,
    private val likePostUseCase: LikePostUseCase,
    private val unlikePostUseCase: UnlikePostUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: LogOutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadUserProfile()
        observePosts()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            try {
                val user = getCurrentUserUseCase()
                _uiState.update { it.copy(user = user) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun onInputChange(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }


    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun observePosts() {
        viewModelScope.launch {
            _uiState
                .map { it.inputText }            // Escucha solo el texto
                .debounce(300)        // Espera un poco entre tipeos
                .distinctUntilChanged()          // Evita duplicados
                .flatMapLatest { query ->        // Cambia de fuente según query
                    if (query.isBlank()) {
                        getAllPostsUseCase()
                    } else {
                        getPostsByTitleUseCase(query)
                    }
                }
                .onStart {
                    _uiState.update { it.copy(isLoading = true) }
                }
                .catch { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message)
                    }
                }
                .collect { posts ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = null,
                            posts = posts.ifEmpty { emptyList() }
                        )
                    }
                }
        }
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


    fun logout(onSuccess: () -> Unit, onError: (Throwable) -> Unit = {}) {
        viewModelScope.launch {
            try {
                logoutUseCase()
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
}