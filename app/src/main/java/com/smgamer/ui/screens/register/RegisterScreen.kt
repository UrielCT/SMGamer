package com.smgamer.ui.screens.register

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.smgamer.R
import com.smgamer.ui.components.LogTextField
import com.smgamer.ui.theme.BottomBarPadding
import com.smgamer.ui.theme.CommonFontSizeLarge
import com.smgamer.ui.theme.CommonPaddingDefault
import com.smgamer.ui.theme.CommonPaddingLarge_med
import com.smgamer.ui.theme.CommonPaddingMiddle
import com.smgamer.ui.theme.CommonPaddingMin
import com.smgamer.ui.theme.CommonPaddingMinDefault
import com.smgamer.ui.viewmodels.LoginViewModel
import java.util.regex.Pattern

@Composable
fun RegisterScreen(
    loginViewModel: LoginViewModel,
    navToHome:()-> Unit,
    navBack: () -> Unit,
    modifier: Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val auth = Firebase.auth

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }


    val db = Firebase.firestore
    val usersCollection = db.collection("Users")

    fun isEmailValid(email: String): Boolean {
        val pattern = Pattern.compile(
            "^[\\w.-]+@([\\w-]+\\.)+[A-Z]{2,4}$",
            Pattern.CASE_INSENSITIVE
        )
        return pattern.matcher(email).matches()
    }



    fun register() {
        // Validación de campos vacíos
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || phone.isEmpty()) {
            Toast.makeText(context, "Para continuar inserta todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Validación de email
        if (!isEmailValid(email)) {
            Toast.makeText(context, "El correo electrónico no es válido", Toast.LENGTH_LONG).show()
            return
        }

        // Validación de contraseñas
        if (password != confirmPassword) {
            Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return
        }

        // Validación de longitud de contraseña
        if (password.length < 6) {
            Toast.makeText(context, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            return
        }

        // Si pasa todas las validaciones

        loginViewModel.createUserWithEmailAndPassword(
            email, password,
            userName = username,
            navToHome = navToHome,
            phone = phone
        )


//        coroutineScope.launch {
//            try {
//                isLoading = true
//                val authResult = auth.createUserWithEmailAndPassword(email.value, password.value).await()
//
//                if (authResult.user != null) {
//                    val userId = authResult.user!!.uid
//
//                    val user = User(
//                        id = userId,
//                        email = email.value,
//                        username = username.value,
//                        phone = phone.value,
//                        timestamp = Date().time
//                    )
//
//                    usersCollection.document(userId).set(user).await()
//
//                    Toast.makeText(context, "Registro exitoso!", Toast.LENGTH_SHORT).show()
//                    navToHome()
//                }
//            } catch (e: Exception) {
//                Toast.makeText(context, "Error al registrar: ${e.message}", Toast.LENGTH_SHORT).show()
//                Log.d("login","Error al registrarse: ${e.message}")
//            } finally {
//                isLoading = false
//            }
//        }
    }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .imePadding()
    ) {
        Text(
            text = stringResource(R.string.sign_up),
            fontSize = CommonFontSizeLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(CommonPaddingDefault)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = BottomBarPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            item { Spacer(Modifier.height(CommonPaddingDefault)) }

            item {
                LogTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = stringResource(R.string.username),
                    placeholder = stringResource(R.string.username_ph),
                    leadingIcon = Icons.Default.Person,
                    keyboardType = KeyboardType.Text,
                )
            }

            item { Spacer(Modifier.height(CommonPaddingDefault)) }

            item {
                LogTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = stringResource(R.string.email),
                    placeholder = stringResource(R.string.email_ph),
                    leadingIcon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email
                )
            }

            item { Spacer(Modifier.height(CommonPaddingDefault)) }

            item {
                LogTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = stringResource(R.string.phone),
                    placeholder = stringResource(R.string.phone_ph),
                    leadingIcon = Icons.Default.Phone,
                    keyboardType = KeyboardType.Number
                )
            }

            item { Spacer(Modifier.height(CommonPaddingDefault)) }

            item {
                LogTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = stringResource(R.string.password),
                    placeholder = stringResource(R.string.password_ph),
                    leadingIcon = Icons.Default.Lock,
                    keyboardType = KeyboardType.Password,
                    isPassword = true,
                    isPasswordVisible = passwordVisible,
                    onPasswordToggleClick = { passwordVisible = !passwordVisible }
                )
            }

            item { Spacer(Modifier.height(CommonPaddingDefault)) }



            item {
                LogTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = stringResource(R.string.confirmPassword),
                    placeholder = stringResource(R.string.confirmPassword_ph),
                    leadingIcon = Icons.Default.Lock,
                    keyboardType = KeyboardType.Password,
                    isPassword = true,
                    isPasswordVisible = confirmPasswordVisible,
                    onPasswordToggleClick = { confirmPasswordVisible = !confirmPasswordVisible }
                )
            }


            item { Spacer(Modifier.height(CommonPaddingMiddle)) }

            item {
                Button(
                    onClick = { register() },
                    modifier = Modifier
                        .padding(horizontal = CommonPaddingDefault)
                        .fillMaxWidth()
                        .height(CommonPaddingLarge_med),
                    shape = RoundedCornerShape(CommonPaddingMinDefault)
                ) {
                    Text(stringResource(R.string.register), fontWeight = FontWeight.Bold)
                }
            }

            item { Spacer(Modifier.height(CommonPaddingDefault)) }

            item {
                Text(
                    stringResource(R.string.register_with),
                    style = MaterialTheme.typography.labelLarge
                )
            }

            item { Spacer(Modifier.height(CommonPaddingDefault)) }

            item {
                OutlinedButton(
                    onClick = { /* Google login */ },
                    modifier = Modifier
                        .padding(horizontal = CommonPaddingDefault)
                        .fillMaxWidth()
                        .height(CommonPaddingLarge_med),
                    shape = RoundedCornerShape(CommonPaddingMinDefault)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_email_grey),
                        contentDescription = null,
                        modifier = Modifier.size(CommonPaddingMiddle)
                    )
                    Spacer(Modifier.width(CommonPaddingMin))
                    Text(stringResource(R.string.google))
                }
            }

            item { Spacer(Modifier.height(CommonPaddingDefault)) }
        }

    }
}