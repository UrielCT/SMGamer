package com.smgamer.ui.screens.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smgamer.domain.usecases.likes.UnlikePostUseCase
import com.smgamer.domain.model.User
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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


//private val getPostsByCategoryUseCase: GetPostsByCategoryUseCase,
//private val getPostsByUserUseCase: GetPostsByUserUseCase,
//private val getPostByIdUseCase: GetPostByIdUseCase,
//private val deletePostUseCase: DeletePostUseCase

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllPostsUseCase: GetAllPostsUseCase,
    private val getPostsByTitleUseCase: GetPostsByTitleUseCase,
    private val likePostUseCase: LikePostUseCase,
    private val unlikePostUseCase: UnlikePostUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: LogOutUseCase,

) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    private val _user = mutableStateOf<User?>(null)
    val user: State<User?> = _user

    // For handling search text typed by user
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    init {
        loadUserProfile()
        observePosts()
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

    // 🔹 Nuevo: Observa los cambios en el texto y decide qué traer
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun observePosts() {
        viewModelScope.launch {
            _searchQuery
                .debounce(300) // evita llamadas excesivas
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    if (query.isBlank()) {
                        getAllPostsUseCase()
                    } else {
                        getPostsByTitleUseCase(query)
                    }
                }
                .onStart { _uiState.update { it.copy(isLoading = true) } }
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { posts ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = null,
                            postsWithUsers = posts
                        )
                    }
                }
        }
    }


    // 🔹 Actualiza el texto del buscador
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }


    fun toggleLike(postId: String, userId: String) {
        viewModelScope.launch {
            val post = _uiState.value.postsWithUsers.find { it.post.id == postId } ?: return@launch
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




//    fun getAllPosts() {
//        viewModelScope.launch {
//            getAllPostsUseCase()
//                .onStart { _uiState.update { it.copy(isLoading = true, error = null) } }
//                .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
//                .collect { postsWithUsers ->
//                    _uiState.update {
//                        it.copy(
//                            isLoading = false,
//                            postsWithUsers = postsWithUsers
//                        )
//                    }
//                }
//        }
//    }




//    private fun observeAllPosts() {
//        allPostsJob?.cancel()
//        allPostsJob = viewModelScope.launch {
//            getAllPostsUseCase()
//                .onStart { _uiState.update { it.copy(isLoading = true, error = null) } }
//                .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
//                .collect { posts ->
//                    _uiState.update { it.copy(isLoading = false, posts = posts) }
//                }
//        }
//    }



    // PUBLIC: set search query from UI (TextField onValueChange)
//    fun setTitleQuery(query: String) {
//        _titleQuery.value = query
//    }
//
//    @OptIn(FlowPreview::class)
//    private fun observeTitleSearch() {
//        // Debounce user's typing for better UX
//        viewModelScope.launch {
//            titleQuery
//                .debounce(300)
//                .distinctUntilChanged()
//                .collectLatest { q ->
//                    performTitleSearch(q)
//                }
//        }
//    }
//
//    private fun performTitleSearch(query: String) {
//        searchJob?.cancel()
//        if (query.isBlank()) {
//            _uiState.update { it.copy(searchQuery = "", searchResults = emptyList(), searchLoading = false) }
//            return
//        }
//
//        searchJob = viewModelScope.launch {
//            getPostsByTitleUseCase(query)
//                .onStart { _uiState.update { it.copy(searchLoading = true, searchQuery = query, searchError = null) } }
//                .catch { e -> _uiState.update { it.copy(searchLoading = false, searchError = e.message) } }
//                .collect { posts ->
//                    _uiState.update { it.copy(searchLoading = false, searchResults = posts, searchQuery = query) }
//                }
//        }
//    }




//    fun getPostsByCategory(category: String) {
//        categoryJob?.cancel()
//        categoryJob = viewModelScope.launch {
//            getPostsByCategoryUseCase(category)
//                .onStart { _uiState.update { it.copy(categoryLoading = true, categoryError = null) } }
//                .catch { e -> _uiState.update { it.copy(categoryLoading = false, categoryError = e.message) } }
//                .collect { posts ->
//                    _uiState.update { it.copy(categoryLoading = false, categoryResults = posts) }
//                }
//        }
//    }

//    fun getPostsByUserId(userId: String) {
//        userPostsJob?.cancel()
//        userPostsJob = viewModelScope.launch {
//            getPostsByUserUseCase(userId)
//                .onStart { _uiState.update { it.copy(userPostsLoading = true, userPostsError = null) } }
//                .catch { e -> _uiState.update { it.copy(userPostsLoading = false, userPostsError = e.message) } }
//                .collect { posts ->
//                    _uiState.update { it.copy(userPostsLoading = false, userPosts = posts) }
//                }
//        }
//    }

//    suspend fun getPostById(postId: String) {
//        _uiState.update { it.copy(selectedPostLoading = true, selectedPostError = null) }
//        when (val res = getPostByIdUseCase(postId)) {
//            is Result.Success -> { /* not used; Result is kotlin.Result-like, use getOrNull below */ }
//        }
//        // Since we used Result<T> above, use getOrNull:
//        val res = getPostByIdUseCase(postId)
//        if (res.isSuccess) {
//            val post = res.getOrNull()
//            _uiState.update { it.copy(selectedPost = post, selectedPostLoading = false) }
//        } else {
//            _uiState.update { it.copy(selectedPostLoading = false, selectedPostError = res.exceptionOrNull()?.message) }
//        }
//    }

//    fun getPostById(postId: String) {
//        viewModelScope.launch {
//            _uiState.update { it.copy(selectedPostLoading = true, selectedPostError = null) }
//            val res = getPostByIdUseCase(postId)
//            if (res.isSuccess) {
//                val post = res.getOrNull()
//                _uiState.update { it.copy(selectedPost = post, selectedPostLoading = false) }
//            } else {
//                _uiState.update {
//                    it.copy(
//                        selectedPostLoading = false,
//                        selectedPostError = res.exceptionOrNull()?.message
//                    )
//                }
//            }
//        }
//    }


//    fun deletePost(postId: String) {
//        viewModelScope.launch {
//            _uiState.update { it.copy(deleteLoading = true, deleteError = null, deleteSuccess = false) }
//            val res = deletePostUseCase(postId)
//            if (res.isSuccess) {
//                _uiState.update { it.copy(deleteLoading = false, deleteSuccess = true) }
//            } else {
//                _uiState.update { it.copy(deleteLoading = false, deleteError = res.exceptionOrNull()?.message ?: "Error") }
//            }
//        }
//    }

//    override fun onCleared() {
//        super.onCleared()
//        searchJob?.cancel()
//        categoryJob?.cancel()
//        userPostsJob?.cancel()
//        allPostsJob?.cancel()
//    }

//    init {
//        getAllPosts()
//    }
//
//    private fun getAllPosts() {
//        viewModelScope.launch {
//            _uiState.update { it.copy(isLoading = true, error = null) }
//
//            getAllPostsUseCase()
//                .onStart { _uiState.update { it.copy(isLoading = true, error = null) } }
//                .catch { e ->
//                    _uiState.update { it.copy(isLoading = false, error = e.message) }
//                }
//                .collect { postList ->
//                    _uiState.update { it.copy(isLoading = false, posts = postList) }
//                }
//        }
//    }





//    fun getAllPosts() {
//        viewModelScope.launch {
//            _uiState.update { it.copy(isLoading = true, error = null) }
//
//            val result = getAllPostsUseCase()
//
//            _uiState.update {
//                it.copy(
//                    isLoading = false,
//                    posts = result.getOrNull() ?: emptyList(),
//                    error = result.exceptionOrNull()?.message
//                )
//            }
//        }
//    }

//    fun getAllPosts() {
////        viewModelScope.launch {
////            _isLoading.value = true
////            _error.value = null
////            val result = getAllPostsUseCase()
////            _isLoading.value = false
////
////            result.onSuccess { _posts.value = it }
////                .onFailure { _error.value = it.message }
////        }
//
//        viewModelScope.launch {
//            getAllPostsUseCase()
//                .onStart { _isLoading.value = true }
//                .catch { e ->
//                    _error.value = e.message
//                    _isLoading.value = false
//                }
//                .collect { postList ->
//                    _posts.value = postList
//                    _isLoading.value = false
//                }
//        }
//    }



