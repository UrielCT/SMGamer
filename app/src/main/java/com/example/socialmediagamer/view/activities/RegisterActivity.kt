package com.example.socialmediagamer.view.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.socialmediagamer.databinding.ActivityRegisterBinding
import com.example.socialmediagamer.model.models.User
import com.example.socialmediagamer.model.providers.AuthProvider
import com.example.socialmediagamer.model.providers.UsersProvider
import dmax.dialog.SpotsDialog
import java.util.*

import java.util.regex.Matcher
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding : ActivityRegisterBinding


    private lateinit var mAuthProvider: AuthProvider
    private lateinit var mUsersProvider: UsersProvider

    private lateinit var mDialog: AlertDialog



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        mAuthProvider = AuthProvider()
        mUsersProvider = UsersProvider()


        mDialog =  SpotsDialog.Builder()
            .setContext(this)
            .setMessage("Espere un momento")
            .setCancelable(false).build()




        binding.circleButtonBack.setOnClickListener {
            finish()
        }

        binding.btnRegistrarse.setOnClickListener {
            register()
        }
    }




    private fun register(){
        val username = binding.etUsername.text.toString()
        val correo = binding.etCorreo.text.toString()
        val password = binding.etPassword.text.toString()
        val passwordConfirm = binding.etPasswordConfirm.text.toString()
        val phone = binding.etTelefono.text.toString()

        if (username.isNotEmpty() && correo.isNotEmpty() && password.isNotEmpty() && passwordConfirm.isNotEmpty() && phone.isNotEmpty()){
            if (isEmailValid(correo)){
                if (password == passwordConfirm){
                    if (password.length >= 6){
                        createUser(username,correo,password,phone)
                    }else{
                        Toast.makeText(this,"la contraseñas debe tener al menos 6 caracteres ",Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this,"las contraseñas no coinciden ",Toast.LENGTH_SHORT).show()

                }

            }else{
                Toast.makeText(this,"has rellenado todos los campos, pero el correo no es valido",Toast.LENGTH_SHORT).show()
            }


        }
        else{
            Toast.makeText(this,"completa todos los campos",Toast.LENGTH_SHORT).show()

        }
    }


    //VERIFICA SI EL CORREO ESTÁ BIEN ESCRITO
    private fun isEmailValid(email: String?): Boolean {
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(email)
        return matcher.matches()
    }


    // CREA UN USUARIO EN LA BASE DE DATOS CON TODOS SUS DATOS PERSONALES

    private fun createUser(username:String, email:String, password:String,phone:String){
        mDialog.show()
        mAuthProvider.register(email, password).addOnCompleteListener {
            if (it.isSuccessful){
                var id = mAuthProvider.getUid() .toString()// ID DEL USUARIO ACTUAL
                val user : User =User()
                user.email = email
                user.id = id
                user.username = username
                user.phone = phone
                user.password = password
                user.timestamp = Date().time

                mUsersProvider.create(user).addOnCompleteListener {
                    mDialog.dismiss()
                    if (it.isSuccessful){
                        val intent = Intent(this, HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)

                    }else{
                        Toast.makeText(this,"el usuario no se pudo almacenar en la base de datos",Toast.LENGTH_SHORT).show()

                    }
                }
            }else{
                mDialog.dismiss()
                Toast.makeText(this,"no se pudo registrar el usuario",Toast.LENGTH_SHORT).show()

            }
        }
    }
}



