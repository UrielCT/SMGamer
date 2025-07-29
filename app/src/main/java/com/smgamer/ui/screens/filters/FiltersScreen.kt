package com.smgamer.ui.screens.filters

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smgamer.R
import com.smgamer.ui.components.FilterCard
import com.smgamer.ui.theme.Green100
import com.smgamer.ui.theme.SMGamerTheme

data class Filter(val name:String,val imageVector: Int)

@Composable
fun FiltersScreen(modifier: Modifier){
    val filtersList : List<Filter> = listOf(
        Filter(name = "PLAYSTATION", imageVector = R.drawable.icon_ps4),
        Filter(name = "XBOX", imageVector = R.drawable.icon_xbox),
        Filter(name = "NINTENDO", imageVector = R.drawable.icon_nintendo),
        Filter(name = "PC", imageVector = R.drawable.icon_pc)
    )

    Column (modifier = modifier
        .fillMaxSize()
        .background(Green100)
        .padding(dimensionResource(R.dimen.common_padding_default)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)

    ){
        filtersList.forEach { filter ->
            FilterCard(
                name = filter.name,
                imageRes = filter.imageVector,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FiltersScreenPreview(){
    SMGamerTheme {
        FiltersScreen(Modifier)
    }
}