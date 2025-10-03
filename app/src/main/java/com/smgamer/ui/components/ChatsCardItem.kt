package com.smgamer.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.smgamer.R


@Composable
fun ChatsCardItem(
    name: String,
    lastMessage: String,
    unreadCount: Int,
    profileImageRes: Int,
    modifier: Modifier = Modifier,
    navToChatDetail:()->Unit
) {
    Row(
        modifier = modifier
            .clickable{
                navToChatDetail()
            }
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Imagen de perfil circular
        val context = LocalContext.current
        val image = ContextCompat.getDrawable(context, profileImageRes)
//        image?.let {
//            Image(
//                bitmap = it.toBitmap().asImageBitmap(),
//                contentDescription = "Profile picture of $name",
//                modifier = Modifier
//                    .size(56.dp)
//                    .clip(CircleShape).background(color = Color.Gray)
//            )
//        }

        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(image)
                .crossfade(true)
                .build(),
            contentDescription = name,
            modifier = Modifier
                .size(80.dp)
                .padding(8.dp)
                .background(Color.Gray, shape = CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Nombre y último mensaje
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = name,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = lastMessage,
                fontSize = 14.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Círculo con cantidad de mensajes no leídos
        if (unreadCount > 0) {
            Box(
                modifier = Modifier
                    .defaultMinSize(24.dp,24.dp) // mínimo tamaño circular
                    .padding(start = 8.dp)
                    .background(Color(0xFFFF9800), shape = CircleShape)
                    .padding(horizontal = 8.dp, vertical = 4.dp), // espaciado interno,
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (unreadCount > 99) "99+" else unreadCount.toString(),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ChatsCardItemPreview(){
    ChatsCardItem(name = "Juan", lastMessage = "hola", unreadCount = 1000,
        profileImageRes = R.drawable.ic_person, navToChatDetail = {})
}