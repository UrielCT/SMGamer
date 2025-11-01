package com.smgamer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.smgamer.R
import com.smgamer.ui.theme.CommonFontSizeDefault
import com.smgamer.ui.theme.CommonFontSizeMiddle
import com.smgamer.ui.theme.CommonFontSizeMin
import com.smgamer.ui.theme.CommonPaddingDefault
import com.smgamer.ui.theme.CommonPaddingLarge_lm
import com.smgamer.ui.theme.CommonPaddingMicro
import com.smgamer.ui.theme.CommonPaddingMin
import com.smgamer.ui.theme.CommonPaddingMinDefault
import com.smgamer.ui.theme.CommonPaddingTwo
import com.smgamer.ui.theme.scaledFont
import com.smgamer.ui.theme.scaledPadding

@Composable
fun ProfilePostCard(
    isMyUser: Boolean,
    name: String,
    lastMessage: String,
    profileImageRes: Any,
    modifier: Modifier = Modifier,
    onDeleteConfirm: () -> Unit = {}
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }


    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = scaledPadding(CommonPaddingDefault),
                vertical = scaledPadding(CommonPaddingMin)
            )
            .background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(scaledPadding(CommonPaddingMinDefault))
            )
            .padding(scaledPadding(CommonPaddingMinDefault)),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(profileImageRes)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .size(scaledPadding(CommonPaddingLarge_lm))
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .border(
                    width = scaledPadding(CommonPaddingTwo),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = CircleShape
                )
                .align(Alignment.Top),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(scaledPadding(CommonPaddingMinDefault)))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                fontWeight = FontWeight.Bold,
                fontSize = scaledFont(CommonFontSizeMiddle),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(scaledPadding(CommonPaddingMicro)))
            Text(
                text = lastMessage,
                fontSize = scaledFont(CommonFontSizeMin),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 2,
                style = TextStyle(
                    lineHeight = scaledFont(CommonFontSizeDefault)
                ),
                overflow = TextOverflow.Ellipsis
            )
        }

        // mostrar icono si es mi publicacion
        if(isMyUser){
            IconButton(
                onClick = { showDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.title_delete_post),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(stringResource(R.string.want_delete_post))
            },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteConfirm()
                    showDialog = false
                }) {
                    Text(stringResource(R.string.yes))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(R.string.no))
                }
            }
        )
    }
}

