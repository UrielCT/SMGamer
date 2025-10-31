package com.smgamer.ui.screens.postdetail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smgamer.domain.model.User
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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


    private var postJob: Job? = null
    private var commentsJob: Job? = null


    fun loadPost(postId: String) {
        postJob?.cancel()
        postJob = getPostByIdFlowUseCase(postId)
            .onStart { _uiState.update { it.copy(isLoading = true, error = null) } }
            .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message ?: "Error") } }
            .onEach { postData ->
                _uiState.update { it.copy(isLoading = false, postData = postData) }
            }
            .launchIn(viewModelScope)

        // Cargar comentarios enriquecidos
        commentsJob?.cancel()
        commentsJob = getCommentsWithUsersUseCase(postId)
            .onStart { /* opcional: seteo de loading de comments */ }
            .catch { e ->
                // puedes almacenar error en estado si querés
                _uiState.update { it.copy(error = e.message ?: "Error en comentarios") }
            }
            .onEach { commentsWithUsers ->
                _uiState.update { it.copy(commentsWithUsers = commentsWithUsers) }
            }
            .launchIn(viewModelScope)
    }


    fun createComment(
        text: String,
        postId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val idUser = _currentUser.value?.id ?: return onError("Usuario no autenticado")

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