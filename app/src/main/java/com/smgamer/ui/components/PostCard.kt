package com.smgamer.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.smgamer.R

@Composable
fun PostCard(modifier: Modifier){
    Card(
        modifier= Modifier.padding(horizontal = dimensionResource(R.dimen.common_padding_default),
            vertical = dimensionResource(R.dimen.common_padding_min)),
        onClick = {}) {

        Column (modifier = Modifier.fillMaxWidth()){
            val image = ContextCompat.getDrawable(LocalContext.current, R.drawable.cover_image)
            Image(bitmap = image!!.toBitmap().asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height( 200.dp),
                contentScale = ContentScale.FillWidth
                    )
            Text("Nombre",
                modifier= Modifier
                    .padding(horizontal = dimensionResource(R.dimen.common_padding_default),
                        vertical = dimensionResource(R.dimen.common_padding_min)),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp)
            Text("By User123",
                modifier= Modifier
                    .padding(horizontal = dimensionResource(R.dimen.common_padding_default)),
                fontFamily = FontFamily.SansSerif,
                fontSize = 14.sp,
                color = Color(0xffff9800)
            )
            Text("Comentario",
                modifier= Modifier
                    .padding(horizontal = dimensionResource(R.dimen.common_padding_default),
                        vertical = dimensionResource(R.dimen.common_padding_min)),
                fontFamily = FontFamily.SansSerif,
                fontSize = 14.sp,)
            Row (
                modifier = Modifier
                    .padding(horizontal = dimensionResource(R.dimen.common_padding_default),
                        vertical = dimensionResource(R.dimen.common_padding_min)
                    ),
                verticalAlignment = Alignment.CenterVertically){
                IconButton(onClick = {}) {
                    Icon(painter = painterResource(id= R.drawable.icon_like_grey) ,
                        contentDescription = null)
                }
                Spacer(Modifier.weight(1f))
                Text("1 me gusta")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PostCardPreview(){
    PostCard(modifier = Modifier)
}