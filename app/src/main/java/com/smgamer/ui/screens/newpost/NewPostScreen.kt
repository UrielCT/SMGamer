package com.smgamer.ui.screens.newpost

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.smgamer.R
import com.smgamer.ui.components.ChooseImageDialog
import com.smgamer.ui.theme.AddPostSize
import com.smgamer.ui.theme.BottomBarPadding
import com.smgamer.ui.theme.CommonFontSizeMicro
import com.smgamer.ui.theme.CommonFontSizeMiddle
import com.smgamer.ui.theme.CommonPaddingDefault
import com.smgamer.ui.theme.CommonPaddingMin
import com.smgamer.ui.theme.CommonPaddingMinDefault
import com.smgamer.ui.theme.DescriptionTextFieldHeight
import com.smgamer.ui.theme.GameBottomPadding
import com.smgamer.ui.theme.scaledFont
import com.smgamer.ui.theme.scaledPadding
import com.smgamer.ui.utils.rememberCameraHandler
import com.smgamer.ui.utils.rememberGalleryHandler

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPostScreen(
    modifier: Modifier,
    newPostViewModel: NewPostViewModel= hiltViewModel(),
    navBack: () -> Unit
) {
    val context = LocalContext.current
    val uiState by newPostViewModel.uiState.collectAsState()


    val categories = listOf(
        "PC" to R.drawable.icon_pc,
        "PlayStation" to R.drawable.icon_ps4,
        "Xbox" to R.drawable.icon_xbox,
        "Nintendo" to R.drawable.icon_nintendo,
        //"Móvil" to R.drawable.ic_mobile
    )

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var selectedImagesUri by remember { mutableStateOf<List<Uri>>(emptyList()) }

    var showDialog by remember { mutableStateOf(false) }
    var imageToDelete by remember { mutableStateOf<Uri?>(null) }

    val focusManager = LocalFocusManager.current
    val density = LocalDensity.current
    val imeVisible = WindowInsets.ime.getBottom(density) > 0
    var hasFocus by remember { mutableStateOf(false) }


    LaunchedEffect(imeVisible) {
        if (!imeVisible) focusManager.clearFocus()
    }

// ✅ Handlers reutilizables
    val openCamera = rememberCameraHandler(context) { uri ->
        if (selectedImagesUri.size < 3) {
            selectedImagesUri = selectedImagesUri + uri
        } else {
            Toast.makeText(context, "Límite de 3 imágenes alcanzado", Toast.LENGTH_SHORT).show()
        }
    }

    val openGallery = rememberGalleryHandler(multiple = true) { uris ->
        val remaining = 3 - selectedImagesUri.size
        val toAdd = uris.take(remaining)
        selectedImagesUri = selectedImagesUri + toAdd
        if (uris.size > remaining)
            Toast.makeText(context, "Solo se agregaron $remaining imágenes (máx 3)", Toast.LENGTH_SHORT).show()
    }

    // Función para subir imágenes (puede ser la que ya tenés)
    fun uploadImages() {
        if (title.isNotBlank() && description.isNotBlank() && category.isNotBlank()) {
            newPostViewModel.createPost(
                context = context,
                title = title,
                description = description,
                category = category,
                images = selectedImagesUri,
                onSuccess = {
                    Toast.makeText(context, "Post creado correctamente", Toast.LENGTH_SHORT).show()
                    navBack()
                },
                onError = {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.new_post), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                            navBack()
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(
                        modifier  = Modifier
                            .padding(end = scaledPadding(CommonPaddingMinDefault))
                            .size(AddPostSize)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            ,
                        onClick = {
                            uploadImages()
                            focusManager.clearFocus()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(scaledPadding(CommonPaddingDefault))
        ) {

            // 📸 Zona de imágenes
            Text(
                text = "Imágenes (${selectedImagesUri.size}/3)",
                fontWeight = FontWeight.Medium,
                fontSize = scaledFont(CommonFontSizeMiddle),
                modifier = Modifier.padding(scaledPadding(CommonPaddingDefault))
            )

            LazyRow(
                modifier = Modifier.padding(horizontal = scaledPadding(CommonPaddingDefault)),
                horizontalArrangement = Arrangement.spacedBy(scaledPadding(CommonPaddingMin))
            ) {
                items(selectedImagesUri) { uri ->
                    Box(
                        modifier = Modifier
                            .size(scaledPadding(GameBottomPadding))
                            .clip(RoundedCornerShape(scaledPadding(CommonPaddingMinDefault)))
                            .clickable { imageToDelete = uri }
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(uri),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                item {
                    Box(
                        modifier = Modifier
                            .size(scaledPadding(GameBottomPadding)) //100.dp
                            .clip(RoundedCornerShape(scaledPadding(CommonPaddingMinDefault)))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                            .clickable { showDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // 📝 Campos de texto
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.post_title_txt)) },
                singleLine = true,
                modifier = Modifier
                    .padding(horizontal = scaledPadding(CommonPaddingDefault))
                    .fillMaxWidth()
                    .onFocusChanged { focusState -> hasFocus = focusState.isFocused },
                shape = RoundedCornerShape(scaledPadding(CommonPaddingMinDefault)),
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(R.string.post_description_tct)) },
                modifier = Modifier
                    .padding(horizontal = scaledPadding(CommonPaddingDefault))
                    .fillMaxWidth()
                    .height(DescriptionTextFieldHeight)
                    .onFocusChanged { focusState -> hasFocus = focusState.isFocused },
                shape = RoundedCornerShape(scaledPadding(CommonPaddingMinDefault)),
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = scaledPadding(CommonPaddingMin)),
                horizontalArrangement = Arrangement.spacedBy(scaledPadding(CommonPaddingMinDefault))
            ) {
                items(categories) { (name, imageRes) ->
                    Column(
                        modifier = Modifier
                            .padding(horizontal = scaledPadding(CommonPaddingMin))
                            .clickable { category = name },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = name,
                            modifier = Modifier
                                .size(scaledPadding(BottomBarPadding))
                                .clip(CircleShape)
                                .background(
                                    if (category == name)
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant
                                )
                                .padding(scaledPadding(CommonPaddingMin)),
                            contentScale = ContentScale.Crop
                        )

                        Text(
                            text = name,
                            fontSize = scaledFont(CommonFontSizeMicro),
                            fontWeight = if (category == name) FontWeight.Bold else FontWeight.Normal,
                            color = if (category == name)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

    }


    // 💬 Diálogo para elegir fuente
    if (showDialog) {
        ChooseImageDialog(
            onDismissRequest = { showDialog = false },
            onGalleryClick = { showDialog = false; openGallery() },
            onCameraClick = { showDialog = false; openCamera() }
        )
    }

    // 🗑 Confirmar eliminación
    if (imageToDelete != null) {
        AlertDialog(
            onDismissRequest = { imageToDelete = null },
            title = { Text(stringResource(R.string.delete_image)) },
            text = { Text(stringResource(R.string.wantto_delete_imagen)) },
            confirmButton = {
                TextButton(onClick = {
                    selectedImagesUri = selectedImagesUri.filter { it != imageToDelete }
                    imageToDelete = null
                }) { Text(stringResource(R.string.delete)) }
            },
            dismissButton = {
                TextButton(onClick = { imageToDelete = null }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }

    if (uiState.isLoading) {
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}