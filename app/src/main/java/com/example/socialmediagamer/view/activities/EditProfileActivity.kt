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
import com.example.socialmediagamer.databinding.ActivityEditProfileBinding
import com.example.socialmediagamer.model.models.User
import com.example.socialmediagamer.model.providers.AuthProvider
import com.example.socialmediagamer.model.providers.ImageProvider
import com.example.socialmediagamer.model.providers.UsersProvider
import com.example.socialmediagamer.utils.FileUtil
import com.example.socialmediagamer.utils.ViewedMessageHelper
import com.squareup.picasso.Picasso
import dmax.dialog.SpotsDialog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding

    private var mImageFile :File? = null
    private var mImageFile2 : File? = null

    private var mImageProfile : String = ""
    private var mImageCover : String = ""


    private lateinit var mBuilderSelector: AlertDialog.Builder
    private lateinit var options:Array<String>
    private val GALLERY_REQUEST_CODE_PROFILE:Int = 1
    private val GALLERY_REQUEST_CODE_COVER:Int = 2
    private val PHOTO_REQUEST_CODE_PROFILE:Int = 3
    private val PHOTO_REQUEST_CODE_COVER:Int = 4

    // FOTO 1
    private lateinit var mAbsolutePhotoPath:String
    private lateinit var mPhotoPath:String
    private var mPhotoFile: File? = null

    // FOTO 2
    private lateinit var mAbsolutePhotoPath2:String
    private lateinit var mPhotoPath2:String
    private var mPhotoFile2: File? = null

    private var mUsername:String = ""
    private var mPhone:String = ""


    private lateinit var mDialog: AlertDialog

    private lateinit var mImageProvider:ImageProvider

    private lateinit var mUsersProvider: UsersProvider

    private lateinit var mAuthProvider: AuthProvider


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


        mBuilderSelector= AlertDialog.Builder(this)
        mBuilderSelector.setTitle("selecciona una opcion")
        options = arrayOf(
            "Imagen de galeria",
            "Tomar foto"
        )

        mImageProvider = ImageProvider()
        mUsersProvider = UsersProvider()
        mAuthProvider = AuthProvider()


        mDialog =  SpotsDialog.Builder()
            .setContext(this)
            .setMessage("Espere un momento")
            .setCancelable(false).build()


        binding.btnEditProfile.setOnClickListener {
            clickEditProfile()
        }


        binding.btnBack.setOnClickListener {
            finish()
        }


        binding.imPerfil.setOnClickListener {
            selectOptionImage(1)
        }

        binding.imageViewCover.setOnClickListener {
            selectOptionImage(2)
        }


        getUser()

    }


    private fun getUser(){
        mUsersProvider.getUser(mAuthProvider.getUid().toString()).addOnSuccessListener {
            if (it.exists()){
                if (it.contains("username")){
                    mUsername = it.getString("username").toString()
                    binding.etUsername.setText(mUsername)
                }
                if (it.contains("phone")) {
                    mPhone = it.getString("phone").toString()
                    binding.etTelefono.setText(mPhone)
                    println("telefono: "+ mPhone)
                }
                if (it.contains("image_profile")){
                    mImageProfile = it.getString("image_profile").toString()
                    if (!mImageProfile.isNullOrEmpty()){
                        Picasso.get().load(mImageProfile).fit().into(binding.imPerfil)
                    }
                }
                if (it.contains("image_cover")){
                    mImageCover = it.getString("image_cover").toString()
                    if (!mImageCover.isNullOrEmpty()){
                        Picasso.get().load(mImageCover).fit().into(binding.imageViewCover)
                    }
                }
            }
        }
    }


    private fun clickEditProfile(){
        mUsername = binding.etUsername.text.toString()
        mPhone = binding.etTelefono.text.toString()
        if (mUsername.isNotEmpty() && mPhone.isNotEmpty()){
            // SELECCIONO AMBAS FOTOS DE LA GALERIA
            if (mImageFile != null && mImageFile2 != null){
                mDialog.show()
                GlobalScope.launch {
                    saveImageCoverAndProfile(mImageFile!!,mImageFile2!!)
                }
            }
            // TOMO LAS DOS FOTOS DE LA CAMARA
            else if (mPhotoFile != null && mPhotoFile2 != null){
                mDialog.show()
                GlobalScope.launch {
                    saveImageCoverAndProfile(mPhotoFile!!,mPhotoFile2!!)
                }
            }
            else if (mImageFile != null && mPhotoFile2 != null){
                mDialog.show()
                GlobalScope.launch {
                    saveImageCoverAndProfile(mImageFile!!,mPhotoFile2!!)
                }
            }
            else if (mPhotoFile != null && mImageFile2 != null){
                mDialog.show()
                GlobalScope.launch {
                    saveImageCoverAndProfile(mPhotoFile!!,mImageFile2!!)
                }
            }
            else if (mPhotoFile != null){
                GlobalScope.launch {
                    saveImage(mPhotoFile!!,true)
                }
            }
            else if (mPhotoFile2 != null){
                GlobalScope.launch {
                    saveImage(mPhotoFile2!!,false)
                }
            }
            else if (mImageFile != null){
                GlobalScope.launch {
                    saveImage(mImageFile!!,true)
                }
            }
            else if (mImageFile2 != null){
                GlobalScope.launch {
                    saveImage(mImageFile2!!,false)
                }
            }
            else{
                val user=User()
                user.username = mUsername
                user.phone = mPhone
                user.id = mAuthProvider.getUid().toString()
                updateInfo(user)
            }
        }
        else{
            Toast.makeText(this,"Ingrese el nombre de usuario y el telegono",Toast.LENGTH_SHORT).show()
        }

    }

    private suspend fun saveImageCoverAndProfile(imageFile1 :File,imageFile2:File){
        mImageProvider.save(this,imageFile1).addOnCompleteListener{
            if (it.isSuccessful){
                mImageProvider.getStorage().downloadUrl.addOnSuccessListener {
                    val urlPofile:String = it.toString()

                    GlobalScope.launch (){
                        mImageProvider.save(applicationContext, imageFile2).addOnCompleteListener{ task->

                            if (task.isSuccessful){

                                mImageProvider.getStorage().downloadUrl.addOnSuccessListener {uri2: Uri ->

                                    val urlCover:String = uri2.toString()
                                    val user = User()
                                    user.username = mUsername
                                    user.phone = mPhone
                                    user.image_profile = urlPofile
                                    user.image_cover = urlCover
                                    user.id = mAuthProvider.getUid().toString()
                                    updateInfo(user)
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

    private suspend fun saveImage(image:File, isProfileImage :Boolean){
        mImageProvider.save(this,image).addOnCompleteListener{
            if (it.isSuccessful){
                mImageProvider.getStorage().downloadUrl.addOnSuccessListener {
                    val url:String = it.toString()
                    val user = User()
                    user.username = mUsername
                    user.phone = mPhone
                    if (isProfileImage){
                        user.image_profile = url
                        user.image_cover = mImageCover
                    }
                    else{
                        user.image_cover = url
                        user.image_profile = mImageProfile
                    }
                    user.id = mAuthProvider.getUid().toString()
                    updateInfo(user)

                }
            }else{
                mDialog.dismiss()
                Toast.makeText(this,"hubo un error al almacenar la imagen",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateInfo(user:User){
        if (mDialog.isShowing){
            mDialog.show()
        }
        mDialog.show()
        mUsersProvider.update(user).addOnCompleteListener { itTask ->
            mDialog.dismiss()
            if (itTask.isSuccessful){
                Toast.makeText(applicationContext,"la informacion se actualizó correctamente",Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(applicationContext,"la informacion no se pudo actualizar ",Toast.LENGTH_SHORT).show()

            }
        }
    }


    private fun selectOptionImage(numberImage: Int){
        mBuilderSelector.setItems(options, DialogInterface.OnClickListener { dialogInterface, i ->
            if (i==0){
                if (numberImage == 1){
                    openGallery(GALLERY_REQUEST_CODE_PROFILE)
                }
                else if (numberImage == 2){
                    openGallery(GALLERY_REQUEST_CODE_COVER)
                }
            }
            else if(i == 1){
                if (numberImage == 1){
                    takePhoto(PHOTO_REQUEST_CODE_PROFILE)
                }
                else if (numberImage == 2){
                    takePhoto(PHOTO_REQUEST_CODE_COVER)
                }
            }
        })
        mBuilderSelector.show()
    }




    private fun takePhoto(requestCode: Int){
        val takePictureIntent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

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
        if (requestCode == PHOTO_REQUEST_CODE_PROFILE){
            mPhotoPath = "file:" + photoFile.absolutePath
            mAbsolutePhotoPath = photoFile.absolutePath
        }
        else if (requestCode == PHOTO_REQUEST_CODE_COVER){
            mPhotoPath2 = "file:" + photoFile.absolutePath
            mAbsolutePhotoPath2 = photoFile.absolutePath
        }
        return photoFile
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
        if (requestCode == GALLERY_REQUEST_CODE_PROFILE && resultCode == RESULT_OK){
            try {
                mPhotoFile = null
                mImageFile = FileUtil.from(this,data?.data!!)
                binding.imPerfil.setImageBitmap(BitmapFactory.decodeFile(mImageFile!!.absolutePath))       // muestra la imagen en el imageview
            }catch (e:Exception){
                Log.d("ERROR","se produjo un error " + e.message)
                Toast.makeText(this,"se produjo un error "+ e.message, Toast.LENGTH_LONG).show()
            }
        }

        if (requestCode == GALLERY_REQUEST_CODE_COVER && resultCode == RESULT_OK){
            try {
                mPhotoFile2 = null
                mImageFile2 = FileUtil.from(this,data?.data!!)
                binding.imageViewCover.setImageBitmap(BitmapFactory.decodeFile(mImageFile2!!.absolutePath))       // muestra la imagen en el imageview
            }catch (e:Exception){
                Log.d("ERROR","se produjo un error " + e.message)
                Toast.makeText(this,"se produjo un error "+ e.message, Toast.LENGTH_LONG).show()
            }
        }

        /**
         *     SELECCION DE FOTOGRAFIA
         *
         **/
        if (requestCode == PHOTO_REQUEST_CODE_PROFILE && resultCode == RESULT_OK){
            mImageFile = null
            mPhotoFile = File(mAbsolutePhotoPath)

            Picasso.get().load(mPhotoPath).fit().into(binding.imPerfil)
        }


        /**
         *     SELECCION DE FOTOGRAFIA
         *
         **/
        if (requestCode == PHOTO_REQUEST_CODE_COVER && resultCode == RESULT_OK){
            mImageFile2 = null
            mPhotoFile2 = File(mAbsolutePhotoPath2)

            Picasso.get().load(mPhotoPath2).fit().into(binding.imageViewCover)
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