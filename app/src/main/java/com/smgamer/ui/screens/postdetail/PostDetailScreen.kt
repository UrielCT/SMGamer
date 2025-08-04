package com.smgamer.ui.screens.postdetail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.smgamer.R
import com.smgamer.ui.components.ProfilePostCard
import com.smgamer.ui.theme.Blue100


@Composable
fun PostDetailScreen(
    modifier: Modifier,
    navBack: () -> Unit,
    navToUserProfile: ()->Unit
){
    val context = LocalContext.current
    val image = ContextCompat.getDrawable(context, R.drawable.cover_image)
    val userImage = ContextCompat.getDrawable(context, R.drawable.ic_person)

    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .background(Blue100)
    ) {
        val(coverImg, colPublic, dots, fab, userCard, consoleClip,descTit,descTxt,
            publicTxt,lazyPublic,btnBack) = createRefs()




        image?.let {
            Image(bitmap = image.toBitmap().asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth()
                    .height(220.dp)
                    .constrainAs(coverImg){
                        top.linkTo(parent.top)
                    },
                contentScale = ContentScale.Crop
            )
        }

        IconButton(
            onClick = { navBack() },
            modifier = Modifier
                .constrainAs(btnBack){
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Cerrar"
            )
        }




        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier= Modifier
                .constrainAs(colPublic){
                    start.linkTo(parent.start, margin = 16.dp)
                    bottom.linkTo(coverImg.bottom, margin = 8.dp)
                }
        ) {
            Text("Hace 12 horas", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text("0 me gustas", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }


        // Dots
        CarouselDots(
            totalDots = 3,
            selectedIndex = 0,
            modifier= Modifier.constrainAs(dots){
                bottom.linkTo(coverImg.bottom, margin = 8.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        FloatingActionButton(
            modifier = Modifier.constrainAs(fab){
                end.linkTo(parent.end, margin = 16.dp)
                bottom.linkTo(coverImg.bottom)
                top.linkTo(coverImg.bottom)
            },
            onClick = {}
        ) {
            Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "chat")
        }



        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .border(width = 2.dp, color = Color.Black, shape = RectangleShape)
                .constrainAs(userCard) {
                    top.linkTo(fab.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                userImage?.let {
                    Image(
                        bitmap = it.toBitmap().asImageBitmap(),
                        contentDescription = "Profile picture",
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.Gray)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Juan",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "8928419",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                OutlinedButton(
                    onClick = { navToUserProfile() },
                    shape = RectangleShape,
                    border = BorderStroke(1.dp, Color(0xFFFF9800)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color(0xFFFF9800)
                    )
                ) {
                    Text("VER PERFIL")
                }
            }
        }


        Text("Nombre del juego",
            color = Color.Black,
            fontSize = 18.sp,
            modifier = Modifier
                .constrainAs(publicTxt){
                    top.linkTo(userCard.bottom)
                    start.linkTo(parent.start)
                }
        )

        Text("PS4",
            color = Color.White,
            fontSize = 18.sp,
            modifier = Modifier
                .background(color = Color(0xFFFF9800))
                .constrainAs(consoleClip){
                    top.linkTo(publicTxt.bottom)
                    start.linkTo(parent.start)
                }
        )


        Text("DESCRIPCIÓN",
            color = Color.Black,
            fontSize = 18.sp,
            modifier = Modifier
                .constrainAs(descTit){
                    top.linkTo(consoleClip.bottom)
                    start.linkTo(parent.start)
                }
        )

        Text("fasf f af af  as ffef ef e fe fe f e",
            color = Color.Black,
            fontSize = 16.sp,
            modifier = Modifier
                .constrainAs(descTxt){
                    top.linkTo(descTit.bottom)
                    start.linkTo(parent.start)
                }
        )

        LazyColumn(
            modifier = Modifier
                .constrainAs(lazyPublic) {
                    top.linkTo(descTxt.bottom)
                    bottom.linkTo(parent.bottom)
                    height= Dimension.fillToConstraints
                }

        ) {
            items(6) {
                ProfilePostCard("name", "gdsgd",  R.drawable.ic_person, Modifier)
            }
        }


    }


}

@Preview(showBackground = true)
@Composable
private fun PostDetailScreenPreview(){
    PostDetailScreen(Modifier,{},{})
}



@Composable
fun CarouselDots(
    totalDots: Int = 3,
    selectedIndex: Int = 0,
    modifier: Modifier
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        repeat(totalDots) { index ->
            val isSelected = index == selectedIndex
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(if (isSelected) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color.Black else Color.LightGray)
            )
        }
    }
}