@file:Suppress("NAME_SHADOWING")

package com.smgamer.ui.screens.postdetail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import com.smgamer.R
import com.smgamer.ui.components.AutoSlidingCarousel
import com.smgamer.ui.theme.CommonFontSizeDefault
import com.smgamer.ui.theme.CommonFontSizeLarge
import com.smgamer.ui.theme.CommonFontSizeMiddle
import com.smgamer.ui.theme.CommonFontSizeMin
import com.smgamer.ui.theme.CommonPaddingDefault
import com.smgamer.ui.theme.CommonPaddingLarge
import com.smgamer.ui.theme.CommonPaddingLarge_lm
import com.smgamer.ui.theme.CommonPaddingMicro
import com.smgamer.ui.theme.CommonPaddingMicroMin
import com.smgamer.ui.theme.CommonPaddingMin
import com.smgamer.ui.theme.CommonPaddingMinDefault
import com.smgamer.ui.theme.CommonPaddingOne
import com.smgamer.ui.theme.CommonPaddingTen
import com.smgamer.ui.theme.CommonPaddingTwo
import com.smgamer.ui.theme.DividerThickness
import com.smgamer.ui.theme.LocalFontScale
import com.smgamer.ui.theme.LocalPaddingScale
import com.smgamer.ui.theme.scaledFont
import com.smgamer.ui.theme.scaledPadding

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PostDetailScreen(
    modifier: Modifier = Modifier,
    navBack: () -> Unit,
    navToUserProfile: () -> Unit,
    navToChatDetail: () -> Unit
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp

    // Escalas adaptativas
    val fontScale = screenWidth / 411f
    val paddingScale = screenHeight / 891f
    val images = listOf(
        R.drawable.cover_image,
        R.drawable.cover_image,
        R.drawable.cover_image,
    )

    CompositionLocalProvider(
        LocalPaddingScale provides paddingScale,
        LocalFontScale provides fontScale
    ) {
        BoxWithConstraints(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            val screenWidth = maxWidth
            val coverHeight = screenWidth * 0.55f


            ConstraintLayout(
                modifier = Modifier.fillMaxSize()
            ) {
                val (coverImg, date, likes, fab, lazyPublic, btnBack) = createRefs()

                AutoSlidingCarousel(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(coverHeight)
                        .constrainAs(coverImg) { top.linkTo(parent.top) },
                    itemsCount = images.size
                ) { page ->
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(images[page])
                            .crossfade(true)
                            .build(),
                        contentDescription = "Imagen ${page + 1}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }


                // 🔙 Botón volver
                IconButton(
                    onClick = navBack,
                    modifier = Modifier
                        //.padding((8 * paddingScale).dp)
                        .padding(scaledPadding(CommonPaddingMin))
                        .constrainAs(btnBack) {
                            top.linkTo(parent.top /*margin = (8 * paddingScale).dp*/)
                            start.linkTo(parent.start /*margin = (8 * paddingScale).dp*/)
                        }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = MaterialTheme.colorScheme.background
                    )
                }

                Text(
                    "12hs Ago",
                    modifier = Modifier
                        .constrainAs(date) {
                            end.linkTo(parent.end, margin = (CommonPaddingDefault * paddingScale))
                            top.linkTo(parent.top, margin = (CommonPaddingMin * paddingScale))
                        },
                    fontSize = scaledFont(CommonFontSizeMin),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Text(
                    "0 likes",
                    modifier = Modifier
                        .constrainAs(likes) {
                            start.linkTo(parent.start, margin = (CommonPaddingDefault * paddingScale))
                            bottom.linkTo(coverImg.bottom, margin = (CommonPaddingMin * paddingScale))
                        },
                    fontSize = scaledFont(CommonFontSizeMin),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                LazyColumn(
                    modifier = Modifier
                        .constrainAs(lazyPublic) {
                            top.linkTo(coverImg.bottom)
                            bottom.linkTo(parent.bottom)
                            height = Dimension.fillToConstraints
                        }
                ) {
                    item { Spacer(modifier = Modifier.height(CommonPaddingLarge)) }

                    // 🧑 Tarjeta del usuario
                    item {
                        UserCard(
                            name = "Juan",
                            phone = "8928 8419",
                            imageRes = R.drawable.ic_person,
                            navToUserProfile = navToUserProfile
                        )
                    }

                    // 🕹️ Detalles del post
                    item { PostDetail() }

                    item {
                        Text(
                            "Comments",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = scaledFont(CommonFontSizeDefault),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(start = scaledPadding(CommonPaddingDefault),
                                    top = scaledPadding(CommonPaddingMinDefault))
                        )
                    }

                    // comments
                    items(6) {
                        CommentItem(
                            name = "Mariano",
                            lastMessage = "Muy buen juego",
                            profileImageRes = R.drawable.ic_person
                        )
                    }
                }

                FloatingActionButton(
                    modifier = Modifier
                        .size(CommonPaddingLarge_lm)
                        .constrainAs(fab) {
                            end.linkTo(parent.end, margin = (CommonPaddingDefault * paddingScale))
                            bottom.linkTo(coverImg.bottom)
                            top.linkTo(coverImg.bottom)
                        },
                    onClick = { navToChatDetail() },
                    containerColor = MaterialTheme.colorScheme.primary,
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Chat,
                        contentDescription = "Chat",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

            }
        }
    }
}

@Composable
fun UserCard(
    name: String,
    phone: String,
    imageRes: Int,
    navToUserProfile: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = scaledPadding(CommonPaddingDefault),
                vertical = scaledPadding(CommonPaddingMinDefault)
            ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(scaledPadding(CommonPaddingMin)),
        border = BorderStroke( scaledPadding(CommonPaddingOne), MaterialTheme.colorScheme.outline)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = scaledPadding(CommonPaddingDefault),
                vertical = scaledPadding(CommonPaddingMinDefault))
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageRes)
                    .crossfade(true)
                    .build(),
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(scaledPadding(CommonPaddingLarge_lm))
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width( scaledPadding(CommonPaddingMinDefault)) )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontWeight = FontWeight.Bold,
                    fontSize = scaledFont(CommonFontSizeDefault),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                //Spacer(modifier = Modifier.height((4 * paddingScale).dp))
                Text(
                    text = phone,
                    fontSize = scaledFont(CommonFontSizeMin),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            OutlinedButton(
                onClick = { navToUserProfile() },
                shape = RoundedCornerShape(scaledPadding(CommonPaddingMin)),
                border = BorderStroke(scaledPadding(CommonPaddingOne), MaterialTheme.colorScheme.primary),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Ver perfil", fontSize = scaledFont(CommonFontSizeMin))
            }
        }
    }
}

