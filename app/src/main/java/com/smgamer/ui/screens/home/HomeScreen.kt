package com.smgamer.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.smgamer.R
import com.smgamer.ui.components.PostCard
import com.smgamer.ui.viewmodels.PostsViewModel


data class Post(
    val image:Int = R.drawable.cover_image,
    val name:String ="",
    val user:String="",
    val lastComment:String ="",
    val likes:Int = 0
)

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    postsViewModel: PostsViewModel,
    navToPostDetail: () -> Unit
){

    val postsList = listOf(
        Post(name = "Juego 1", user = "Persona 1", lastComment = "Muy bueno", likes = 10),
        Post(name = "Juego 2", user = "Persona 1", lastComment = "Muy bueno", likes = 8),
        Post(name = "Juego 3", user = "Persona 2", lastComment = "Increíble diseño", likes = 25),
        Post(name = "Juego 4", user = "Persona 3", lastComment = "Me entretuvo bastante", likes = 5),
        Post(name = "Juego 5", user = "Persona 4", lastComment = "Vale la pena probarlo", likes = 13),
        Post(name = "Juego 6", user = "Persona 5", lastComment = "Lo recomendaría", likes = 7),
    )

    LazyColumn(modifier = modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background),
    horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(postsList) { post ->
            PostCard(
                post = post,
                navToPostDetail = navToPostDetail
            )
        }
    }

}