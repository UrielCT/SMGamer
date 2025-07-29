package com.smgamer.ui.screens.chats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.smgamer.ui.theme.Green100
import com.smgamer.ui.theme.Yellow100

@Composable
fun ChatsScreen(modifier: Modifier){
    Column (modifier = modifier
        .fillMaxSize()
        .background(Yellow100)
        , horizontalAlignment = Alignment.CenterHorizontally,
        //.padding(dimensionResource(R.dimen.common_padding_min))
    ){
        Text("Filters")
    }
}