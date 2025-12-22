package com.smgamer.ui.screens.chatdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smgamer.domain.model.Message
import com.smgamer.domain.usecases.GetCurrentUserUseCase
import com.smgamer.domain.usecases.chats.CreateOrGetChatUseCase
import com.smgamer.domain.usecases.chats.GetChatFlowUseCase
import com.smgamer.domain.usecases.messages.GetMessagesByChatUseCase
import com.smgamer.domain.usecases.messages.MarkMessagesAsViewedUseCase
import com.smgamer.domain.usecases.messages.SendMessageUseCase
import com.smgamer.domain.usecases.messages.UpdateMessageViewedUseCase
import com.smgamer.domain.usecases.posts.GetUserFlowUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatDetailViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val createOrGetChatUseCase: CreateOrGetChatUseCase,
    private val getChatFlowUseCase: GetChatFlowUseCase,
    private val getUserFlowUseCase: GetUserFlowUseCase,
    private val getMessagesByChatUseCase: GetMessagesByChatUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val updateMessageViewedUseCase: UpdateMessageViewedUseCase,
    private val markMessagesAsViewedUseCase: MarkMessagesAsViewedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatDetailUiState())
    val uiState: StateFlow<ChatDetailUiState> = _uiState

    private var meId: String? = null
    private var currentChatId: String? = null

    fun open(otherId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // 1) obtener user actual
            val me = getCurrentUserUseCase()
            meId = me?.id

            if (meId == null) {
                _uiState.update { it.copy(isLoading = false, error = "Usuario no autenticado") }
                return@launch
            }

            // 2) create or get chat
            val chat = createOrGetChatUseCase(meId!!, otherId)
            currentChatId = chat.id

            // 3) subscribe flows: chat info, other user info, messages
            viewModelScope.launch {
                getChatFlowUseCase(chat.id)
                    .collect { c -> _uiState.update { it.copy(chat = c) } }
            }

            viewModelScope.launch {
                getUserFlowUseCase(otherId)
                    .collect { dto -> _uiState.update { it.copy(otherUser = dto) } }
            }

            viewModelScope.launch {
                getMessagesByChatUseCase(chat.id)
                    .collect { msgs -> _uiState.update { it.copy(messages = msgs) } }
            }

            _uiState.update { it.copy(isLoading = false, error = null) }

            viewModelScope.launch {
                currentChatId?.let { chatId ->
                    meId?.let { userId ->
                        // Espera 1 seg para asegurar que ya se cargaron los mensajes
                        delay(1000)
                        markMessagesAsViewedUseCase(chatId, userId)
                    }
                }
            }

        }
    }

    fun onInputChange(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun sendMessage() {
        val chatId = currentChatId ?: return
        val me = meId ?: return
        val text = _uiState.value.inputText.trim()
        if (text.isBlank()) return

        val message = Message(
            id = "",
            idSender = me,
            idReceiver = _uiState.value.otherUser?.id ?: "",
            idChat = chatId,
            message = text,
            viewed = false,
            timestamp = System.currentTimeMillis()
        )

        viewModelScope.launch {
            sendMessageUseCase(message)
            _uiState.update { it.copy(inputText = "") }
        }
    }

    fun markViewed(messageId: String) {
        val chatId = currentChatId ?: return
        viewModelScope.launch { updateMessageViewedUseCase(chatId, messageId) }
    }

//
//    fun loadMessages(chatId: String) {
//        viewModelScope.launch {
//            _uiState.value = _uiState.value.copy(isLoading = true)
//            getMessagesByChatUseCase(chatId).collect { messages ->
//                _uiState.value = _uiState.value.copy(messages = messages, isLoading = false)
//            }
//        }
//    }
//
//    fun sendMessage(chatId: String, senderId: String, receiverId: String) {
//        val text = _uiState.value.inputText.trim()
//        if (text.isBlank()) return
//        val message = Message(
//            id = "", // will be set by service
//            idChat = chatId,
//            idSender = senderId,
//            idReceiver = receiverId,
//            message = text,
//            viewed = false,
//            timestamp = System.currentTimeMillis()
//        )
//        viewModelScope.launch {
//            sendMessageUseCase(message)
//            // clear input
//            _uiState.value = _uiState.value.copy(inputText = "")
//        }
//    }

//    fun markViewed(chatId: String, messageId: String) {
//        viewModelScope.launch {
//            updateMessageViewedUseCase(chatId, messageId)
//        }
//    }

//    fun onInputChange(text: String) {
//        _uiState.value = _uiState.value.copy(inputText = text)
//    }
}