package com.example.socialmediagamer.model.providers

import android.content.Context
import android.graphics.Bitmap
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import java.io.File
import java.util.*

class ImageProvider {
    private var mStorage : StorageReference = FirebaseStorage.getInstance().reference


    // COMPRIME LA IMAGEN
    suspend fun save(context: Context, file: File) : UploadTask{
        val compressImage = Compressor.compress(context,file){
            resolution(500,500)
            quality(80)
            format(Bitmap.CompressFormat.JPEG)
        }

        val storage : StorageReference = FirebaseStorage.getInstance().reference.child("${Date()}" + ".jpg")
        mStorage = storage
        val task:UploadTask = storage.putBytes(compressImage.absoluteFile.absoluteFile.readBytes())

        return task

    }

    fun getStorage():StorageReference{
        return mStorage
    }

}