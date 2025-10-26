package com.smgamer.domain.repository

import android.content.Context
import android.net.Uri

interface CloudinaryRepository {
    suspend fun uploadImage(context: Context, uri: Uri, folder:String): String?
    suspend fun deleteImage(imageUrl: String)
}