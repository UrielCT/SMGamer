package com.example.socialmediagamer.view.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.socialmediagamer.R
import com.example.socialmediagamer.databinding.ActivityPostBinding
import com.example.socialmediagamer.model.models.Post
import com.example.socialmediagamer.model.providers.AuthProvider
import com.example.socialmediagamer.model.providers.ImageProvider
import com.example.socialmediagamer.model.providers.PostProvider
import com.example.socialmediagamer.utils.FileUtil
import com.example.socialmediagamer.utils.ViewedMessageHelper
import com.google.android.gms.tasks.Task
import com.squareup.picasso.Picasso
import dmax.dialog.SpotsDialog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class PostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding
    private val GALLERY_REQUEST_CODE:Int = 1
    private val GALLERY_REQUEST_CODE_2:Int = 2
    private val PHOTO_REQUEST_CODE:Int = 3
    private val PHOTO_REQUEST_CODE_2:Int = 4


    private var mImageFile :File? = null
    private var mImageFile2 : File? = null

    private lateinit var mImageProvider: ImageProvider
    private lateinit var mPostProvider: PostProvider
    private lateinit var mAuthProvider: AuthProvider


    private var mCategory: String? = null
    private var mTitle: String = ""
    private var mDescription: String = ""

    private lateinit var mDialog: AlertDialog
    private lateinit var mBuilderSelector: AlertDialog.Builder

    private lateinit var options:Array<String>

    // FOTO 1
    private lateinit var mAbsolutePhotoPath:String
    private lateinit var mPhotoPath:String
    private var mPhotoFile:File? = null

    // FOTO 2
    private lateinit var mAbsolutePhotoPath2:String
    private lateinit var mPhotoPath2:String
    private var mPhotoFile2:File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)


        mImageProvider = ImageProvider()
        mPostProvider = PostProvider()
        mAuthProvider = AuthProvider()


        mDialog =  SpotsDialog.Builder()
            .setContext(this)
            .setMessage("Espere un momento")
            .setCancelable(false).build()


        mBuilderSelector= AlertDialog.Builder(this)
        mBuilderSelector.setTitle("selecciona una opcion")
        options = arrayOf(
            "Imagen de galeria",
            "Tomar foto"
        )



        binding.imPost1.setOnClickListener {
            selectOptionImage(1)
        }

        binding.imPost2.setOnClickListener {
            selectOptionImage(2)
        }

        binding.btnPost.setOnClickListener {
            clickPost()
        }


        binding.imPc.setOnClickListener {
            mCategory = "PC"
            binding.txCategoria.text = mCategory
        }
        binding.imPs4.setOnClickListener {
            mCategory = "PS4"
            binding.txCategoria.text = mCategory

        }
        binding.imXbox.setOnClickListener {
            mCategory = "XBOX"
            binding.txCategoria.text = mCategory

        }
        binding.imNintendo.setOnClickListener {
            mCategory = "NINTENDO"
            binding.txCategoria.text = mCategory

        }

        binding.btnBack.setOnClickListener {
            finish()
        }


    }


    private fun selectOptionImage(numberImage: Int){
        mBuilderSelector.setItems(options,DialogInterface.OnClickListener { dialogInterface, i ->
            if (i==0){
                if (numberImage == 1){
                    openGallery(GALLERY_REQUEST_CODE)
                }
                else if (numberImage == 2){
                    openGallery(GALLERY_REQUEST_CODE_2)
                }
            }
            else if(i == 1){
                if (numberImage == 1){
                    takePhoto(PHOTO_REQUEST_CODE)
                }
                else if (numberImage == 2){
                    takePhoto(PHOTO_REQUEST_CODE_2)
                }
            }
        })
        mBuilderSelector.show()
    }




    private fun takePhoto(requestCode: Int){
        val takePictureIntent:Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (takePictureIntent.resolveActivity(packageManager) != null){
            var photoFile:File? = null

            try {
                photoFile = createPhotofile(requestCode)
            }catch (e:Exception){
                Toast.makeText(this,"hubo un error con el archivo "+ e.message, Toast.LENGTH_SHORT).show()
            }

            if (photoFile != null){
                var photoUri = FileProvider.getUriForFile(this,"com.example.socialmediagamer",photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(takePictureIntent, requestCode)
            }
        }
    }



    private fun createPhotofile(requestCode: Int):File{
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val photoFile = File.createTempFile(
            "${Date()}" + "_photo",
            ".jpg",
            storageDir
        )
        if (requestCode == PHOTO_REQUEST_CODE){
            mPhotoPath = "file:" + photoFile.absolutePath
            mAbsolutePhotoPath = photoFile.absolutePath
        }
         else if (requestCode == PHOTO_REQUEST_CODE_2){
            mPhotoPath2 = "file:" + photoFile.absolutePath
            mAbsolutePhotoPath2 = photoFile.absolutePath
        }
        return photoFile
    }



    // CLICK DEL BOTON POST
    private fun clickPost(){
        mTitle = binding.etVideoGame.text.toString()
        mDescription = binding.etDescription.text.toString()

        if (!mTitle.isNullOrEmpty() && !mDescription.isNullOrEmpty() && !mCategory!!.isNullOrEmpty()){

            // SELECCIONO AMBAS FOTOS DE LA GALERIA
            if (mImageFile != null && mImageFile2 != null){
                mDialog.show()
                GlobalScope.launch {
                saveImage(mImageFile!!,mImageFile2!!)
                }
            }
            // TOMO LAS DOS FOTOS DE LA CAMARA
            else if (mPhotoFile != null && mPhotoFile2 != null){
                mDialog.show()
                GlobalScope.launch {
                    saveImage(mPhotoFile!!,mPhotoFile2!!)
                }
            }
            else if (mImageFile != null && mPhotoFile2 != null){
                mDialog.show()
                GlobalScope.launch {
                    saveImage(mImageFile!!,mPhotoFile2!!)
                }
            }
            else if (mPhotoFile != null && mImageFile2 != null){
                mDialog.show()
                GlobalScope.launch {
                    saveImage(mPhotoFile!!,mImageFile2!!)
                }
            }
            else{
                Toast.makeText(this,"debe seleccionar una imagen",Toast.LENGTH_SHORT).show()
            }
        }
        else{
            Toast.makeText(this,"completa los campos para publicar",Toast.LENGTH_SHORT).show()
        }
    }




    private suspend fun saveImage(imageFile1 :File,imageFile2:File){
        mImageProvider.save(this,imageFile1).addOnCompleteListener{
            if (it.isSuccessful){
                mImageProvider.getStorage().downloadUrl.addOnSuccessListener {
                    val url:String = it.toString()

                    GlobalScope.launch (){
                        mImageProvider.save(applicationContext, imageFile2).addOnCompleteListener{ task->

                            if (task.isSuccessful){

                                mImageProvider.getStorage().downloadUrl.addOnSuccessListener {uri2: Uri ->

                                    val url2:String = uri2.toString()
                                    val post = Post()
                                    post.image1 = url
                                    post.image2 = url2
                                    post.title= mTitle.lowercase()
                                    post.descripcion = mDescription
                                    post.category = mCategory.toString()
                                    post.idUser = mAuthProvider.getUid().toString()
                                    post.timestamp = Date().time

                                    mPostProvider.save(post).addOnCompleteListener {itTask:Task<Void> ->
                                        mDialog.dismiss()
                                        if(itTask.isSuccessful){
                                            clearForm()
                                            Toast.makeText(applicationContext,"la informacion se almaceno correctamente!",Toast.LENGTH_SHORT).show()
                                        }
                                        else{
                                            Toast.makeText(applicationContext,"no se pudo almacenar la informacion!",Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                            else{
                                Toast.makeText(applicationContext,"hubo un error al almacenar la imagen 2",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }else{
                mDialog.dismiss()
                Toast.makeText(this,"hubo un error al almacenar la imagen",Toast.LENGTH_SHORT).show()
            }
        }
    }



    // RESETEA LAS VARIABLES Y ESO
    private fun clearForm (){
        binding.etVideoGame.setText("")
        binding.etDescription.setText("")
        binding.txCategoria.text = ""
        binding.imPost1.setImageResource(R.drawable.upload_image)
        binding.imPost2.setImageResource(R.drawable.upload_image)
        mTitle = ""
        mDescription = ""
        mCategory = ""
        mImageFile = null
        mImageFile2 = null

    }




    private fun openGallery(requestCode: Int){
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.setType("image/*")
        startActivityForResult(galleryIntent,requestCode)
    }





    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /**
         *     SELECCION DE IMAGEN DESDE LA GALERIA
         *
         **/
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
            try {
                mPhotoFile = null
                mImageFile = FileUtil.from(this,data?.data!!)
                binding.imPost1.setImageBitmap(BitmapFactory.decodeFile(mImageFile!!.absolutePath))       // muestra la imagen en el imageview
            }catch (e:Exception){
                Log.d("ERROR","se produjo un error " + e.message)
                Toast.makeText(this,"se produjo un error "+ e.message, Toast.LENGTH_LONG).show()
            }
        }

        if (requestCode == GALLERY_REQUEST_CODE_2 && resultCode == RESULT_OK){
            try {
                mPhotoFile2 = null
                mImageFile2 = FileUtil.from(this,data?.data!!)
                binding.imPost2.setImageBitmap(BitmapFactory.decodeFile(mImageFile2!!.absolutePath))       // muestra la imagen en el imageview
            }catch (e:Exception){
                Log.d("ERROR","se produjo un error " + e.message)
                Toast.makeText(this,"se produjo un error "+ e.message, Toast.LENGTH_LONG).show()
            }
        }

        /**
         *     SELECCION DE FOTOGRAFIA
         *
         **/
        if (requestCode == PHOTO_REQUEST_CODE && resultCode == RESULT_OK){
            mImageFile = null
            mPhotoFile = File(mAbsolutePhotoPath)

            Picasso.get().load(mPhotoPath).fit().into(binding.imPost1)
        }


        /**
         *     SELECCION DE FOTOGRAFIA
         *
         **/
        if (requestCode == PHOTO_REQUEST_CODE_2 && resultCode == RESULT_OK){
            mImageFile2 = null
            mPhotoFile2 = File(mAbsolutePhotoPath2)

            Picasso.get().load(mPhotoPath2).fit().into(binding.imPost2)
        }

    }


    override fun onStart() {
        super.onStart()
        ViewedMessageHelper().updateOnline(true,this)

    }


    override fun onPause() {
        super.onPause()
        ViewedMessageHelper().updateOnline(false,this)
    }



}