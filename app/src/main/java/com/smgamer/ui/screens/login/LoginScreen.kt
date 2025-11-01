package com.smgamer.ui.screens.login

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import com.smgamer.R
import com.smgamer.ui.components.LogTextField
import com.smgamer.ui.components.common.rememberGoogleSignInLauncher
import com.smgamer.ui.theme.BottomBarPadding
import com.smgamer.ui.theme.CommonFontSizeLarge
import com.smgamer.ui.theme.CommonPaddingDefault
import com.smgamer.ui.theme.CommonPaddingLarge_med
import com.smgamer.ui.theme.CommonPaddingMiddle
import com.smgamer.ui.theme.CommonPaddingMin
import com.smgamer.ui.theme.CommonPaddingMinDefault
import java.util.regex.Pattern

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = hiltViewModel(),
    navToHome: () -> Unit,
    navToRegister: () -> Unit,
    modifier: Modifier
) {
    val state by loginViewModel.state.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val (googleSignInClient, launchGoogleSignIn) = rememberGoogleSignInLauncher { credential ->
        loginViewModel.signInWithGoogleCredential(credential, navToHome)
    }


    fun isEmailValid(email: String): Boolean {
        val pattern = Pattern.compile(
            "^[\\w.-]+@([\\w-]+\\.)+[A-Z]{2,4}$",
            Pattern.CASE_INSENSITIVE
        )
        return pattern.matcher(email).matches()
    }

    fun login(){
        if (password != "" && isEmailValid(email) ){

            loginViewModel.signInWithEmailAndPassword(email, password, navToHome)

            Log.d("login","email: $email")
            Log.d("login","password: $password")
        }else{
            Log.d("login","el email o la contraseña son incorrectos")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .imePadding()
    ) {
        Text(
            text = stringResource(R.string.sign_in),
            fontSize = CommonFontSizeLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(CommonPaddingDefault)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = BottomBarPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
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

            item { Spacer(Modifier.height(CommonPaddingMiddle)) }

            //login con email
            item {
                Button(
                    onClick = { login() },
                    modifier = Modifier
                        .padding(horizontal = CommonPaddingDefault)
                        .fillMaxWidth()
                        .height(CommonPaddingLarge_med),
                    shape = RoundedCornerShape(CommonPaddingMinDefault)
                ) {
                    Text(stringResource(R.string.log_in), fontWeight = FontWeight.Bold)
                }
            }

            item { Spacer(Modifier.height(CommonPaddingDefault)) }

            item {
                Text(stringResource(R.string.log_in_with),
                    style = MaterialTheme.typography.labelLarge)
            }

            item { Spacer(Modifier.height(CommonPaddingDefault)) }

            // login con google
            item {
                OutlinedButton(
                    onClick = { launchGoogleSignIn() },
                    enabled = state !is LoginState.Loading,
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

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(CommonPaddingDefault)
        ) {
            Text(stringResource(R.string.no_account_txt))
            Spacer(Modifier.width(CommonPaddingMin))
            TextButton(onClick = { navToRegister() }) {
                Text(stringResource(R.string.register))
            }
        }

        if (state is LoginState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}