package com.smgamer.ui.screens.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.smgamer.ui.models.User
import kotlinx.coroutines.launch
import java.util.Date

class LoginViewModel: ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val db = FirebaseFirestore.getInstance()
    private val _loading = MutableLiveData(false)


    fun signInWithGoogleCredential(credential:AuthCredential, navToHome:()-> Unit) = viewModelScope.launch {
        try {
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        Log.d("login","signInWith google logged")
                        checkAndCreateUserInFirestore(navToHome)
                    }

                }
                .addOnFailureListener {
                    Log.d("login","fallo signInWith google ")
                }
        }
        catch (ex:Exception){
            Log.d("login","signInWith google ${ex.message}")
        }
    }

    private fun checkAndCreateUserInFirestore(navToHome: () -> Unit) {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val userId = user.uid
            val userRef = db.collection("Users").document(userId)

            userRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null && document.exists()) {
                        // Usuario ya existe en Firestore
                        navToHome()
                    } else {
                        // Crear nuevo usuario con valores por defecto
                        createNewUser(user, navToHome)
                    }
                } else {
                    Log.d("login", "Error al verificar usuario: ${task.exception?.message}")
                }
            }
        }
    }

    private fun createNewUser(firebaseUser: FirebaseUser, navToHome: () -> Unit) {
        val userId = firebaseUser.uid
        val email = firebaseUser.email ?: ""
        val displayName = firebaseUser.displayName ?: "Nuevo Usuario"

        val newUser = User(
            id = userId,
            email = email,
            password = "", // No se usa para login con Google
            phone = "",
            username = displayName,
            timestamp = Date().time,
            profileImage = firebaseUser.photoUrl?.toString() ?: ""
        )

        db.collection("Users").document(userId)
            .set(newUser)
            .addOnSuccessListener {
                Log.d("login", "Usuario creado en Firestore")
                navToHome()
            }
            .addOnFailureListener { e ->
                Log.d("login", "Error al crear usuario: $e")
            }
    }





    fun signInWithEmailAndPassword(email:String, password:String, navToHome:() -> Unit) = viewModelScope.launch{
        try {
            auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        Log.d("login","signInWithEmailAndPassword logged")
                        navToHome()
                    }
                    else{
                        Log.d("login","signInWithEmailAndPassword ${task.result}")
                    }
                }
        }
        catch (ex:Exception){
            Log.d("login","signInWithEmailAndPassword ${ex.message}")
        }
    }


    fun createUserWithEmailAndPassword(
        email:String,
        password:String,
        phone:String,
        userName:String,
        navToHome:() -> Unit){
        if(_loading.value == false){
            _loading.value = true
            auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener{ task ->
                    if (task.isSuccessful){

                        createUser(email, password, phone, userName)

                        Log.d("login","createUserWithEmailAndPassword logged")
                        navToHome()
                    }
                    else{
                        Log.d("login","createUserWithEmailAndPassword ${task.result}")
                    }
                    _loading.value = false
                }
        }
    }


    private fun createUser( email: String, password: String,phone: String, userName: String){
        val userId = auth.currentUser?.uid
        val user = User(
            id = userId!!,
            email = email,
            password = password,
            phone= phone,
            username = userName,
            timestamp = Date().time
        )

        //user["user_id"] = userId.toString()
        //user["displayName"] = displayName.toString()
        FirebaseFirestore.getInstance().collection("Users")
            .add(user)
            .addOnSuccessListener {
                Log.d("login", "creado: ${it.id}")
            }
            .addOnFailureListener{
                Log.d("login","ocurrio un error: ${it}")
            }
    }

}