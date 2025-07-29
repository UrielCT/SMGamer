package com.smgamer.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.smgamer.ui.theme.Blue100
import com.smgamer.ui.theme.Green100

@Composable
fun ProfileScreen(modifier: Modifier){
    Column (modifier = modifier
        .fillMaxSize()
        .background(Blue100)
        , horizontalAlignment = Alignment.CenterHorizontally,
        //.padding(dimensionResource(R.dimen.common_padding_min))
    ){
        Text("Filters")
    }
}