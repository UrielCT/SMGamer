package com.smgamer.ui.screens.login

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.smgamer.R
import com.smgamer.ui.viewmodels.LoginViewModel
import java.util.regex.Pattern

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel,
    navToHome: () -> Unit,
    navToRegister: () -> Unit,
    modifier: Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val auth = Firebase.auth

    var email = remember { mutableStateOf("") }
    var password = remember { mutableStateOf("") }
    var passwordVisible = remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }

//    fun firebaseAuthWithGoogle(idToken: String, onSuccess: () -> Unit) {
//        coroutineScope.launch {
//            try {
//                isLoading = true
//                val credential = GoogleAuthProvider.getCredential(idToken, null)
//                auth.signInWithCredential(credential).await()
//                Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
//                onSuccess()
//            } catch (e: Exception) {
//                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
//            } finally {
//                isLoading = false
//            }
//        }
//    }

    // Configuración del login con Google
    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }
//
//    val googleSignInLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        try {
//            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
//            val account = task.getResult(ApiException::class.java)!!
//            firebaseAuthWithGoogle(account.idToken!!, navToHome)
//        } catch (e: ApiException) {
//            Log.w("GoogleSignIn", "Google sign in failed", e)
//            Toast.makeText(context, "Error al iniciar sesión con Google", Toast.LENGTH_SHORT).show()
//        }
//    }



    // DESCOMENTAR

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(ApiException::class.java)!!

            // Crea la credencial y pasa al ViewModel
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            loginViewModel.signInWithGoogleCredential(credential, navToHome)

        } catch (e: ApiException) {
            Log.w("GoogleSignIn", "Google sign in failed", e)
            Toast.makeText(context, "Error al iniciar sesión con Google", Toast.LENGTH_SHORT).show()
        }
    }


    fun isEmailValid(email: String): Boolean {
        val pattern = Pattern.compile(
            "^[\\w.-]+@([\\w-]+\\.)+[A-Z]{2,4}$",
            Pattern.CASE_INSENSITIVE
        )
        return pattern.matcher(email).matches()
    }

    fun login(){
        if (password.value != "" && isEmailValid(email.value) ){

            loginViewModel.signInWithEmailAndPassword(email.value, password.value, navToHome)

            Log.d("login","email: $email")
            Log.d("login","password: $password")
        }else{
            Log.d("login","el email o la contraseña son incorrectos")
        }
    }


    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Header con gradiente
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height( dimensionResource(R.dimen.login_cover_height) )
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    )
            )



            // Contenedor para el avatar y la tarjeta
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 160.dp)
            ) {
                // Tarjeta de login
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimensionResource(R.dimen.common_padding_middle)),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = dimensionResource(R.dimen.common_padding_min),
                        pressedElevation = 4.dp
                    ),
                    shape = RoundedCornerShape(dimensionResource(R.dimen.common_padding_default)),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 70.dp, bottom = 24.dp, start = 24.dp, end = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Iniciar Sesión",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Campos de formulario
                        InputField(
                            value = email.value,
                            onValueChange = { email.value = it },
                            label = "Correo electrónico",
                            placeholder = "tucorreo@ejemplo.com",
                            leadingIcon = Icons.Default.Email,
                            keyboardType = KeyboardType.Email
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        InputField(
                            value = password.value,
                            onValueChange = { password.value = it },
                            label = "Contraseña",
                            placeholder = "Ingresa tu contraseña",
                            leadingIcon = Icons.Default.Lock,
                            keyboardType = KeyboardType.Password,
                            isPassword = true,
                            isPasswordVisible = passwordVisible.value,
                            onPasswordToggleClick = { passwordVisible.value = !passwordVisible.value }
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Botón de login
                        Button(
                            onClick = { login() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(
                                text = "INICIAR SESIÓN",
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Login google button
                        OutlinedButton(
                            onClick = {
                                val signInIntent = googleSignInClient.signInIntent
                                googleSignInLauncher.launch(signInIntent)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            )
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_email_grey),
                                contentDescription = "Google logo",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Google",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Avatar circular superpuesto
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.TopCenter)
                        .offset(y = (-60).dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = CircleShape,
                            clip = true
                        )
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .border(
                            width = 3.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_person),
                        contentDescription = "User avatar",
                        modifier = Modifier.size(60.dp)
                    )
                }
            }


            // Pie de página
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿No tienes cuenta? ",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
                TextButton(
                    onClick = navToRegister,
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Text(
                        text = "Regístrate aquí",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
    keyboardType: KeyboardType,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onPasswordToggleClick: (() -> Unit)? = null
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            modifier = Modifier.padding(bottom = 4.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder) },
            leadingIcon = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                if (isPassword && onPasswordToggleClick != null) {
                    IconButton(onClick = onPasswordToggleClick) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.AccountCircle
                            else Icons.Default.Face,
                            contentDescription = if (isPasswordVisible) "Ocultar contraseña"
                            else "Mostrar contraseña",
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = if (isPassword && !isPasswordVisible)
                PasswordVisualTransformation()
            else VisualTransformation.None,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            singleLine = true
        )
    }
}