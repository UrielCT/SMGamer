package com.smgamer.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import com.smgamer.R
import com.smgamer.ui.components.PostCard
import com.smgamer.ui.theme.Pink100
import com.smgamer.ui.theme.SMGamerTheme




@Composable
fun HomeScreen(modifier: Modifier = Modifier){
    LazyColumn(modifier = modifier
        .fillMaxSize()
        .background(Pink100)
        //.padding(dimensionResource(R.dimen.common_padding_min))
        ,
    horizontalAlignment = Alignment.CenterHorizontally,
        //verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.common_padding_min))
    ) {
        item {
            PostCard(modifier)
        }
        item {
            PostCard(modifier)
        }
        item {
            PostCard(modifier)
        }
        item {
            PostCard(modifier)
        }
    }

}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview(){
    SMGamerTheme {
        HomeScreen(Modifier)
    }
}