package com.smgamer.ui.screens.filteredposts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smgamer.ui.components.PostCard

@Composable
fun FilteredPostsScreen(
    modifier: Modifier,
    navToPostDetail:()->Unit
){

    Column(modifier = modifier.fillMaxSize()) {
        Text("Total de resultados: ")

        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // Dos columnas
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            content = {
                items(23) { item ->
                    //PostCard(modifier = Modifier, navToPostDetail = navToPostDetail)
                }
            }
        )
    }



}

@Preview(showBackground = true)
@Composable
fun FilteredPostsScreenPreview(){
    FilteredPostsScreen(Modifier,{})
}