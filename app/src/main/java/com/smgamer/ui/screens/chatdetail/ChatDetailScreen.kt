package com.smgamer.ui.screens.chatdetail

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


data class ChatMessage(
    val text: String,
    val isMine: Boolean,
    val timestamp: Long,
    val status: MessageStatus
)

enum class MessageStatus {
    SENT, DELIVERED, SEEN
}

@SuppressLint("NewApi")
@Composable
fun ChatDetailScreen(modifier: Modifier){

    val messages = remember {
        mutableStateListOf(
            ChatMessage("Hola, ¿cómo estás?", false, System.currentTimeMillis(), MessageStatus.SENT),
            ChatMessage( "Bien, ¿y vos?", true, System.currentTimeMillis(), MessageStatus.SENT),
            ChatMessage("Hola, ¿cómo estás?", false, System.currentTimeMillis(), MessageStatus.SENT),
            ChatMessage( "Bien, ¿y vos?", true, System.currentTimeMillis(), MessageStatus.SENT),
            ChatMessage("Hola, ¿cómo estás?", false, System.currentTimeMillis(), MessageStatus.SENT),
            ChatMessage( "Bien, ¿y vos?", true, System.currentTimeMillis(), MessageStatus.SENT),
            ChatMessage("Hola, ¿cómo estás?", false, System.currentTimeMillis(), MessageStatus.SENT),
            ChatMessage( "Bien, ¿y vos?", true, System.currentTimeMillis(), MessageStatus.SENT),
            ChatMessage("Hola, ¿cómo estás?", false, System.currentTimeMillis(), MessageStatus.SENT),
            ChatMessage( "Bien, ¿y vos?", true, System.currentTimeMillis(), MessageStatus.SENT),
            ChatMessage("Hola, ¿cómo estás?", false, System.currentTimeMillis(), MessageStatus.SENT),
            ChatMessage( "Bien, ¿y vos?", true, System.currentTimeMillis(), MessageStatus.SENT),
            ChatMessage("Hola, ¿cómo estás?", false, System.currentTimeMillis(), MessageStatus.SENT),
            ChatMessage( "Bien, ¿y vos?", true, System.currentTimeMillis(), MessageStatus.SENT),
            ChatMessage("Hola, ¿cómo estás?", false, System.currentTimeMillis(), MessageStatus.SENT),
            ChatMessage( "Bien, ¿y vos?", true, System.currentTimeMillis(), MessageStatus.SENT),



        )

    }
    var text by remember { mutableStateOf("") }
    val scrollState = rememberLazyListState()

    Column(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f).padding(8.dp),
            reverseLayout = true,
            state = scrollState
        ) {
            items(messages.sortedByDescending { it.timestamp }) { message ->
                MessageBubble(message = message)
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Escribe un mensaje...") }
            )
            IconButton(onClick = {
                if (text.isNotBlank()) {
                    messages.add(
                        ChatMessage(
                            text.trim(),
                            isMine = true,
                            timestamp = System.currentTimeMillis(),
                            status = MessageStatus.SENT
                        )
                    )
                    text = ""
                }
            }) {
                Icon(Icons.Default.Send, contentDescription = "Enviar")
            }
        }
    }

}

@SuppressLint("NewApi")
@Composable
fun MessageBubble(message: ChatMessage) {
    val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = if (message.isMine) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .background(
                    color = if (message.isMine) Color(0xFFD2F8FF) else Color(0xFFE0E0E0),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(8.dp)
                .widthIn(max = 280.dp)
        ) {
            Text(text = message.text, fontSize = 16.sp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = timeFormatter.format(Date(message.timestamp)),
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.width(4.dp))
                if (message.isMine) {
                    Icon(
                        imageVector = when (message.status) {
                            MessageStatus.SENT -> Icons.Default.Check
                            MessageStatus.DELIVERED -> Icons.Default.DoneAll
                            MessageStatus.SEEN -> Icons.Default.DoneAll
                        },
                        contentDescription = null,
                        tint = when (message.status) {
                            MessageStatus.SEEN -> Color(0xFF4FC3F7)
                            else -> Color.Gray
                        },
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatDetailScreenPreview(){
    ChatDetailScreen(Modifier)
}