package com.smgamer.ui.screens.newpost

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.smgamer.R
import com.smgamer.ui.components.ChooseImageDialog
import com.smgamer.ui.theme.AddPostSize
import com.smgamer.ui.theme.BottomBarPadding
import com.smgamer.ui.theme.CommonFontSizeMicro
import com.smgamer.ui.theme.CommonFontSizeMiddle
import com.smgamer.ui.theme.CommonPaddingDefault
import com.smgamer.ui.theme.CommonPaddingMicro
import com.smgamer.ui.theme.CommonPaddingMin
import com.smgamer.ui.theme.CommonPaddingMinDefault
import com.smgamer.ui.theme.DescriptionTextFieldHeight
import com.smgamer.ui.theme.GameBottomPadding
import com.smgamer.ui.theme.scaledFont
import com.smgamer.ui.theme.scaledPadding
import com.smgamer.ui.viewmodels.PostsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPostScreen(
    modifier: Modifier,
    postsViewModel: PostsViewModel,
    navBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

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
    var selectedImagesUri by remember { mutableStateOf<List<Uri>>(emptyList()) } // mis imagenes antes de cargarlas
    var uploadedImagesUrl by remember { mutableStateOf<List<String>>(emptyList()) } // imagenes de cloudinary

    var showDialog by remember { mutableStateOf(false) }
    // una imagen
    //var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    //var uploadedImageUrl by remember { mutableStateOf<String?>(null) }
    // muchas imagenes


    var imageToDelete by remember { mutableStateOf<Uri?>(null) }



    // varias fotos
    //var photoUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    var photoFile by remember { mutableStateOf<File?>(null) }

    // Lanzador para galería de 1 imagen
//    val galleryLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri: Uri? ->
//        uri?.let {
//            selectedImageUri = it
//        }
//    }

    // Lanzador para seleccionar varias imágenes
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->

        val currentCount = selectedImagesUri.size
        val incomingCount = uris.size
        val maxLimit = 3

        if (currentCount >= maxLimit) {
            Toast.makeText(context, "Ya alcanzaste el límite de $maxLimit imágenes",
                Toast.LENGTH_SHORT).show()
            return@rememberLauncherForActivityResult
        }

        val availableSlots = maxLimit - currentCount
        val toAdd = if (incomingCount > availableSlots) uris.take(availableSlots) else uris

        selectedImagesUri = selectedImagesUri + toAdd

        if (incomingCount > availableSlots) {
            Toast.makeText(context,
                "Solo se agregaron $availableSlots imágen/es más. Límite $maxLimit alcanzado.",
                Toast.LENGTH_SHORT).show()
        }
    }


    // Función para subir imágenes (puede ser la que ya tenés)
    fun uploadImages() {
        uploadedImagesUrl = emptyList() // limpiar URLs anteriores
        selectedImagesUri.forEach { uri ->
            uploadImageToCloudinary(
                scope = scope,
                context = context,
                imageUri = uri,
                uploadPreset = "sm_gamer",
                cloudName = "ddbqwxz5l"
            ) { url ->
                url?.let {
                    uploadedImagesUrl = uploadedImagesUrl + it
                }
            }
        }
    }



    // Lanzador para cámara para 1 foto
