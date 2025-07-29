package com.smgamer.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.smgamer.R


@Composable
fun ProfilePostCard(
    name: String,
    lastMessage: String,
    profileImageRes: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Imagen de perfil circular
        val context = LocalContext.current
        val image = ContextCompat.getDrawable(context, profileImageRes)
        image?.let {
            Image(
                bitmap = it.toBitmap().asImageBitmap(),
                contentDescription = "Profile picture of $name",
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape).background(color = Color.Gray)
            )
        }

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

        IconButton(onClick = {}) {
            Icon(imageVector = Icons.Default.Clear, contentDescription = null)
        }


    }
}


@Preview(showBackground = true)
@Composable
fun ProfilePostCardPreview(){
    ProfilePostCard(name = "Juan", lastMessage = "hola",  profileImageRes = R.drawable.ic_person)
}