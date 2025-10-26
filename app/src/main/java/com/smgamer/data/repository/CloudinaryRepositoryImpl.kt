package com.smgamer.data.repository

import android.content.Context
import android.net.Uri
import com.smgamer.data.datastore.remote.cloudinary.CloudinaryService
import com.smgamer.domain.repository.CloudinaryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CloudinaryRepositoryImpl @Inject constructor(
    private val cloudinaryService: CloudinaryService
): CloudinaryRepository{

    override suspend fun uploadImage(context: Context, uri: Uri, folder:String): String? = withContext(Dispatchers.IO) {
        return@withContext cloudinaryService.uploadImageToCloudinaryBlocking(context, uri, folder)
    }

    override suspend fun deleteImage(imageUrl: String) = withContext(Dispatchers.IO) {
        cloudinaryService.deleteImageFromCloudinary(imageUrl)
    }

}