package com.smgamer.ui.utils

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun rememberCameraHandler(
    context: Context,
    onPhotoCaptured: (Uri) -> Unit
): () -> Unit {
    var photoFile by remember { mutableStateOf<File?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoFile != null) {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                photoFile!!
            )
            onPhotoCaptured(uri)
        }
    }

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

    // Retorna una lambda para lanzar la cámara (pidiendo permiso si hace falta)
    return {
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }
}




@Composable
fun rememberGalleryHandler(
    multiple: Boolean = false,
    onImagesSelected: (List<Uri>) -> Unit
): () -> Unit {
    val launcher = rememberLauncherForActivityResult(
        contract = if (multiple)
            ActivityResultContracts.GetMultipleContents()
        else
            ActivityResultContracts.GetContent()
    ) { result ->
        when (result) {
            is Uri -> onImagesSelected(listOf(result))
            is List<*> -> onImagesSelected(result.filterIsInstance<Uri>())
        }
    }

    return { launcher.launch("image/*") }
}



fun createImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
}

//@Composable
//fun rememberImagePicker(
//    maxImages: Int = 3,
//    onImagesChanged: (List<Uri>) -> Unit
//): ImagePickerState {
//    val context = LocalContext.current
//    val scope = rememberCoroutineScope()
//
//    var selectedUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
//    var photoFile by remember { mutableStateOf<File?>(null) }
//    var showDialog by remember { mutableStateOf(false) }
//
//    val galleryLauncher = rememberLauncherForActivityResult(
//        ActivityResultContracts.GetMultipleContents()
//    ) { uris ->
//        val currentCount = selectedUris.size
//        val availableSlots = (maxImages - currentCount).coerceAtLeast(0)
//        val newUris = uris.take(availableSlots)
//        selectedUris = selectedUris + newUris
//        onImagesChanged(selectedUris)
//    }
//
//    val cameraLauncher = rememberLauncherForActivityResult(
//        ActivityResultContracts.TakePicture()
//    ) { success ->
//        if (success) {
//            photoFile?.let {
//                val uri = FileProvider.getUriForFile(
//                    context,
//                    "${context.packageName}.fileprovider",
//                    it
//                )
//                selectedUris = selectedUris + uri
//                onImagesChanged(selectedUris)
//            }
//        }
//    }
//
//    val cameraPermissionLauncher = rememberLauncherForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ) { granted ->
//        if (granted) {
//            photoFile = createImageFile(context)
//            val uri = FileProvider.getUriForFile(
//                context,
//                "${context.packageName}.fileprovider",
//                photoFile!!
//            )
//            cameraLauncher.launch(uri)
//        } else {
//            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    return remember {
//        ImagePickerState(
//            context = context,
//            scope = scope,
//            selectedUrisProvider = { selectedUris },
//            showDialogProvider = { showDialog },
//            setShowDialog = { showDialog = it },
//            launchGallery = { galleryLauncher.launch("image/*") },
//            launchCamera = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) }
//        )
//    }
//}
