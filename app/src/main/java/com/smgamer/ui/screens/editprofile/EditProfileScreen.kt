package com.smgamer.ui.screens.editprofile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.smgamer.R
import com.smgamer.ui.theme.CommonFontSizeDefault
import com.smgamer.ui.theme.CommonFontSizeLarge
import com.smgamer.ui.theme.CommonFontSizeMin
import com.smgamer.ui.theme.CommonPaddingDefault
import com.smgamer.ui.theme.CommonPaddingMin
import com.smgamer.ui.theme.CommonPaddingTwo
import com.smgamer.ui.theme.scaledFont
import com.smgamer.ui.theme.scaledPadding

@Composable
fun EditProfileScreen(
    modifier: Modifier = Modifier,
    navBack: () -> Unit,
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("Uriel") }
    var email by remember { mutableStateOf("uriel@gmail.com") }

    var coverUri by remember { mutableStateOf<Uri?>(null) }
    var profileUri by remember { mutableStateOf<Uri?>(null) }

    val coverPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        coverUri = it
    }
    val profilePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        profileUri = it
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
    ) {
        val screenWidth = maxWidth
        val coverHeight = screenWidth * 0.55f

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = scaledPadding(CommonPaddingDefault))
        ) {
            item {
                // 🖼 Imagen de portada
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(coverHeight)
                        .clickable { coverPicker.launch("image/*") }
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(coverUri ?: R.drawable.cover_image)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Imagen de portada",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // 🔙 Botón volver
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

                // 🧍 Imagen de perfil editable
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-screenWidth * 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(profileUri ?: R.drawable.ic_person)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Imagen de perfil",
                        modifier = Modifier
                            .size(screenWidth * 0.32f)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .border(
                                width = scaledPadding(CommonPaddingTwo),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = CircleShape
                            )
                            .clickable { profilePicker.launch("image/*") },
                        contentScale = ContentScale.Crop
                    )
                }

                // 🧾 Formulario
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(scaledPadding(CommonPaddingMin)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-screenWidth * 0.05f))
                        .padding(horizontal = scaledPadding(CommonPaddingDefault))
                ) {
                    Text(
                        text = stringResource(R.string.edit_profile_title),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = scaledFont(CommonFontSizeLarge),
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(stringResource(R.string.name_label)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(stringResource(R.string.email_label)) },
                        singleLine = true,
                        isError = email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        Text(
                            text = stringResource(R.string.invalid_email),
                            color = MaterialTheme.colorScheme.error,
                            fontSize = scaledFont(CommonFontSizeMin)
                        )
                    }

                    Button(
                        onClick = {  },
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .padding(top = scaledPadding(CommonPaddingDefault))
                    ) {
                        Text(
                            text = stringResource(R.string.save_changes),
                            fontSize = scaledFont(CommonFontSizeDefault)
                        )
                    }
                }
            }
        }
    }
}