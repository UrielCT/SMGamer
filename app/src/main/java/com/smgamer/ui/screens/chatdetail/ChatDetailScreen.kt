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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import com.smgamer.R
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
import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isMine: Boolean,
    val timestamp: Long,
    val status: MessageStatus
)

enum class MessageStatus {
    SENT, DELIVERED, SEEN
}

@SuppressLint("NewApi", "FrequentlyChangedStateReadInComposition")
@Composable
fun ChatDetailScreen(modifier: Modifier = Modifier) {
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

//    val messages = remember {
//        mutableStateListOf(
//            ChatMessage(UUID.randomUUID().toString(), "Hola, ¿cómo estás?", false, System.currentTimeMillis(), MessageStatus.SENT),
//            ChatMessage(UUID.randomUUID().toString(), "Bien, ¿y vos?", true, System.currentTimeMillis(), MessageStatus.SENT),
//            ChatMessage(UUID.randomUUID().toString(), "Todo bien, gracias por preguntar 😊", false, System.currentTimeMillis(), MessageStatus.SENT),
//            ChatMessage(UUID.randomUUID().toString(), "¿Qué tal tu día?", true, System.currentTimeMillis(), MessageStatus.SENT),
//            ChatMessage(UUID.randomUUID().toString(), "Bastante ocupado, pero bien. ¿Y el tuyo?", false, System.currentTimeMillis(), MessageStatus.SENT),
//            ChatMessage(UUID.randomUUID().toString(), "Tranquilo, solo trabajando un poco desde casa.", true, System.currentTimeMillis(), MessageStatus.SENT),
//            ChatMessage(UUID.randomUUID().toString(), "Qué bueno. ¿Terminaste ese proyecto que me contaste?", false, System.currentTimeMillis(), MessageStatus.SENT),
//            ChatMessage(UUID.randomUUID().toString(), "Sí, justo lo entregué ayer.", true, System.currentTimeMillis(), MessageStatus.SENT),
//            ChatMessage(UUID.randomUUID().toString(), "¡Genial! Seguro te salió excelente.", false, System.currentTimeMillis(), MessageStatus.SENT),
//            ChatMessage(UUID.randomUUID().toString(), "Espero que sí 😅", true, System.currentTimeMillis(), MessageStatus.SENT),
//            ChatMessage(UUID.randomUUID().toString(), "¿Vas a descansar hoy?", false, System.currentTimeMillis(), MessageStatus.SENT),
//            ChatMessage(UUID.randomUUID().toString(), "Un poco, sí. Pero también quiero avanzar con algunas cosas.", true, System.currentTimeMillis(), MessageStatus.SENT),
//            ChatMessage(UUID.randomUUID().toString(), "Es entendible, a veces uno nunca termina del todo.", false, System.currentTimeMillis(), MessageStatus.SENT),
//            ChatMessage(UUID.randomUUID().toString(), "Exactamente 😄", true, System.currentTimeMillis(), MessageStatus.SENT),
//            ChatMessage(UUID.randomUUID().toString(), "¿Te gustaría salir a caminar un rato esta tarde?", false, System.currentTimeMillis(), MessageStatus.SENT),
//            ChatMessage(UUID.randomUUID().toString(), "Sí, suena bien. ¿A qué hora?", true, System.currentTimeMillis(), MessageStatus.SENT),
//            ChatMessage(UUID.randomUUID().toString(), "Como a las 5 pm, si te viene bien.", false, System.currentTimeMillis(), MessageStatus.SENT),
//            ChatMessage(UUID.randomUUID().toString(), "Perfecto, nos vemos entonces.", true, System.currentTimeMillis(), MessageStatus.SENT),
//            ChatMessage(UUID.randomUUID().toString(), "Genial, llevo algo para merendar también.", false, System.currentTimeMillis(), MessageStatus.SENT),
//            ChatMessage(UUID.randomUUID().toString(), "¡Qué buena idea! 😋", true, System.currentTimeMillis(), MessageStatus.SENT),
//            ChatMessage(UUID.randomUUID().toString(), "Ah, y también quería preguntarte algo.", false, System.currentTimeMillis(), MessageStatus.SENT),
//            ChatMessage(UUID.randomUUID().toString(), "Claro, dime.", true, System.currentTimeMillis(), MessageStatus.SENT),
//            ChatMessage(UUID.randomUUID().toString(), "¿Vas a estar disponible este fin de semana?", false, System.currentTimeMillis(), MessageStatus.SENT),
//            ChatMessage(UUID.randomUUID().toString(), "Sí, creo que sí. ¿Por qué?", true, System.currentTimeMillis(), MessageStatus.SENT),
//            ChatMessage(UUID.randomUUID().toString(), "Quería ver si hacemos un plan con amigos.", false, System.currentTimeMillis(), MessageStatus.SENT),
//            ChatMessage(UUID.randomUUID().toString(), "Perfecto, me sumo.", true, System.currentTimeMillis(), MessageStatus.SENT),
//            ChatMessage(UUID.randomUUID().toString(), "¡Genial! Entonces confirmamos hora y lugar más tarde.", false, System.currentTimeMillis(), MessageStatus.SENT),
//            ChatMessage(UUID.randomUUID().toString(), "Sí, hablemos después.", true, System.currentTimeMillis(), MessageStatus.SENT),
//            ChatMessage(UUID.randomUUID().toString(), "Ok, que tengas buena tarde mientras tanto.", false, System.currentTimeMillis(), MessageStatus.SENT),
//            ChatMessage(UUID.randomUUID().toString(), "Igualmente, nos vemos.", true, System.currentTimeMillis(), MessageStatus.SENT)
//        )
//    }

//    val messages = rememberSaveable(saver = listSaver(
//        save = { it.toList() },
//        restore = { it.toMutableStateList() }
//    )) {
//        mutableStateListOf<ChatMessage>()
//    }

    val messages = remember { mutableStateListOf<ChatMessage>() }


    var text by remember { mutableStateOf("") }

    val shouldScroll by remember {
        derivedStateOf { scrollState.firstVisibleItemIndex == 0 }
    }

    LaunchedEffect(messages.size, shouldScroll) {
        if (shouldScroll) {
            scrollState.scrollToItem(0)
        }
    }

//    LaunchedEffect(Unit) {
//        snapshotFlow { messages.size }
//            .collect {
//                if (shouldScroll) scrollState.scrollToItem(0)
//            }
//    }

    fun onSendMessage(){
        if (text.isNotBlank()) {
            val newMessage = ChatMessage(
                id = UUID.randomUUID().toString(),
                text.trim(),
                isMine = true,
                timestamp = System.currentTimeMillis(),
                status = MessageStatus.SENT
            )
            messages.add(0,newMessage)
            text = ""

            // 👇 Scrollea al último mensaje
            coroutineScope.launch {
                withFrameNanos { } // espera el próximo frame
                scrollState.animateScrollToItem(0)
            }
        }
    }


    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .imePadding()
    ) {

        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = true, // importante: normal, de arriba hacia abajo
            state = scrollState
        ) {
            items(
                count = messages.size,
                key = { index -> messages[index].id }
            ) { index ->
                val message = messages[index]
                val nextMessage = messages.getOrNull(index + 1)
                val showTail = nextMessage?.isMine != message.isMine
                MessageBubble(message = message, showTail = showTail)
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
                value = text,
                onValueChange = { text = it },
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