//    val cameraLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.TakePicture()
//    ) { success ->
//        if (success) {
//            selectedImageUri = photoFile?.let {
//                FileProvider.getUriForFile(
//                    context,
//                    "${context.packageName}.fileprovider",
//                    it
//                )
//            }
//        }

    // Lanzador para cámara para multiples fotos
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            if (selectedImagesUri.size >= 3) {
                Toast.makeText(context, "Ya alcanzaste el límite de 3 imágenes",
                    Toast.LENGTH_SHORT).show()
                return@rememberLauncherForActivityResult
            }

            photoFile?.let { file ->
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                selectedImagesUri = selectedImagesUri + uri
            }
        }
    }

    // Lanzador para pedir permiso de cámara
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            photoFile = createImageFile(context)
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                photoFile!!
            )
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    val focusManager = LocalFocusManager.current
    val density = LocalDensity.current
    val imeVisible = WindowInsets.ime.getBottom(density) > 0
    var hasFocus by remember { mutableStateOf(false) }

    LaunchedEffect(imeVisible) {
        if (!imeVisible) focusManager.clearFocus()
    }


    //al apretar en add, mostrar panatalla de carga, bloquear botones y mostrar
    // mensaje de exito o error y navegar hacia atras automaticamente
    // no hace falta mostrar las imagenes cargadas despues de subirlas




    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.new_post), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                            //hideKeyboardAndClearFocus()
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

            // Mostrar imágenes subidas (desde URLs Cloudinary)
            LazyRow {
                items(uploadedImagesUrl) { url ->
                    Image(
                        painter = rememberAsyncImagePainter(url),
                        contentDescription = null,
                        modifier = Modifier
                            .size(scaledPadding(GameBottomPadding))
                            .padding(scaledPadding(CommonPaddingMicro)),
                        contentScale = ContentScale.Crop
                    )
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
            onGalleryClick = {
                showDialog = false
                galleryLauncher.launch("image/*")
            },
            onCameraClick = {
                showDialog = false
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
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
}



//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MyTextField(
//    value: String,
//    hasFocus: (Boolean) -> Unit,
//    onValueChange: (String) -> Unit,
//    onFocusManage: () -> Unit,
//){
//    OutlinedTextField(
//        modifier = Modifier
//            //.weight(1f)
//            .onFocusChanged { focusState -> hasFocus(focusState.isFocused) },
//        value = value,
//        onValueChange = { onValueChange(it) },
//        placeholder = {
//            Text(
//                "poner texto",
//                //stringResource(R.string.txt_search),
//                color = MaterialTheme.colorScheme.onSurfaceVariant
//            )
//        },
//        leadingIcon = {
//            IconButton(onClick = { onFocusManage() }
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Search,
//                    contentDescription = null,
//                    tint = MaterialTheme.colorScheme.onSurface
//                )
//            }
//        },
////        trailingIcon = {
////            if (focused || input.isNotEmpty()) {
////                IconButton(onClick = {
////                    clearInput()
////                    onFocusManage()
////                }) {
////                    Icon(
////                        imageVector = Icons.Default.Clear,
////                        contentDescription = null,
////                        tint = MaterialTheme.colorScheme.onSurface
////                    )
////                }
////            }
////        },
//        singleLine = true,
//        textStyle = TextStyle(
//            fontSize = CommonFontSizeDefault,
//            color = MaterialTheme.colorScheme.onSurface
//        ),
//        shape = RoundedCornerShape(CommonPaddingMin),
//        colors = TextFieldDefaults.outlinedTextFieldColors(
//            containerColor = MaterialTheme.colorScheme.surfaceVariant,
//            focusedBorderColor = MaterialTheme.colorScheme.primary,
//            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
//            cursorColor = MaterialTheme.colorScheme.primary
//        )
//
//    )
//}



//@Composable
//fun keyboardAsState(): State<Boolean> {
//    val keyboardState = remember { mutableStateOf(false) }
//    val view = LocalView.current
//
//    DisposableEffect(view) {
//        val listener = ViewTreeObserver.OnGlobalLayoutListener {
//            val heightDiff = view.rootView.height - view.height
//            keyboardState.value = heightDiff > 200 // si hay más de 200dp de diferencia, está abierto
//        }
//        view.viewTreeObserver.addOnGlobalLayoutListener(listener)
//        onDispose {
//            view.viewTreeObserver.removeOnGlobalLayoutListener(listener)
//        }
//    }
//
//    return keyboardState
//}


//// Función para convertir Uri a File (igual que antes)
//fun uriToFile(uri: Uri, context: Context): File {
//    val inputStream = context.contentResolver.openInputStream(uri)
//    val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)
//    inputStream.use { input ->
//        tempFile.outputStream().use { output ->
//            input?.copyTo(output)
//        }
//    }
//    return tempFile
//}

// Helper para lanzar en Main Thread desde un coroutine IO
fun CoroutineScope.launchedInMain(block: suspend () -> Unit) {
    this.launch(Dispatchers.Main) { block() }
}

// Crear archivo temporal para la foto
fun createImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
}


fun uploadImageToCloudinary(
    scope: CoroutineScope,
    context: Context,
    imageUri: Uri,
    uploadPreset: String,
    cloudName: String,
    onResult: (String?) -> Unit
) {
    scope.launch(Dispatchers.IO) {
        try {
            val rotatedBitmap = rotateImageIfRequired(context, imageUri)
            val compressedFile = compressBitmapToFile(context, rotatedBitmap)

            val client = OkHttpClient()
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file",
                    compressedFile.name,
                    compressedFile.asRequestBody("image/webp".toMediaTypeOrNull())
                )
                .addFormDataPart("upload_preset", uploadPreset)
                .build()

            val request = Request.Builder()
                .url("https://api.cloudinary.com/v1_1/$cloudName/image/upload")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = JSONObject(response.body?.string() ?: "")
                val url = json.getString("secure_url")
                withContext(Dispatchers.Main) {
                    onResult(url)
                }
            } else {
                withContext(Dispatchers.Main) {
                    onResult(null)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                onResult(null)
            }
        }
    }
}


fun rotateImageIfRequired(context: Context, imageUri: Uri): Bitmap {
    val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
    val bitmap = BitmapFactory.decodeStream(inputStream)
    inputStream?.close()

    val exifStream = context.contentResolver.openInputStream(imageUri)
    val exif = ExifInterface(exifStream!!)
    exifStream.close()

    val orientation = exif.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL
    )

    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
        else -> bitmap
    }
}

fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(degrees)
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

fun compressBitmapToFile(context: Context, bitmap: Bitmap): File {
    val compressedFile = File(context.cacheDir, "compressed_${System.currentTimeMillis()}.webp")
    FileOutputStream(compressedFile).use { out ->
        bitmap.compress(Bitmap.CompressFormat.WEBP, 80, out)  // Calidad 80%
    }
    return compressedFile
}