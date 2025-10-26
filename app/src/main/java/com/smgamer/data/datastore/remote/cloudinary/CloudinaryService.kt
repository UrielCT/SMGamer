package com.smgamer.data.datastore.remote.cloudinary

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.util.Log
import com.smgamer.utils.Constants
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.security.MessageDigest
import javax.inject.Inject

class CloudinaryService @Inject constructor() {

    private fun generateSignature(params: Map<String, String>, apiSecret: String): String {
        val sorted = params.toSortedMap()
        val toSign = sorted.entries.joinToString("&") { "${it.key}=${it.value}" }
        return MessageDigest.getInstance("SHA-1")
            .digest((toSign + apiSecret).toByteArray())
            .joinToString("") { "%02x".format(it) }
    }

    private fun extractPublicIdFromUrl(imageUrl: String): String? {
        return imageUrl
            .substringAfterLast("/upload/") // Quita todo antes de upload/
            .substringAfter("/")            // Quita el versionado vXXXXXX/
            .substringBeforeLast(".")       // Quita la extensión (.webp, .jpg, etc.)
            .ifEmpty { null }
    }

    /****   DELETE IMAGE   ****/
    fun deleteImageFromCloudinary(imageUrl: String) {
        val publicId = extractPublicIdFromUrl(imageUrl) ?: return
        val cloudName = Constants.CLRY_CLOUD_NAME
        val apiKey = Constants.CLRY_API_KEY
        val apiSecret = Constants.CLRY_API_SECRET
        val timestamp = (System.currentTimeMillis() / 1000).toString()

        val params = mapOf(
            "public_id" to publicId,
            "timestamp" to timestamp
        )

        val signature = generateSignature(params, apiSecret)

        val client = OkHttpClient()
        val body = FormBody.Builder()
            .add("public_id", publicId)
            .add("timestamp", timestamp)
            .add("api_key", apiKey)
            .add("signature", signature)
            .build()

        val request = Request.Builder()
            .url("https://api.cloudinary.com/v1_1/$cloudName/image/destroy")
            .post(body)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                Log.e("Cloudinary", "Error al eliminar imagen: ${response.code} - ${response.message}")
            } else {
                Log.d("Cloudinary", "Imagen eliminada correctamente: $publicId")
            }
        }
    }



    /****   UPDATE IMAGE   ****/
    fun uploadImageToCloudinaryBlocking(
        context: Context,
        imageUri: Uri,
        folder:String = "smgamer"
    ): String? {
        val cloudName = Constants.CLRY_CLOUD_NAME
        val uploadPreset = Constants.CLRY_UPLOAD_PRESET
        val rotatedBitmap = rotateImageIfRequired(context, imageUri)
        val compressedFile = compressBitmapToFile(context, rotatedBitmap)

        val client = OkHttpClient()
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", compressedFile.name, compressedFile.asRequestBody("image/webp".toMediaTypeOrNull()))
            .addFormDataPart("upload_preset", uploadPreset)
            .addFormDataPart("folder", folder)
            .build()

        val request = Request.Builder()
            .url("https://api.cloudinary.com/v1_1/$cloudName/image/upload")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val json = JSONObject(response.body?.string() ?: "")
                return json.getString("secure_url")
            }
        }
        return null
    }


    private fun rotateImageIfRequired(context: Context, imageUri: Uri): Bitmap {
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

    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun compressBitmapToFile(context: Context, bitmap: Bitmap): File {
        val compressedFile = File(context.cacheDir, "compressed_${System.currentTimeMillis()}.webp")
        FileOutputStream(compressedFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.WEBP, 80, out)  // Calidad 80%
        }
        return compressedFile
    }
}