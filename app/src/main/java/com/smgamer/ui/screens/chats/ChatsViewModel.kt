package com.smgamer.ui.screens.chats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smgamer.domain.usecases.GetChatsWithDetailsUseCase
import com.smgamer.domain.usecases.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel @Inject constructor(
    private val getChatsWithDetailsUseCase: GetChatsWithDetailsUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatsUiState())
    val uiState: StateFlow<ChatsUiState> = _uiState

    init {
        loadCurrentUserAndChats()
    }

    private fun loadCurrentUserAndChats() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val user = getCurrentUserUseCase()


                if (user?.id != null) {
                    getChatsWithDetailsUseCase(user.id)
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

}