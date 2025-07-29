package com.smgamer.ui.screens.filters

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.smgamer.ui.theme.Green100
import com.smgamer.ui.theme.Pink100

@Composable
fun FiltersScreen(modifier: Modifier){
    Column (modifier = modifier
        .fillMaxSize()
        .background(Green100)
        , horizontalAlignment = Alignment.CenterHorizontally,
        //.padding(dimensionResource(R.dimen.common_padding_min))
    ){
        Text("Filters")
    }
}