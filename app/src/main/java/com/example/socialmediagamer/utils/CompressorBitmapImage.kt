package com.example.socialmediagamer.utils


/*class CompressorBitmapImage {
    /*
     * Metodo que permite comprimir imagenes y transformarlas a bitmap
     */
    fun getImage(context: Context, path: String?, width: Int, height: Int): ByteArray {

        val file_thumb_path = File(path)    // almacena el path del archivo
        var thumb_bitmap: Bitmap? = null
        try {
            thumb_bitmap = id.zelory.compressor.Compressor(context)
                .setMaxWidth(width)
                .setMaxHeight(height)
                .setQuality(75)
                .compressToBitmap(file_thumb_path)

        } catch (e: IOException) {
            e.printStackTrace()
        }
        val baos = ByteArrayOutputStream()
        thumb_bitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, baos)


        return baos.toByteArray()
    }
}*/
