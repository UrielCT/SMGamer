package com.smgamer.ui.screens.chatdetail

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.smgamer.R
import com.smgamer.domain.model.User
import com.smgamer.ui.screens.chatdetail.components.MessageBubble
import com.smgamer.ui.theme.CommonFontSizeDefault
import com.smgamer.ui.theme.CommonPaddingDefault
import com.smgamer.ui.theme.CommonPaddingLarge_med
import com.smgamer.ui.theme.CommonPaddingMicroMin
import com.smgamer.ui.theme.CommonPaddingMiddle
import com.smgamer.ui.theme.CommonPaddingMin
import com.smgamer.ui.theme.scaledFont
import com.smgamer.ui.theme.scaledPadding
import kotlinx.coroutines.launch

//enum class MessageStatus {
//    SENT, DELIVERED, SEEN
//}

@SuppressLint("NewApi", "FrequentlyChangedStateReadInComposition")
@Composable
fun ChatDetailScreen(
    otherId:String,
    modifier: Modifier = Modifier,
    chatDetailViewModel: ChatDetailViewModel = hiltViewModel(),
    navBack:() -> Unit
) {
    val uiState by chatDetailViewModel.uiState.collectAsState()

    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // si existe traerlo jusnto on la info del otro user, el chat, y los mensajes

    val shouldScroll by remember {
        derivedStateOf { scrollState.firstVisibleItemIndex == 0 }
    }

    LaunchedEffect(otherId) {
        chatDetailViewModel.open(otherId)
        //chatDetailViewModel.loadMessages(otherId)
    }

    LaunchedEffect(Unit) {
        snapshotFlow { uiState.messages.size }
            .collect {
                if (shouldScroll) scrollState.scrollToItem(0)
            }
    }

    fun onSendMessage() {
        coroutineScope.launch {
            withFrameNanos { }
            scrollState.animateScrollToItem(0)
        }
        chatDetailViewModel.sendMessage()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .imePadding()
    ) {

        ChatDetailTopBar(uiState.otherUser, onBack = { navBack() })

        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = true, // importante: normal, de arriba hacia abajo
            state = scrollState
        ) {
            items(
               count =  uiState.messages.size,
                key = { index -> uiState.messages[index].id }
            ) {  index ->
                val message = uiState.messages[index]
                val nextMessage = uiState.messages.getOrNull(index + 1)
                val showTail = nextMessage?.idSender != message.idSender
                MessageBubble(
                    message = message,
                    showTail = showTail,
                    isMine = message.idSender != (uiState.otherUser?.id ?: ""),
                    status = message.viewed
                )
            }
        }

        // Caja de texto y botón enviar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(
                    horizontal = scaledPadding(CommonPaddingDefault),
                    vertical = scaledPadding(CommonPaddingMicroMin)
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = uiState.inputText,
                onValueChange = chatDetailViewModel::onInputChange,
                textStyle = TextStyle(
                    fontSize = scaledFont(CommonFontSizeDefault),
                    lineHeight = scaledFont(CommonFontSizeDefault) * 1.2
                ),
                placeholder = {
                    Text(
                        text = stringResource(R.string.ph_message_txt),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = scaledFont(CommonFontSizeDefault),
                        lineHeight = scaledFont(CommonFontSizeDefault) * 1.2
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = scaledPadding(CommonPaddingLarge_med))
                    .clip(RoundedCornerShape(scaledPadding(CommonPaddingMiddle)))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(scaledPadding(CommonPaddingMiddle)),
                maxLines = 6,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions( onSend = { onSendMessage() } ),
            )

            Spacer(Modifier.width(scaledPadding(CommonPaddingMin)))

            IconButton(
                onClick = { onSendMessage() }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailTopBar(otherUser: User?, onBack:() -> Unit) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Imagen del otro usuario
                AsyncImage(
                    model = otherUser?.profileImage,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )

                Spacer(Modifier.width(12.dp))

                Column {
                    Text(
                        text = otherUser?.username ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (otherUser?.isOnline == true) "En línea" else "Desconectado",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (otherUser?.isOnline == true)
                            Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = { onBack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}
