package com.example.socialmediagamer.model.providers

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class AuthProvider {

    private val mAuth : FirebaseAuth= FirebaseAuth.getInstance()

    fun register(email:String,password: String):Task<AuthResult>{
        return mAuth.createUserWithEmailAndPassword(email, password)
    }



    fun login(email:String ,password:String ): Task<AuthResult> {
        return mAuth.signInWithEmailAndPassword(email,password)
    }

    fun googleLogin(acct:GoogleSignInAccount): Task<AuthResult>{
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        return mAuth.signInWithCredential(credential)
    }

    fun getUid(): String? {
        return if (mAuth.currentUser!=null) mAuth.currentUser!!.uid else {
            null
        }
    }

    fun getUserSession(): FirebaseUser? {
        return if (mAuth.currentUser!=null) mAuth.currentUser!! else {
            null
        }
    }


    fun getEmail(): String? {
        return if (mAuth.currentUser!=null) mAuth.currentUser!!.email else {
            null
        }
    }


    fun logout(){
        if (mAuth != null){
            mAuth.signOut()
        }
    }

}