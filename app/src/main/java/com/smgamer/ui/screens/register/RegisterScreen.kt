package com.smgamer.ui.screens.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smgamer.R

@Composable
fun RegisterScreen(
    navToHome:()-> Unit,
    navBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    val username = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val phone = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }
    val confirmPasswordVisible = remember { mutableStateOf(false) }


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Header con forma de arco (simulado)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    )
            )

            // Botón de retroceso flotante
            IconButton(
                onClick = navBack,
                modifier = Modifier
                    .size(56.dp)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver atrás",
                    tint = MaterialTheme.colorScheme.primary
                )
            }







            // Contenedor para el avatar y la tarjeta
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 160.dp)
            ) {
                // Tarjeta de register
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 4.dp
                    ),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 70.dp, bottom = 24.dp, start = 24.dp, end = 24.dp)
                            .verticalScroll(scrollState),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Regístrate Ahora",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        // Campos de formulario
                        InputField(
                            value = username.value,
                            onValueChange = { username.value = it },
                            label = "Nombre de usuario",
                            placeholder = "Ingresa tu usuario",
                            leadingIcon = Icons.Default.Person,
                            keyboardType = KeyboardType.Text
                        )

                        Spacer(modifier = Modifier.height(16.dp))

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
                            value = phone.value,
                            onValueChange = { phone.value = it },
                            label = "Teléfono",
                            placeholder = "Ingresa tu número",
                            leadingIcon = Icons.Default.Phone,
                            keyboardType = KeyboardType.Phone
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        InputField(
                            value = password.value,
                            onValueChange = { password.value = it },
                            label = "Contraseña",
                            placeholder = "Crea una contraseña",
                            leadingIcon = Icons.Default.Lock,
                            keyboardType = KeyboardType.Password,
                            isPassword = true,
                            isPasswordVisible = passwordVisible.value,
                            onPasswordToggleClick = { passwordVisible.value = !passwordVisible.value }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        InputField(
                            value = confirmPassword.value,
                            onValueChange = { confirmPassword.value = it },
                            label = "Confirmar contraseña",
                            placeholder = "Repite tu contraseña",
                            leadingIcon = Icons.Default.Lock,
                            keyboardType = KeyboardType.Password,
                            isPassword = true,
                            isPasswordVisible = confirmPasswordVisible.value,
                            onPasswordToggleClick = { confirmPasswordVisible.value = !confirmPasswordVisible.value }
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Botón de registro
                        Button(
                            onClick = navToHome,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 4.dp,
                                pressedElevation = 2.dp
                            )
                        ) {
                            Text(
                                text = "REGISTRARSE",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        // Espacio adicional al final para evitar que el teclado cubra campos
                        Spacer(modifier = Modifier.height(16.dp))

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
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
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
                            imageVector = if (isPasswordVisible) Icons.Default.Lock
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