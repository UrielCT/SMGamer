package com.smgamer.ui.screens.filteredposts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.smgamer.ui.components.PostCard
import com.smgamer.ui.screens.home.Post
import com.smgamer.ui.theme.CommonPaddingDefault
import com.smgamer.ui.theme.CommonPaddingMin
import com.smgamer.ui.theme.scaledPadding

@Composable
fun FilteredPostsScreen(
    modifier: Modifier,
    navToPostDetail:()->Unit
){
    val postsList = listOf(
        Post(name = "Juego 1", user = "Persona 1", lastComment = "Muy bueno", likes = 10),
        Post(name = "Juego 2", user = "Persona 1", lastComment = "Muy bueno", likes = 8),
        Post(name = "Juego 3", user = "Persona 2", lastComment = "Increíble diseño", likes = 25),
        Post(name = "Juego 4", user = "Persona 3", lastComment = "Me entretuvo bastante", likes = 5),
        Post(name = "Juego 5", user = "Persona 4", lastComment = "Vale la pena probarlo", likes = 13),
        Post(name = "Juego 6", user = "Persona 5", lastComment = "Lo recomendaría", likes = 7),
        Post(name = "Juego 6", user = "Persona 5", lastComment = "Lo recomendaría", likes = 7),
        Post(name = "Juego 6", user = "Persona 5", lastComment = "Lo recomendaría", likes = 7),
    )

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "Total de resultados: ",
            modifier = Modifier.padding(
                horizontal = scaledPadding(CommonPaddingDefault),
                vertical = scaledPadding(CommonPaddingMin)
            )
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(scaledPadding(CommonPaddingMin)),
            horizontalArrangement = Arrangement.spacedBy(scaledPadding(CommonPaddingMin)),
            contentPadding = PaddingValues(
                start = scaledPadding(CommonPaddingDefault),
                end = scaledPadding(CommonPaddingDefault),
                top = scaledPadding(CommonPaddingDefault),
                bottom = scaledPadding(CommonPaddingDefault)
            )
        ) {
            items(postsList) { post ->
                PostCard(
                    post = post,
                    navToPostDetail = navToPostDetail
                )
            }
        }
    }
}