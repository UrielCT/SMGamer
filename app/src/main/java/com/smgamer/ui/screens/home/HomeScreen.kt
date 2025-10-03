package com.smgamer.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.smgamer.ui.components.PostCard
import com.smgamer.ui.theme.Background
import com.smgamer.ui.viewmodels.PostsViewModel


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    postsViewModel: PostsViewModel,
    navToPostDetail: () -> Unit
){
    LazyColumn(modifier = modifier
        .fillMaxSize()
        .background(Background),
    horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(5) {
            PostCard(
                modifier,
                navToPostDetail = navToPostDetail
            )
        }
    }

}