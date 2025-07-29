package com.smgamer.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.smgamer.R

@Composable
fun FilterCard(
    modifier: Modifier = Modifier,
    name: String,
    imageRes:Int = R.drawable.icon_pc
){
    Card(
        modifier
            .padding(horizontal = dimensionResource(R.dimen.common_padding_default),
                vertical = dimensionResource(R.dimen.common_padding_mini)
            )
            ,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.common_padding_default))
        ) {
            val context = LocalContext.current
            val image = ContextCompat.getDrawable(context, imageRes)
            image?.let {
                Image(bitmap = image.toBitmap().asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.
                        background(color = Color.Black)
                        .size(100.dp)
                        .padding(8.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Text(text = name,
                modifier= Modifier
                    .padding(horizontal = dimensionResource(R.dimen.common_padding_default),
                        vertical = dimensionResource(R.dimen.common_padding_min)
                    ),
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FilterCardPreview(){
    FilterCard(modifier = Modifier,"titulo")
}