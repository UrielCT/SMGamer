package com.example.socialmediagamer.view.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.socialmediagamer.databinding.ActivityCompleteProfileBinding
import com.example.socialmediagamer.model.models.User
import com.example.socialmediagamer.model.providers.AuthProvider
import com.example.socialmediagamer.model.providers.UsersProvider
import dmax.dialog.SpotsDialog
import java.util.*

class CompleteProfileActivity : AppCompatActivity() {

    private lateinit var binding : ActivityCompleteProfileBinding
    private lateinit var mAuthProvider: AuthProvider
    private lateinit var mUsersProvider: UsersProvider

    private lateinit var mDialog: AlertDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompleteProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuthProvider = AuthProvider()
        mUsersProvider = UsersProvider()


        mDialog =  SpotsDialog.Builder()
            .setContext(this)
            .setMessage("Espere un momento")
            .setCancelable(false).build()



        binding.btnRegistrarse.setOnClickListener {
            register()
        }



    }

    private fun register(){
        val username = binding.etUsername.text.toString()
        val phone = binding.etTelefono.text.toString()


        if (username.isNotEmpty() ){
            updateUser(username,phone)
        }
        else{
            Toast.makeText(this,"completa todos los campos", Toast.LENGTH_SHORT).show()
        }
    }




    private fun updateUser(username:String,phone:String){
        var id = mAuthProvider.getUid().toString()
        val user: User = User()
        user.username = username
        user.id = id
        user.phone = phone
        user.timestamp= Date().time
        //user.password = password


        mDialog.show()

        mUsersProvider.update(user).addOnCompleteListener {
            mDialog.dismiss()
            if (it.isSuccessful){
                startActivity(Intent(this, HomeActivity::class.java))


            }else{
                Toast.makeText(this,"el usuario no se pudo almacenar en la base de datos",
                    Toast.LENGTH_SHORT).show()

            }
        }
    }
}