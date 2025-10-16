package com.smgamer.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.smgamer.R
import com.smgamer.ui.components.ProfilePostCard
import com.smgamer.ui.theme.CommonFontSizeDefault
import com.smgamer.ui.theme.CommonFontSizeLarge
import com.smgamer.ui.theme.CommonFontSizeMicro
import com.smgamer.ui.theme.CommonFontSizeMiddle
import com.smgamer.ui.theme.CommonFontSizeMin
import com.smgamer.ui.theme.CommonPaddingDefault
import com.smgamer.ui.theme.CommonPaddingMin
import com.smgamer.ui.theme.CommonPaddingTwo
import com.smgamer.ui.theme.DividerThickness
import com.smgamer.ui.theme.scaledFont
import com.smgamer.ui.theme.scaledPadding

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    navToEditProfile: () -> Unit
) {
    val context = LocalContext.current

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val screenWidth = maxWidth
        val coverHeight = screenWidth * 0.55f

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = scaledPadding(CommonPaddingDefault))
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(coverHeight)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(R.drawable.cover_image)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Edit Icon
                    IconButton(
                        onClick = { navToEditProfile() },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(scaledPadding(CommonPaddingDefault))
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-screenWidth * 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(R.drawable.ic_person)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier
                            .size(screenWidth * 0.32f)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .border(
                                width = scaledPadding(CommonPaddingTwo),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = CircleShape
                            ),
                        contentScale = ContentScale.Crop
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-screenWidth * 0.08f))
                ) {
                    Text(
                        text = "Username",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = scaledFont(CommonFontSizeLarge),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "uriel@gmail.com",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = scaledFont(CommonFontSizeMin)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = scaledPadding(CommonPaddingDefault)),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    //ProfileStat("1", stringResource(R.string.posts))
                    //ProfileStat("12874387", stringResource(R.string.user_phone))
                }

                HorizontalDivider(
                    modifier = Modifier.padding( scaledPadding(CommonPaddingDefault)),
                    thickness = DividerThickness,
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                Text(
                    text = stringResource(R.string.posts_title),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = scaledFont(CommonFontSizeDefault),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(
                        start = scaledPadding(CommonPaddingDefault),
                        bottom = scaledPadding(CommonPaddingMin)
                    )
                )
            }

            items(6) { index ->
//                ProfilePostCard(
//                    name = "Publicación $index",
//                    lastMessage = "Contenido o descripción breve de la publicación número $index.",
//                    profileImageRes = R.drawable.ic_person
//                )
            }
        }
    }
}

