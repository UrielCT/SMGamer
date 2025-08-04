package com.smgamer.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.smgamer.ui.components.PostCard
import com.smgamer.ui.theme.Pink100
import com.smgamer.ui.theme.SMGamerTheme




@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navToPostDetail: () -> Unit
){
    LazyColumn(modifier = modifier
        .fillMaxSize()
        .background(Pink100)
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

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview(){
    SMGamerTheme {
        HomeScreen(Modifier,{})
    }
}