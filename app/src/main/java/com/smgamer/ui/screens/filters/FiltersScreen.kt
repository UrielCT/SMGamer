package com.smgamer.ui.screens.filters

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.smgamer.R
import com.smgamer.ui.components.FilterCard
import com.smgamer.ui.theme.CommonPaddingDefault

data class Filter(
    val name:String,
    val imageVector: Int
)

@Composable
fun FiltersScreen(
    modifier: Modifier,
    navToFilteredPosts: () -> Unit
) {
    val filtersList: List<Filter> = listOf(
        Filter(name = "PLAYSTATION", imageVector = R.drawable.icon_ps4),
        Filter(name = "XBOX", imageVector = R.drawable.icon_xbox),
        Filter(name = "NINTENDO", imageVector = R.drawable.icon_nintendo),
        Filter(name = "PC", imageVector = R.drawable.icon_pc)
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(vertical = CommonPaddingDefault)
    ) {
        items(filtersList) { filter ->
            FilterCard(
                name = filter.name,
                imageRes = filter.imageVector,
                modifier = Modifier.fillMaxWidth(),
                navToFilteredPosts = { navToFilteredPosts() }
            )
        }
    }
}