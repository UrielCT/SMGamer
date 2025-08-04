package com.smgamer.ui.screens.newpost

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.smgamer.R
import com.smgamer.ui.theme.Blue100

@Composable
fun NewPostScreen(
    modifier: Modifier,
    navBack: () -> Unit
) {
    val context = LocalContext.current
    val image = ContextCompat.getDrawable(context, R.drawable.cover_image)


    ConstraintLayout(
        modifier = modifier.fillMaxSize()
            .background(Blue100)
    ) {
        val (coverImg, btnEdit, editForm) = createRefs()

        var nombre by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }


        image?.let {
            Image(
                bitmap = image.toBitmap().asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .constrainAs(coverImg) {
                        top.linkTo(parent.top)
                    },
                contentScale = ContentScale.Crop
            )


            IconButton(onClick = { navBack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Cerrar"
                )
            }


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .constrainAs(editForm){
                        top.linkTo(coverImg.bottom)
                    },
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Nombre
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    placeholder = { Text("Juan Pérez") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Comentario") },
                    placeholder = { Text("comentario") },
                    singleLine = true,
                    isError = email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches(),
                    modifier = Modifier.fillMaxWidth()
                )

                // Opcional: mensaje de error
                if (email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Text(
                        text = "Correo electrónico no válido",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }





            Button(onClick = {},
                modifier= Modifier
                    .constrainAs(btnEdit){
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                Text("PUBLICAR", fontSize = 22.sp, modifier = Modifier
                    .padding(horizontal = dimensionResource(R.dimen.common_padding_default)))
            }


        }
    }
}

@Preview(showBackground = true)
@Composable
fun NewPostScreenPreview(){
    NewPostScreen(Modifier,{})
}