@Composable
fun PostDetail(){
    Text(
        "Nombre del juego",
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = scaledFont(CommonFontSizeLarge),
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .padding(horizontal = scaledPadding(CommonPaddingDefault),
                vertical = scaledPadding(CommonPaddingMin))
    )

    Text(
        "PS4",
        color = MaterialTheme.colorScheme.onPrimary,
        fontSize = scaledFont(CommonFontSizeLarge),
        modifier = Modifier
            .padding(
                horizontal = scaledPadding(CommonPaddingDefault),
                vertical = scaledPadding(CommonPaddingMicro)
            )
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(scaledPadding(CommonPaddingMicroMin))
            )
            .padding(
                horizontal = scaledPadding(CommonPaddingTen),
                vertical = scaledPadding(CommonPaddingMicro)
            )
    )

    HorizontalDivider(
        modifier = Modifier.padding( scaledPadding(CommonPaddingDefault)),
        thickness = DividerThickness,
        color = MaterialTheme.colorScheme.outlineVariant
    )

    Text(
        "Description",
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = scaledFont(CommonFontSizeDefault),
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .padding(start = scaledPadding(CommonPaddingDefault),
                top = scaledPadding(CommonPaddingMinDefault))
    )

    Text(
        text = "Lorem ipsum dolor sit amet consectetur adipiscing elit sagittis placerat tristique, malesuada blandit dictum magna viverra pretium facilisi nascetur congue, odio sapien tortor cras posuere fringilla sollicitudin mus faucibus. Libero odio aptent integer placerat interdum himenaeos nisi mauris inceptos conubia, in laoreet ac nunc sapien tortor natoque accumsan sagittis. Odio ultrices felis blandit in lobortis nullam a facilisi, commodo mattis ornare ad mollis aenean tempor, vestibulum lacus vel netus pretium auctor morbi.",
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = scaledFont(CommonFontSizeMiddle),
        modifier = Modifier
            .padding(horizontal = scaledPadding(CommonPaddingDefault),
                vertical = scaledPadding(CommonPaddingMin))
    )

    HorizontalDivider(
        modifier = Modifier.padding(scaledPadding(CommonPaddingDefault)),
        thickness = DividerThickness,
        color = MaterialTheme.colorScheme.outlineVariant
    )
}

@Composable
fun CommentItem(
    name: String,
    lastMessage: String,
    profileImageRes: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(scaledPadding(CommonPaddingDefault)),
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
                ),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width( scaledPadding(CommonPaddingMinDefault) ))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name.uppercase(),
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                fontSize = scaledFont(CommonFontSizeMiddle),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = lastMessage,
                fontSize = scaledFont(CommonFontSizeMin),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}