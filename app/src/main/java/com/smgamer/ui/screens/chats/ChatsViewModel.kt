package com.smgamer.ui.screens.chats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smgamer.domain.usecases.GetChatsWithDetailsUseCase
import com.smgamer.domain.usecases.GetCurrentUserUseCase
import com.smgamer.domain.usecases.chats.GetChatsByUserUseCase
//import com.smgamer.domain.usecases.chats.GetChatsWithUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel @Inject constructor(
   // private val getChatsByUserUseCase: GetChatsByUserUseCase,
   // private val getChatsWithUsersUseCase: GetChatsWithUsersUseCase,
    private val getChatsWithDetailsUseCase: GetChatsWithDetailsUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {


    private val _uiState = MutableStateFlow(ChatsUiState())
    val uiState: StateFlow<ChatsUiState> = _uiState

    private var currentUserId: String? = null

    init {
        loadCurrentUserAndChats()
    }

    private fun loadCurrentUserAndChats() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val user = getCurrentUserUseCase()
                currentUserId = user?.id

                if (currentUserId != null) {
                    getChatsWithDetailsUseCase(currentUserId!!)
                        .onEach { chatsData ->
                            _uiState.value = _uiState.value.copy(
                                chats = chatsData,
                                isLoading = false,
                                error = null
                            )
                        }
                        .launchIn(viewModelScope)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Usuario no autenticado"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }

//    private fun loadCurrentUserAndChats() {
//        viewModelScope.launch {
//            _uiState.value = _uiState.value.copy(isLoading = true)
//            try {
//                val user = getCurrentUserUseCase()
//                currentUserId = user?.id
//
//                if (currentUserId != null) {
//                    getChatsWithUsersUseCase(currentUserId!!)
//                        .onEach { chatsData ->
//                            _uiState.value = _uiState.value.copy(
//                                chats = chatsData,
//                                isLoading = false,
//                                error = null
//                            )
//                        }
//                        .launchIn(viewModelScope)
//                } else {
//                    _uiState.value = _uiState.value.copy(
//                        isLoading = false,
//                        error = "Usuario no autenticado"
//                    )
//                }
//            } catch (e: Exception) {
//                _uiState.value = _uiState.value.copy(
//                    isLoading = false,
//                    error = e.message ?: "Error desconocido"
//                )
//            }
//        }
//    }

//    private fun loadCurrentUserAndChats() {
//        viewModelScope.launch {
//            _uiState.value = _uiState.value.copy(isLoading = true)
//            try {
//                val user = getCurrentUserUseCase()
//                currentUserId = user?.id
//
//                if (currentUserId != null) {
//                    getChatsByUserUseCase(currentUserId!!)
//                        .onEach { chats ->
//                            _uiState.value = _uiState.value.copy(
//                                chats = chats,
//                                isLoading = false,
//                                error = null
//                            )
//                        }
//                        .launchIn(viewModelScope)
//                } else {
//                    _uiState.value = _uiState.value.copy(
//                        isLoading = false,
//                        error = "Usuario no autenticado"
//                    )
//                }
//            } catch (e: Exception) {
//                _uiState.value = _uiState.value.copy(
//                    isLoading = false,
//                    error = e.message ?: "Error desconocido"
//                )
//            }
//        }
//    }

    fun getCurrentUserId(): String? = currentUserId

//    private fun loadCurrentUserAndChats() {
//        viewModelScope.launch {
//            try {
//                _uiState.value = _uiState.value.copy(isLoading = true)
//
//                // ✅ Obtener el usuario actual (desde FirebaseAuth o local)
//                val user = getCurrentUserUseCase()
//                currentUserId = user?.id
//
//                if (currentUserId != null) {
//                    getChatsByUserUseCase(currentUserId!!)
//                        .collect { chats ->
//                            _uiState.value = _uiState.value.copy(
//                                chats = chats,
//                                isLoading = false,
//                                error = null
//                            )
//                        }
//                } else {
//                    _uiState.value = _uiState.value.copy(
//                        isLoading = false,
//                        error = "Usuario no autenticado"
//                    )
//                }
//            } catch (e: Exception) {
//                _uiState.value = _uiState.value.copy(
//                    isLoading = false,
//                    error = e.message ?: "Error desconocido"
//                )
//            }
//        }
//    }
//
//    fun getCurrentUserId(): String? = currentUserId

//    fun loadChats(userId: String) {
//        viewModelScope.launch {
//            _uiState.value = _uiState.value.copy(isLoading = true)
//            getChatsByUserUseCase(userId).collect { chats ->
//                _uiState.value = _uiState.value.copy(chats = chats, isLoading = false)
//            }
//        }
//    }
}