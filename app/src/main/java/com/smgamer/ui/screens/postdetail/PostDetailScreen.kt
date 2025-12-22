package com.smgamer.ui.screens.postdetail

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import com.smgamer.R
import com.smgamer.domain.model.CommentWithUser
import com.smgamer.ui.components.AutoSlidingCarousel
import com.smgamer.ui.components.CommentDialog
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
import com.smgamer.ui.theme.scaledFont
import com.smgamer.ui.theme.scaledPadding
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PostDetailScreen(
    modifier: Modifier = Modifier,
    postDetailViewModel: PostDetailViewModel = hiltViewModel(),
    postId: String,
    navBack: () -> Unit,
    navToUserProfile: (String) -> Unit,
) {
    val context = LocalContext.current
    val uiState by postDetailViewModel.uiState.collectAsState()

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp

    // Escalas adaptativas
    val paddingScale = screenHeight / 891f

    val images = uiState.postData?.post?.images ?: emptyList()

    var showCommentDialog by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }

    LaunchedEffect(postId) {
       postDetailViewModel.loadPost(postId)
    }

    fun createComment(){
        if (commentText.isNotBlank()) {
            uiState.postData?.post?.id?.let {
                postDetailViewModel.createComment(
                    commentText, it,
                    onSuccess = {
                        Toast.makeText(context, "Comentario creado", Toast.LENGTH_SHORT).show()
                        commentText = ""
                        showCommentDialog = false
                    },
                    onError = {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }else{
            Toast.makeText(context,"Texto en blanco",Toast.LENGTH_SHORT).show()
        }
    }


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

            IconButton(
                onClick = navBack,
                modifier = Modifier
                    .constrainAs(btnBack) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    }
                    .padding(scaledPadding(CommonPaddingDefault))
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            Text(
                uiState.postData?.post?.timestamp?.let { agoTextFromTimestamp(it) } ?: "Hace un momento",
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
                "${uiState.postData?.likesCount ?: 0} likes",
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

                item {
                    val user = uiState.postData?.user
                    UserCard(
                        name = user?.username ?: "User",
                        phone = user?.phone ?: "",
                        imageRes = user?.profileImage,
                        navToUserProfile = {
                            user?.id?.let { navToUserProfile(it) }
                        }
                    )
                }

                item {
                    PostDetail(
                        category= uiState.postData?.post?.category.toString(),
                        title= uiState.postData?.post?.title.toString(),
                        description = uiState.postData?.post?.description.toString()
                    )
                }

                item {
                    Text(
                        "Comments",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = scaledFont(CommonFontSizeDefault),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(
                                start = scaledPadding(CommonPaddingDefault),
                                top = scaledPadding(CommonPaddingMinDefault)
                            )
                    )
                }

                if (uiState.commentsWithUsers.isEmpty()) {
                    item {
                        Text(
                            "Sin comentarios aún",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .padding(scaledPadding(CommonPaddingDefault))
                        )
                    }
                } else {
                    items(uiState.commentsWithUsers) { commentWithUser ->
                        CommentItemWithUser(commentWithUser = commentWithUser)
                    }
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
                onClick = { showCommentDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Chat,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

        }

        if (showCommentDialog) {
            CommentDialog(
                commentText = commentText,
                onValueChange = { commentText = it },
                onDismiss = { showCommentDialog = false },
                onConfirm = { createComment() },
                onCancel = {
                    showCommentDialog = false
                    commentText = ""
                }
            )
        }


        // Estado de carga / error (overlay simple)
        if (uiState.isLoading) {
            // Podés mostrar un ProgressIndicator bonito
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                Text(
                    text = uiState.error ?: "Error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }

}

fun agoTextFromTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < TimeUnit.MINUTES.toMillis(1) -> "hace unos segundos"
        diff < TimeUnit.HOURS.toMillis(1) -> {
            val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
            "hace $minutes minuto${if (minutes > 1) "s" else ""}"
        }
        diff < TimeUnit.DAYS.toMillis(1) -> {
            val hours = TimeUnit.MILLISECONDS.toHours(diff)
            "hace $hours hora${if (hours > 1) "s" else ""}"
        }
        diff < TimeUnit.DAYS.toMillis(2) -> "ayer"
        diff < TimeUnit.DAYS.toMillis(7) -> {
            val days = TimeUnit.MILLISECONDS.toDays(diff)
            "hace $days día${if (days > 1) "s" else ""}"
        }
        diff < TimeUnit.DAYS.toMillis(30) -> {
            val weeks = TimeUnit.MILLISECONDS.toDays(diff) / 7
            "hace $weeks semana${if (weeks > 1) "s" else ""}"
        }
        diff < TimeUnit.DAYS.toMillis(365) -> {
            val months = TimeUnit.MILLISECONDS.toDays(diff) / 30
            "hace $months mes${if (months > 1) "es" else ""}"
        }
        else -> {
            val years = TimeUnit.MILLISECONDS.toDays(diff) / 365
            "hace $years año${if (years > 1) "s" else ""}"
        }
    }
}



@Composable
fun UserCard(
    name: String,
    phone: String,
    imageRes: String?,
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
fun PostDetail(
    category: String,
    title: String,
    description: String
){
    Text(
        title,
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = scaledFont(CommonFontSizeLarge),
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .padding(horizontal = scaledPadding(CommonPaddingDefault),
                vertical = scaledPadding(CommonPaddingMin))
    )

    Text(
        category,
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
        description,
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
fun CommentItemWithUser(
    commentWithUser: CommentWithUser,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val comment = commentWithUser.comment
    val user = commentWithUser.user

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(scaledPadding(CommonPaddingDefault)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(if (user.profileImage.isBlank()) R.drawable.ic_person else user.profileImage)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .size(scaledPadding(CommonPaddingLarge_lm))
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(
                    width = scaledPadding(CommonPaddingTwo),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = CircleShape
                ),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width( scaledPadding(CommonPaddingMinDefault) ))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = user.username.ifBlank { "Usuario" }.uppercase(),
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    fontSize = scaledFont(CommonFontSizeMiddle),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = agoTextFromTimestamp(comment.timestamp),
                    fontSize = scaledFont(CommonFontSizeMin),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = comment.comment,
                fontSize = scaledFont(CommonFontSizeMin),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                modifier = Modifier.padding(top = scaledPadding(CommonPaddingMicro))
            )
        }
    }
}