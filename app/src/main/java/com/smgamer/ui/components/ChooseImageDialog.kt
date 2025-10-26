package com.smgamer.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.smgamer.R
import com.smgamer.ui.theme.AddPostSize
import com.smgamer.ui.theme.CommonFontSizeMicro
import com.smgamer.ui.theme.CommonPaddingDefault
import com.smgamer.ui.theme.CommonPaddingMicro
import com.smgamer.ui.theme.CommonPaddingMin
import com.smgamer.ui.theme.GameBottomPadding
import com.smgamer.ui.theme.scaledFont
import com.smgamer.ui.theme.scaledPadding

@Composable
fun ChooseImageDialog(
    onDismissRequest:() -> Unit,
    onGalleryClick:() -> Unit,
    onCameraClick:() -> Unit
){
    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        title = { Text(stringResource(R.string.add_image)) },
        text = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = scaledPadding(CommonPaddingMin)),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ImageFromCard(
                    onClick = { onGalleryClick() },
                    imageRes = R.drawable.ic_gallery_image,
                    text = R.string.from_gallery
                )

                ImageFromCard(
                    onClick = { onCameraClick() },
                    imageRes = R.drawable.ic_camera,
                    text = R.string.take_picture
                )
            }
        },
        confirmButton = {}
    )
}

@Composable
fun ImageFromCard(
    onClick: () -> Unit,
    imageRes: Int,
    text: Int
){
    Card(
        modifier = Modifier
            .size(scaledPadding(GameBottomPadding))
            .clickable { onClick() },
        shape = RoundedCornerShape(scaledPadding(CommonPaddingDefault)),
        elevation = CardDefaults.cardElevation(
            defaultElevation = scaledPadding(CommonPaddingMicro))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaledPadding(CommonPaddingMin)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier.size(scaledPadding(AddPostSize))
            )
            Text(
                text = stringResource(text),
                fontSize = scaledFont(CommonFontSizeMicro),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = scaledPadding(CommonPaddingMicro))
            )
        }
    }
}