package com.smgamer.ui.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.smgamer.ui.components.PostCard
import com.smgamer.ui.theme.Background
import com.smgamer.ui.theme.SMGamerTheme
import com.smgamer.ui.viewmodels.PostsViewModel


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    postsViewModel: PostsViewModel,
    navToPostDetail: () -> Unit
){
    LazyColumn(modifier = modifier
        .fillMaxSize()
        .background(Background)
        //.padding(dimensionResource(R.dimen.common_padding_min))
        ,
    horizontalAlignment = Alignment.CenterHorizontally,
        //verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.common_padding_min))
    ) {
        items(5) {
            PostCard(
                modifier,
                navToPostDetail = navToPostDetail
            )
        }
    }

}

//@SuppressLint("ViewModelConstructorInComposable")
//@Preview(showBackground = true)
//@Composable
//fun HomeScreenPreview(){
//    SMGamerTheme {
//        val postsViewModel:PostsViewModel= PostsViewModel()
//        HomeScreen(Modifier,postsViewModel,{})
//    }
//}