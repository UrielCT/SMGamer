package com.smgamer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.smgamer.R

@Composable
fun FilterCard(
    modifier: Modifier = Modifier,
    name: String,
    imageRes:Int = R.drawable.icon_pc,
    navToFilteredPosts: () -> Unit
){

    val painter = if (imageRes != 0) painterResource(id = imageRes)
    else rememberVectorPainter(Icons.Default.AccountCircle)

    val context = LocalContext.current

    Card(
        onClick = { navToFilteredPosts() },
        modifier
            .padding(horizontal = dimensionResource(R.dimen.common_padding_default),
                vertical = dimensionResource(R.dimen.common_padding_mini)
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.common_padding_default))
        ) {
//            val context = LocalContext.current
//            val image = ContextCompat.getDrawable(context, imageRes)
//            image?.let {
//                Image(painter = painterResource(id= imageRes) ,
//                    contentDescription = null,
//                    modifier = Modifier.
//                        background(color = Color.Black)
//                        .size(100.dp)
//                        .padding(8.dp),
//                    contentScale = ContentScale.Crop
//                )
//            }
//            Image(
//                painter = painter, // ✅ Carga eficiente
//                contentDescription = name,
//                modifier = Modifier
//                    .size(80.dp)
//                    .padding(8.dp)
//                    .background(Color.Gray, shape = CircleShape),
//                contentScale = ContentScale.Crop
//            )
            // ✅ Imagen con Coil
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageRes)
                    .crossfade(true)
                    .build(),
                contentDescription = name,
                modifier = Modifier
                    .size(80.dp)
                    .padding(8.dp)
                    .background(Color.Gray, shape = CircleShape),
                contentScale = ContentScale.Crop
            )

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

//@Preview(showBackground = true)
//@Composable
//private fun FilterCardPreview(){
//    FilterCard(modifier = Modifier,"titulo", navToFilteredPosts = {})
//}