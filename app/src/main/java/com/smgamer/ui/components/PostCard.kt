package com.smgamer.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.smgamer.R
import com.smgamer.ui.screens.home.Post
import com.smgamer.ui.theme.CommonPaddingDefault
import com.smgamer.ui.theme.CommonPaddingLarge
import com.smgamer.ui.theme.CommonPaddingMicro
import com.smgamer.ui.theme.CommonPaddingMin
import com.smgamer.ui.theme.CommonPaddingMinDefault
import com.smgamer.ui.theme.CommonPaddingNone
import com.smgamer.ui.theme.PostImageHeight
import com.smgamer.ui.theme.scaledPadding

@Composable
fun PostCard(
    post: Post,
    modifier: Modifier = Modifier,
    navToPostDetail: () -> Unit,
    cardPadding: PaddingValues = PaddingValues(CommonPaddingNone),
    likeSize : Dp = scaledPadding(CommonPaddingLarge)
){
    val context = LocalContext.current
    var isLiked by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .padding(cardPadding)
            .fillMaxWidth(),
        onClick = { navToPostDetail() },
        shape = RoundedCornerShape(scaledPadding(CommonPaddingDefault)),
        elevation = CardDefaults.cardElevation(defaultElevation = scaledPadding(CommonPaddingMicro)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(post.image)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(PostImageHeight)
                    .clip(
                        RoundedCornerShape(
                            topStart = scaledPadding(CommonPaddingDefault),
                            topEnd = scaledPadding(CommonPaddingDefault)
                        )
                    ),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(scaledPadding(CommonPaddingDefault))) {
                Text(
                    text = post.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = stringResource(R.string.by_user, post.user),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(scaledPadding(CommonPaddingMin)))

                Text(
                    text = post.lastComment,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    maxLines = 2
                )

                Spacer(Modifier.height(scaledPadding(CommonPaddingMinDefault)))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { isLiked = !isLiked },
                        modifier = Modifier.size(likeSize)
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (isLiked) R.drawable.icon_like_blue
                                else R.drawable.icon_like_grey
                            ),
                            contentDescription = null,
                            tint = if (isLiked) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = stringResource(R.string.likes_txt, post.likes + if (isLiked) 1 else 0),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}