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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.smgamer.domain.model.PostData
import com.smgamer.ui.theme.CommonPaddingDefault
import com.smgamer.ui.theme.CommonPaddingLarge
import com.smgamer.ui.theme.CommonPaddingMicro
import com.smgamer.ui.theme.CommonPaddingMin
import com.smgamer.ui.theme.CommonPaddingMinDefault
import com.smgamer.ui.theme.CommonPaddingNone
import com.smgamer.ui.theme.DividerThickness
import com.smgamer.ui.theme.PostImageHeight
import com.smgamer.ui.theme.scaledPadding

@Composable
fun PostCard(
    data: PostData,
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    modifier: Modifier = Modifier,
    navToPostDetail: (String) -> Unit,
    cardPadding: PaddingValues = PaddingValues(CommonPaddingNone),
    likeSize : Dp = scaledPadding(CommonPaddingLarge)
){
    val context = LocalContext.current
    val lastComment = data.comments.maxByOrNull { it.timestamp }?.comment ?: "Sin comentarios"

    Card(
        modifier = modifier
            .padding(cardPadding)
            .fillMaxWidth(),
        onClick = { navToPostDetail(data.post.id) },
        shape = RoundedCornerShape(scaledPadding(CommonPaddingDefault)),
        elevation = CardDefaults.cardElevation(defaultElevation = scaledPadding(CommonPaddingMicro)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(data.post.images[0])
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
                    text = data.post.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = stringResource(R.string.by_user, data.user?.username ?: "User"),  // iria el nombre del user
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(scaledPadding(CommonPaddingMin)))

                Text(
                    text = lastComment,  // poner el ultimo comentario
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    maxLines = 1
                )

                Spacer(Modifier.height(scaledPadding(CommonPaddingMinDefault)))

                HorizontalDivider(
                    thickness = DividerThickness,
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                Spacer(Modifier.height(scaledPadding(CommonPaddingMinDefault)))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { onLikeClick() },
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
                        text = stringResource(R.string.likes_txt, data.likesCount), // poner los likes
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}