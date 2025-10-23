package com.smgamer.ui.components.common

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.smgamer.R

data class GoogleSignInComponents(
    val googleSignInClient: GoogleSignInClient,
    val launcher: (AuthCredential) -> Unit
)

@Composable
fun rememberGoogleSignInLauncher(
    onCredentialReady: (AuthCredential) -> Unit
): Pair<GoogleSignInClient, () -> Unit> {
    val context = LocalContext.current

    // Configurar cliente de Google (solo se crea una vez)
    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    // Configurar launcher (para recibir resultado del intent)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            onCredentialReady(credential)
        } catch (e: Exception) {
            Toast.makeText(context, "Error al iniciar sesión con Google", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    // Devuelvo el cliente y una función que lanza el intent
    return Pair(
        googleSignInClient,
        { launcher.launch(googleSignInClient.signInIntent) }
    )
}