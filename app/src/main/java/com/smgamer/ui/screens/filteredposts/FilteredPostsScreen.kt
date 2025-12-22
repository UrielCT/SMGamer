package com.smgamer.ui.screens.filteredposts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smgamer.ui.components.PostCard
import com.smgamer.ui.theme.CommonPaddingDefault
import com.smgamer.ui.theme.CommonPaddingMin
import com.smgamer.ui.theme.scaledPadding

@Composable
fun FilteredPostsScreen(
    modifier: Modifier,
    category:String,
    filteredPostsViewModel: FilteredPostsViewModel = hiltViewModel(),
    navToPostDetail:(String)->Unit
){
    val uiState by filteredPostsViewModel.uiState.collectAsState()

    LaunchedEffect(category) {
        filteredPostsViewModel.loadPostsByCategory(category)
    }

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "Total de resultados: ${uiState.posts.size}",
            modifier = Modifier.padding(
                horizontal = scaledPadding(CommonPaddingDefault),
                vertical = scaledPadding(CommonPaddingDefault)
            )
        )

        when {
            uiState.isLoading -> {
                Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Text(
                    text = uiState.error ?: "Error desconocido",
                    modifier = Modifier.padding(16.dp),
                    color = Color.Red
                )
            }

            uiState.posts.isEmpty() -> {
                Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay publicaciones en esta categoría.")
                }
            }

            else -> uiState.user?.let { currentUser ->
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(scaledPadding(CommonPaddingMin)),
                    horizontalArrangement = Arrangement.spacedBy(scaledPadding(CommonPaddingMin)),
                    contentPadding = PaddingValues(
                        start = scaledPadding(CommonPaddingDefault),
                        end = scaledPadding(CommonPaddingDefault),
                        top = scaledPadding(CommonPaddingDefault),
                        bottom = scaledPadding(CommonPaddingDefault)
                    )
                ) {
                    items(uiState.posts, key = { it.post.id }) { post ->
                        PostCard(
                            data = post,
                            isLiked = post.isLikedBy(currentUser.id),
                            onLikeClick = { filteredPostsViewModel.toggleLike(post.post.id, currentUser.id) },
                            navToPostDetail = { postId -> navToPostDetail(postId)},
                        )
                    }

                }
            }
        }
    }
}