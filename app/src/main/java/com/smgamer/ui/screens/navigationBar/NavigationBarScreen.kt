package com.smgamer.ui.screens.navigationBar


import android.annotation.SuppressLint
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.smgamer.ui.navigation.Destination
import com.smgamer.ui.navigation.NavigationWrapper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationBarScreen(){
    val navController = rememberNavController()
    val startDestination = Destination.HOME

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val destinations = remember { Destination.entries }

    // Estado para modo búsqueda
    var isSearching by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }


    Scaffold(
        //modifier = Modifier.fillMaxSize(),
        topBar = {
            if(currentRoute != Destination.FILTERS.route && currentRoute != Destination.PROFILE.route){
                TopAppBar(
                    title = {
                        if(isSearching){
                            TextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Buscar...") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                trailingIcon = {
                                    IconButton(onClick = {
                                        searchQuery = "" // limpia el texto
                                        isSearching = false // cierra el modo búsqueda si querés
                                    }) {
                                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                                    }
                                }
                            )
                        }
                        if(currentRoute == Destination.CHATS.route){
                            Text("Chats")
                        }
                    },
                    actions = {
                        if (currentRoute == Destination.HOME.route){
                            if (isSearching) {
                                IconButton(onClick = {
                                    // Al cerrar búsqueda
                                    isSearching = false
                                    searchQuery = ""
                                }) {
                                    Icon(Icons.Default.MoreVert, contentDescription = "Cerrar búsqueda")
                                }
                            } else {
                                IconButton(onClick = { isSearching = true }) {
                                    Icon(Icons.Default.Search, contentDescription = "Buscar")
                                }
                                IconButton(onClick = { /* más acciones */ }) {
                                    Icon(Icons.Default.MoreVert, contentDescription = "Menú")
                                }
                            }
                        }

                    }
                )
            }

        },

        floatingActionButton = {
            if(currentRoute == Destination.HOME.route){
                FloatingActionButton(onClick = {}) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
        },


        bottomBar = {
            BottomBar(
                navController = navController,
                destinations = destinations
            )

        }) { innerPadding ->
        NavigationWrapper(navController, startDestination,modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        )

    }
}


@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun BottomBar(
    navController: NavHostController,
    destinations: List<Destination>
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val NoRippleInteractionSource = MutableInteractionSource()

    NavigationBar {
        destinations.forEach { destination ->
            val selected = currentRoute == destination.route

            NavigationBarItem(
                selected = selected,
                modifier = Modifier.clickable(
                    interactionSource = NoRippleInteractionSource,
                    indication = null
                ){},
                onClick = {
                    if (!selected) {
                        navController.navigate(destination.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(destination.icon, contentDescription = destination.contentDescription)
                },
                label = {
                    Text(destination.label)
                },
                alwaysShowLabel = false,

            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun NavigationBarScreenPreview(){

    NavigationBarScreen()
}