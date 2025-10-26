package com.smgamer.domain.usecases.cloudinary

import android.content.Context
import android.net.Uri
import com.smgamer.domain.repository.CloudinaryRepository
import javax.inject.Inject

class UploadImageToCloudinaryUseCase @Inject constructor(
    private val cloudinaryRepository: CloudinaryRepository
) {
    suspend operator fun invoke(context: Context, uri: Uri, folder: String): String? {
        return cloudinaryRepository.uploadImage(context, uri, folder)
    }
}