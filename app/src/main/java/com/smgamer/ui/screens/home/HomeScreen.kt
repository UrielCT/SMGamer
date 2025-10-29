package com.smgamer.ui.screens.home

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smgamer.ui.components.PostCard
import com.smgamer.ui.theme.CommonPaddingDefault
import com.smgamer.ui.theme.CommonPaddingMin
import com.smgamer.ui.theme.scaledPadding
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel,
    navToPostDetail: (String) -> Unit,
    navToLogin: () -> Unit
){
    val context = LocalContext.current
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val user by homeViewModel.user
    val searchQuery by homeViewModel.searchQuery.collectAsState()

    var expanded by remember { mutableStateOf(false) }
    var isSearching by rememberSaveable { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    AnimatedContent(
                        targetState = isSearching,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "Search Animation"
                    ) { searching ->
                        if (searching) {
                            SearchBarWithFocus(
                                searchQuery = searchQuery,
                                onSearchQueryChange = { homeViewModel.updateSearchQuery(it) },
                                onCloseSearch = {
                                    homeViewModel.updateSearchQuery("")
                                    isSearching = false
                                    focusManager.clearFocus()
                                    keyboardController?.hide()
                                }
                            )
                        } else {
                            Text("Buscar")
                        }
                    }
                },
                actions = {
                    if (isSearching) {
                        IconButton(onClick = {
                            homeViewModel.updateSearchQuery("")
                            isSearching = false
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }) {
                            Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Cerrar búsqueda")
                        }
                    } else {
                        IconButton(onClick = {
                            isSearching = true
                        }) {
                            Icon(Icons.Default.Search, contentDescription = "Buscar")
                        }

                        Box {
                            IconButton(onClick = { expanded = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "Menú")
                            }

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Cerrar sesión") },
                                    onClick = {
                                        expanded = false
                                        homeViewModel.logout(
                                            onSuccess = { navToLogin() },
                                            onError = {
                                                Toast.makeText(context, "Error al cerrar sesión", Toast.LENGTH_SHORT).show()

                                            }
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { innerPadding ->

        Box(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                uiState.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                uiState.error != null -> {
                    Text(
                    text = "Error: ${uiState.error}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center))

                    Log.d("error","${uiState.error}")
                }
                uiState.postsWithUsers.isEmpty() -> Text(
                    text = "No hay posts disponibles",
                    modifier = Modifier.align(Alignment.Center)
                )
                else -> user?.let { currentUser ->

                    LazyColumn(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        contentPadding = PaddingValues(
                            bottom = 80.dp
                        )
                    ) {
                        items(uiState.postsWithUsers, key = { it.post.id }) { post ->
                            PostCard(
                                data = post,
                                isLiked = post.isLikedBy(currentUser.id),
                                onLikeClick = { homeViewModel.toggleLike(post.post.id, currentUser.id) },
                                navToPostDetail = { postId -> navToPostDetail(postId)},
                                cardPadding = PaddingValues(
                                    horizontal = scaledPadding(CommonPaddingDefault),
                                    vertical = scaledPadding(CommonPaddingMin)
                                )
                            )
                        }
                    }
                }

            }
        }
    }

}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarWithFocus(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onCloseSearch: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        placeholder = { Text("Buscar...") },
        singleLine = true,
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null)
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchQueryChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Borrar texto")
                }
            } else {
                IconButton(onClick = {
                    onCloseSearch()
                    focusManager.clearFocus()
                    keyboardController?.hide()
                }) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar búsqueda")
                }
            }
        },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
                keyboardController?.hide()
            }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .focusRequester(focusRequester),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}