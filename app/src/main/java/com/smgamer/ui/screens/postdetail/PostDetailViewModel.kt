package com.smgamer.ui.screens.postdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smgamer.domain.usecases.GetCurrentUserUseCase
import com.smgamer.domain.usecases.comments.CreateCommentUseCase
import com.smgamer.domain.usecases.comments.GetCommentsByPostUseCase
import com.smgamer.domain.usecases.posts.GetPostByIdFlowUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val getPostByIdFlowUseCase: GetPostByIdFlowUseCase,
    private val getCommentsWithUsersUseCase: GetCommentsByPostUseCase,
    private val createCommentUseCase: CreateCommentUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(PostDetailUiState(isLoading = true))
    val uiState: StateFlow<PostDetailUiState> = _uiState.asStateFlow()

    private var postJob: Job? = null
    private var commentsJob: Job? = null

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() = viewModelScope.launch {
        try {
            val user = getCurrentUserUseCase()
            _uiState.update { it.copy(user = user, error = null) }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(error = e.message ?: "Error al cargar el perfil", isLoading = false)
            }
        }
    }


    fun loadPost(postId: String) {
        postJob?.cancel()
        postJob = viewModelScope.launch {
            combine(
                getPostByIdFlowUseCase(postId),
                getCommentsWithUsersUseCase(postId)
            ) { postData, comments ->
                postData to comments
            }
                .onStart { _uiState.update { it.copy(isLoading = true, error = null) } }
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message ?: "Error desconocido") }
                }
                .collect { (postData, comments) ->
                    _uiState.update {
                        it.copy(
                            postData = postData,
                            commentsWithUsers = comments,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }


    fun createComment(
        text: String,
        postId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val idUser = _uiState.value.user?.id ?: return onError("Usuario no autenticado")

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // 🧾 Crear el post en Firestore
                val result = createCommentUseCase(text, idUser, postId)
                result.onSuccess {
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


    override fun onCleared() {
        super.onCleared()
        postJob?.cancel()
        commentsJob?.cancel()
    }
}