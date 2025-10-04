package com.smgamer.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.smgamer.ui.theme.CommonFontSizeDefault
import com.smgamer.ui.theme.CommonFontSizeMicro
import com.smgamer.ui.theme.CommonFontSizeMin
import com.smgamer.ui.theme.CommonPaddingDefault
import com.smgamer.ui.theme.CommonPaddingLarge_lm
import com.smgamer.ui.theme.CommonPaddingMicroMin
import com.smgamer.ui.theme.CommonPaddingMiddle
import com.smgamer.ui.theme.CommonPaddingMinDefault
import com.smgamer.ui.theme.CommonPaddingTwo
import com.smgamer.ui.theme.Online

@Composable
fun ChatsCardItem(
    name: String,
    lastMessage: String,
    unreadCount: Int,
    profileImageRes: Int,
    isOnline: Boolean,
    navToChatDetail: () -> Unit
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navToChatDetail() }
            .padding(horizontal = CommonPaddingDefault, vertical = CommonPaddingMinDefault),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(CommonPaddingLarge_lm)) {
//            AsyncImage(
//                model = ImageRequest.Builder(context)
//                    .data(profileImageRes)
//                    .crossfade(true)
//                    .build(),
//                contentDescription = name,
//                modifier = Modifier
//                    .size(CommonPaddingLarge_lm)
//                    .clip(CircleShape)
//                    .background(MaterialTheme.colorScheme.surfaceVariant),
//                contentScale = ContentScale.Crop
//            )
            Image(
                painter = painterResource(id = profileImageRes),
                contentDescription = name,
                modifier = Modifier
                    .size(CommonPaddingLarge_lm)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(CommonPaddingDefault)
                    .clip(CircleShape)
                    .background(
                        if (isOnline) Online
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    .border(
                        width = CommonPaddingTwo,
                        color = MaterialTheme.colorScheme.background,
                        shape = CircleShape
                    )
            )
        }

        Spacer(modifier = Modifier.width(CommonPaddingDefault))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = name,
                fontWeight = FontWeight.Bold,
                fontSize = CommonFontSizeDefault,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(CommonPaddingTwo))
            Text(
                text = lastMessage,
                fontSize = CommonFontSizeMin,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (unreadCount > 0) {
            Box(
                modifier = Modifier
                    .defaultMinSize(minWidth = CommonPaddingMiddle, minHeight = CommonPaddingMiddle)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
                    .padding(horizontal = CommonPaddingMicroMin, vertical = CommonPaddingTwo),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (unreadCount > 99) "99+" else unreadCount.toString(),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = CommonFontSizeMicro,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}