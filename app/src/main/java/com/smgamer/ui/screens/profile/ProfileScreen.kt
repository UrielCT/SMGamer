package com.smgamer.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
fun ProfileScreen(
    modifier: Modifier,
    navToEditProfile: () -> Unit
    ){
    val context = LocalContext.current

    ConstraintLayout(
        modifier = modifier
            .background(Blue100)
    ) {
        val(coverImg,txtEdit,colPublic,colPhone,userImg,userName, userEmail,
            publicTxt,lazyPublic) = createRefs()
        val image = ContextCompat.getDrawable(context, R.drawable.cover_image)
        val userImage = ContextCompat.getDrawable(context, R.drawable.ic_person)



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


        Text("EDITAR PERFIL",
            color = Color.White,
            fontSize = 20.sp,
            modifier=Modifier
                .clickable{
                    navToEditProfile()
                }
                .constrainAs(txtEdit){
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier= Modifier
                .constrainAs(colPublic){
                    start.linkTo(parent.start)
                    bottom.linkTo(coverImg.bottom)
                }
        ) {
            Text("1", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text("PUBLICACIONES", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier= Modifier
                .constrainAs(colPhone){
                    end.linkTo(parent.end)
                    bottom.linkTo(coverImg.bottom)
                }
        ) {
            Text("12874387", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text("TELÉFONO", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        userImage?.let {
            Image(bitmap = userImage.toBitmap().asImageBitmap(),
                contentDescription = "Profile image",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(3.dp, color = Color.LightGray, CircleShape)
                    .background(color = Color.Blue)
                    .constrainAs(userImg){
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(coverImg.bottom)
                        top.linkTo(coverImg.bottom)
                    },
                contentScale = ContentScale.Crop
            )
        }

        Text("Nombre",
            color = Color.Black,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .constrainAs(userName){
                    top.linkTo(userImg.bottom)
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                }
        )

        Text("uriel@gmail.com",
            color = Color.Black,
            fontSize = 14.sp,
            modifier = Modifier
                .constrainAs(userEmail){
                    top.linkTo(userName.bottom)
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                }
        )

        Text("Publicaciones",
            color = Color(0xFFFF9800),
            fontSize = 18.sp,
            modifier = Modifier
                .constrainAs(publicTxt){
                    top.linkTo(userEmail.bottom)
                    start.linkTo(parent.start)
                }
        )


        LazyColumn(
            modifier = Modifier
                .constrainAs(lazyPublic) {
                    top.linkTo(publicTxt.bottom)
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
private fun ProfileScreenPreview(){
    ProfileScreen(Modifier,{})
}