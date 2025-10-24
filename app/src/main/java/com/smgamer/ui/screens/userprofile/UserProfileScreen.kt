package com.smgamer.ui.screens.userprofile

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
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
fun UserProfileScreen(
    modifier: Modifier,
    isProfile: Boolean = false,
    userId: String? = null,
    navBack: () -> Unit,
    navToChatDetail: () -> Unit,
    navToEditProfile: () -> Unit,
    userProfileViewModel: UserProfileViewModel = hiltViewModel()
){
    val user by userProfileViewModel.user
    val isLoading by userProfileViewModel.isLoading
    val isMyUser by userProfileViewModel.isMyUser

    // userId = null o id del usuario
    LaunchedEffect(userId) {
        userProfileViewModel.loadUserProfile(userId)
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val screenWidth = maxWidth
        val coverHeight = screenWidth * 0.55f

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            return@BoxWithConstraints
        }

        if (user == null) {
            Text(
                text = "No hay usuario iniciado",
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            return@BoxWithConstraints
        }

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
                    // imagen del cover
                    AsyncImage(
                        model = user!!.coverImage.ifEmpty { R.drawable.cover_image },
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Back icon
                    if (!isProfile){ //antes !isMyProfile
                        IconButton(
                            onClick = navBack,
                            modifier = Modifier
                                .align(Alignment.TopStart)
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
                    }

                    if(isMyUser){
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

                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-screenWidth * 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    //imagen de usuario
                    AsyncImage(
                        model = user!!.profileImage.ifEmpty { R.drawable.ic_person },
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
                        text = user!!.username,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = scaledFont(CommonFontSizeLarge),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = user!!.email,
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
                    ProfileStat("1", stringResource(R.string.posts))
                    ProfileStat(user!!.phone, stringResource(R.string.user_phone))
                }

                HorizontalDivider(
                    modifier = Modifier.padding(scaledPadding(CommonPaddingDefault)),
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
                ProfilePostCard(
                    isMyUser = isMyUser,
                    name = "Nombre del juego",
                    lastMessage = "Hace 12 días",
                    profileImageRes = R.drawable.ic_person,
                    onDeleteConfirm = {
                        // eliminar post
                    }
                )
            }
        }

        // mostrar si no es mi usuario
        if(!isMyUser){
            FloatingActionButton(
                onClick = {
                    navToChatDetail()
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(scaledPadding(CommonPaddingDefault))
            ) {
                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null)
            }
        }

    }
}

@Composable
fun ProfileStat(number: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = number,
            fontSize = scaledFont(CommonFontSizeMiddle),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = label,
            fontSize = scaledFont(CommonFontSizeMicro),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}