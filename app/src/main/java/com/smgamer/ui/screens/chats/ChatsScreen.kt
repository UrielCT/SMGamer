package com.smgamer.ui.screens.chats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.smgamer.ui.components.ChatsCardItem
import com.smgamer.ui.theme.CommonPaddingDefault
import com.smgamer.ui.theme.CommonPaddingMin
import com.smgamer.ui.theme.DividerThickness

@Composable
fun ChatsScreen(
    modifier: Modifier = Modifier,
    chatsViewModel: ChatsViewModel = hiltViewModel(),
    navToChatDetail: (String) -> Unit
) {
    val uiState by chatsViewModel.uiState.collectAsState()
    //val currentUserId = chatsViewModel.getCurrentUserId()

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Text(uiState.error ?: "Error desconocido", color = Color.Red)
            }
            uiState.chats.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No tienes chats todavía.")
                }
            }
            else -> {
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    verticalArrangement = Arrangement.spacedBy(CommonPaddingMin)
                ) {
                    items(uiState.chats, key = { it.chat.id }) { chat ->
                        val other = chat.otherUser
                        val name = other?.username ?: "Usuario desconocido"
                        val image = other?.profileImage ?: ""
                        val isOnline = other?.isOnline ?: false
                        val lastMessage = chat.lastMessage?.message ?: "Sin mensajes aún"
                        val unreadCount = chat.unreadCount
                        ChatsCardItem(
                            name = name,
                            lastMessage = lastMessage,
                            unreadCount = unreadCount,
                            profileImageRes = image,
                            isOnline = isOnline,
                            navToChatDetail = {
                                other?.id?.let { navToChatDetail(it) }
                            }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = CommonPaddingDefault),
                            thickness = DividerThickness,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
            }
        }
    }
}