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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import coil.compose.rememberAsyncImagePainter
import com.smgamer.R
import com.smgamer.ui.theme.Background
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

@Composable
fun NewPostScreen(
    modifier: Modifier,
    postsViewModel: PostsViewModel,
    navBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    //val title by remember { mutableStateOf<String>("") }
    //val description by remember { mutableStateOf<String>("") }
    //val category by remember { mutableStateOf<String>("") }
    var selectedImagesUri by remember { mutableStateOf<List<Uri>>(emptyList()) } // mis imagenes antes de cargarlas
    var uploadedImagesUrl by remember { mutableStateOf<List<String>>(emptyList()) } // imagenes de cloudinary


    val camImage = ContextCompat.getDrawable(context, R.drawable.ic_person)

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
//    val galleryLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetMultipleContents()
//    ) { uris: List<Uri> ->
//        selectedImagesUri = selectedImagesUri + uris
//    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->

        val currentCount = selectedImagesUri.size
        val incomingCount = uris.size
        val maxLimit = 5

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
//    val cameraLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.TakePicture()
//    ) { success ->
//        if (success) {
//            photoFile?.let { file ->
//                val uri = FileProvider.getUriForFile(
//                    context,
//                    "${context.packageName}.fileprovider",
//                    file
//                )
//                selectedImagesUri = selectedImagesUri + uri
//                //photoUris = photoUris + uri
//            }
//        }
//    }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            if (selectedImagesUri.size >= 5) {
                Toast.makeText(context, "Ya alcanzaste el límite de 5 imágenes",
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


    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
    ) {
        val (coverImg, btnEdit, editForm,tit,rowImages,categories) = createRefs()

        var title by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        val category by remember { mutableStateOf<String>("") }


        Box(modifier = Modifier
            .background(color = Color.Red)
            .fillMaxWidth()
            .height(220.dp)
            .constrainAs(coverImg) {
                top.linkTo(parent.top)
            })


        Text("Create new post -> ${selectedImagesUri.size} / 5",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.constrainAs(tit){
                top.linkTo(parent.top, margin = 10.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        if (imageToDelete != null) {
            AlertDialog(
                onDismissRequest = { imageToDelete = null },
                title = { Text("Eliminar imagen") },
                text = { Text("¿Deseas eliminar esta imagen?") },
                confirmButton = {
                    TextButton(onClick = {
                        selectedImagesUri = selectedImagesUri.filter { it != imageToDelete }
                        imageToDelete = null
                    }) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { imageToDelete = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .constrainAs(rowImages) {
                    top.linkTo(tit.bottom)
                    bottom.linkTo(coverImg.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            Button(onClick = {
                if (selectedImagesUri.size != 5) {
                    showDialog = true
                } else {
                    Toast.makeText(context, "Ya alcanzaste el límite de 5 imágenes",
                        Toast.LENGTH_SHORT).show()
                }
            }
            ) {
                Text("Seleccionar imagen")
            }

            Spacer(Modifier.height(16.dp))

            // una sola imagen
//            uploadedImageUrl?.let { url ->
//                Image(
//                    painter = rememberAsyncImagePainter(url),
//                    contentDescription = "Imagen subida",
//                    modifier = Modifier
//                        //.fillMaxWidth()
//                        .size(50.dp)
//                )
//            }

            Spacer(Modifier.height(16.dp))


            // Mostrar imágenes seleccionadas (preview local)
            LazyRow {
                items(selectedImagesUri) { uri ->
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .padding(4.dp)
                            .clickable {
                                imageToDelete = uri
                            },
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { uploadImages() },
                enabled = selectedImagesUri.isNotEmpty()
            ) {
                Text("Subir imágenes")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Mostrar imágenes subidas (desde URLs Cloudinary)
            LazyRow {
                items(uploadedImagesUrl) { url ->
                    Image(
                        painter = rememberAsyncImagePainter(url),
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .padding(4.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // una sola imagen

//            selectedImageUri?.let { uri ->
//                Image(
//                    painter = rememberAsyncImagePainter(uri),
//                    contentDescription = "Imagen seleccionada",
//                    modifier = Modifier.size(100.dp),
//                    contentScale = ContentScale.Crop
//                )
//
//                Button(onClick = {
//                    selectedImageUri?.let { uri ->
//                        uploadImageToCloudinary(
//                            context = context,
//                            imageUri = uri,
//                            uploadPreset = "sm_gamer",
//                            scope = scope,
//                            cloudName = "ddbqwxz5l"
//                        ) { url ->
//                            if (url != null) {
//                                uploadedImageUrl = url
//                                // Aquí puedes guardarla en tu base de datos
//                            } else {
//                                Toast.makeText(context, "Error al subir", Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                    }
//                }) {
//                    Text("Subir a Cloudinary")
//                }
//
//
//            }


//            selectedImageUri?.let { uri ->
//                Text("Imagen seleccionada: $uri")
//                Button(onClick = {
//                    // Subir a Cloudinary
//                    scope.launch(Dispatchers.IO) {
//                        try {
//                            val file = uriToFile(uri, context)
//                            val result = cloudinary.uploader().upload(file, ObjectUtils.emptyMap())
//                            val url = result["secure_url"] as String
//                            launchedInMain {
//                                uploadedImageUrl = url
//                            }
//                        } catch (e: Exception) {
//                            launchedInMain {
//                                // Manejar error aquí si querés
//                            }
//                        }
//                    }
//                }) {
//                    Text("Guardar en Cloudinary")
//                }
//            }
        }

//        Row(
//            modifier = Modifier
//            .constrainAs(rowImages){
//                top.linkTo(title.bottom)
//                bottom.linkTo(coverImg.bottom)
//                start.linkTo(parent.start)
//                end.linkTo(parent.end)
//            },
//            horizontalArrangement = Arrangement.spacedBy(24.dp)
//        ) {
//
//            camImage?.let {
//                Image(
//                    bitmap = camImage.toBitmap().asImageBitmap(),
//                    contentDescription = null,
//                    modifier = Modifier
//                        .clickable{
//                            showDialog = true
//                        }
//                        .background(color = Color.Gray)
//                        .size(100.dp),
//                    contentScale = ContentScale.Crop
//                )
//            }
//
//            uploadedImageUrl?.let { url->
//                Image(
//                    painter = rememberAsyncImagePainter(url),
//                    contentDescription = null,
//                    modifier = Modifier
//                        .background(color = Color.Gray)
//                        .size(100.dp),
//                    contentScale = ContentScale.Crop
//                )
//            }
//
//        }






        IconButton(onClick = { navBack() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Cerrar"
            )
        }


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .constrainAs(editForm) {
                    top.linkTo(coverImg.bottom)
                },
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Nombre") },
                placeholder = { Text("Juan Pérez") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )


            // description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripcion") },
                placeholder = { Text("Descripcion") },
                singleLine = true,
                isError = description.isNotEmpty() ,
                modifier = Modifier.fillMaxWidth()
            )

            // Opcional: mensaje de error
//            if (description.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(description).matches()) {
//                Text(
//                    text = "Correo electrónico no válido",
//                    color = MaterialTheme.colorScheme.error,
//                    style = MaterialTheme.typography.bodySmall
//                )
//            }
        }



        Row(
            modifier = Modifier
                .constrainAs(categories) {
                    bottom.linkTo(btnEdit.top)
                    top.linkTo(editForm.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            camImage?.let {
                Image(
                    bitmap = camImage.toBitmap().asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .background(color = Color.Gray)
                        .size(60.dp),
                    contentScale = ContentScale.Crop
                )
            }

            camImage?.let {
                Image(
                    bitmap = camImage.toBitmap().asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .background(color = Color.Gray)
                        .size(60.dp),
                    contentScale = ContentScale.Crop
                )
            }

            camImage?.let {
                Image(
                    bitmap = camImage.toBitmap().asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .background(color = Color.Gray)
                        .size(60.dp),
                    contentScale = ContentScale.Crop
                )
            }

            camImage?.let {
                Image(
                    bitmap = camImage.toBitmap().asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .background(color = Color.Gray)
                        .size(60.dp),
                    contentScale = ContentScale.Crop
                )
            }

        }


        Button(onClick = {},
            modifier= Modifier
                .constrainAs(btnEdit){
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            Text("PUBLICAR", fontSize = 22.sp, modifier = Modifier
                .padding(horizontal = dimensionResource(R.dimen.common_padding_default)))
        }
    }


    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Seleccionar opción") },
            text = {
                Column {
                    Text(
                        "Elegir de galería",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showDialog = false
                                galleryLauncher.launch("image/*")
                            }
                            .padding(8.dp)
                    )
                    Text(
                        "Sacar foto",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showDialog = false
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                //photoFile = createImageFile(context)
                                //val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile!!)
                                //cameraLauncher.launch(uri)
                            }
                            .padding(8.dp)
                    )
                }
            },
            confirmButton = {}
        )
    }
}

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



@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun NewPostScreenPreview(){
    val postsViewModel = PostsViewModel()
    NewPostScreen(Modifier, postsViewModel = postsViewModel,{})
}
