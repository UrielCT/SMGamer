package com.example.socialmediagamer.view.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.socialmediagamer.R
import com.example.socialmediagamer.databinding.ActivityMainBinding
import com.example.socialmediagamer.model.models.User
import com.example.socialmediagamer.model.providers.AuthProvider
import com.example.socialmediagamer.model.providers.UsersProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dmax.dialog.SpotsDialog

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    private lateinit var mAuthProvider:AuthProvider
    private lateinit var mUsersProvider: UsersProvider


    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private val REQUEST_CODE_GOOGLE:Int = 1


    private lateinit var mDialog: AlertDialog







    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuthProvider = AuthProvider()
        mUsersProvider = UsersProvider()

        mDialog =  SpotsDialog.Builder()
            .setContext(this)
            .setMessage("Espere un momento")
            .setCancelable(false).build()


        val gso: GoogleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)



        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            logIn()
        }

        binding.btnLoginGoogle.setOnClickListener {
            signInGoogle()
        }
    }


    override fun onStart() {
        super.onStart()
        if (mAuthProvider.getUserSession() != null){
            val intent =Intent(this, HomeActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)


        }
    }


    // se ejecuta cuando el usuario realiza una accion

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == REQUEST_CODE_GOOGLE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("ERROR", "Google sign in failed", e)
            }
        }
    }



    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
       mDialog.show()
        mAuthProvider.googleLogin(acct).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    var id:String = mAuthProvider.getUid().toString()
                    checkUserExist(id)
                    startActivity(Intent(this, HomeActivity::class.java))
                } else {
                    mDialog.dismiss()
                    Log.w("ERROR", "signInWithCredential:failure", task.exception)
                    Toast.makeText(this,"Nos se pudo iniciar sesion con google",Toast.LENGTH_SHORT).show()
                }
            }
    }







    private fun checkUserExist(id:String) {
        mUsersProvider.getUser(id).addOnSuccessListener {
            if(it.exists()){
                mDialog.dismiss()
                startActivity(Intent(this, HomeActivity::class.java))

            }else{
                var email: String = mAuthProvider.getEmail().toString()
                var map: Map<String,Any> = hashMapOf(
                    "email" to email
                )
                val user :User = User()
                user.email = email
                user.id = id

                mUsersProvider.create(user).addOnCompleteListener {
                    mDialog.dismiss()
                    if (it.isSuccessful){
                        startActivity(Intent(this, CompleteProfileActivity::class.java))

                    }else{
                        Toast.makeText(this,"no se pudo almacenar la informacion del usuario",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }






    private fun signInGoogle() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, REQUEST_CODE_GOOGLE)
    }





    private fun logIn(){
        val email = binding.etCorreo.text.toString()
        val password = binding.etPassword.text.toString()

        if (email.isNullOrEmpty() || password.isNullOrEmpty()){
            Toast.makeText(this,"complete los campos que faltan!",Toast.LENGTH_SHORT).show()
        }else{
            mDialog.show()
            mAuthProvider.login(email, password).addOnCompleteListener {
                mDialog.dismiss()
                if (it.isSuccessful){
                    val intent= Intent(this, HomeActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }else{
                    Toast.makeText(this,"El email o la contraseña que ingresaste no son correctas", Toast.LENGTH_SHORT).show()
                }
            }
        }
        Log.d("CAMPO","email: $email")
        Log.d("CAMPO","password: $password")
    }


}