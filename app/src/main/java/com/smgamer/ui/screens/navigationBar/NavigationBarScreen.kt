package com.smgamer.ui.screens.navigationBar

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.smgamer.ui.navigation.Destination
import com.smgamer.ui.navigation.NavigationWrapper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationBarScreen(){
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = Destination.fromRoute(backStackEntry?.destination?.route)

    // 👇 Rutas que NO deben mostrar ninguna barra
    val noBarsRoutes = listOf(
        Destination.Login,
        Destination.Register,
        Destination.EditProfile
    )

    // 👇 Solo muestra BottomNav en ciertas pantallas
    val showBottomBar = currentDestination in listOf(
        Destination.Home,
        Destination.Filters,
        Destination.Profile,
        Destination.Chats,
        Destination.PostDetail
    )

    val showTopBar = currentDestination in listOf(
        Destination.Home,
        Destination.Chats,
        Destination.FilteredPosts
    )

    // 👇 Solo muestra TopBar en una pantalla específica (ej: ChatDetail)
    //val showTopBar = currentDestination == Destination.ChatDetail

    val showFab = currentDestination == Destination.Home


    // Estado para modo búsqueda
    var isSearching by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }



    Scaffold(
        topBar = {
            if(showTopBar){
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
                        if(currentDestination == Destination.Chats){
                            Text("Chats")
                        }else if(currentDestination == Destination.FilteredPosts){
                            Text("Filters")
                        }
                    },
                    actions = {
                        if (currentDestination == Destination.Home){
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

                    },
                    navigationIcon = {
                        if(currentDestination == Destination.FilteredPosts){
                            IconButton(onClick = { navController.popBackStack()
                            }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        }
                    }
                )
            }

        },

        floatingActionButton = {
            if(showFab){
                FloatingActionButton(onClick = {
                    navController.navigate(Destination.PostDetail.route)
                }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
        },


        bottomBar = {
            if (showBottomBar) {
                if (currentDestination != null) {
                    var cd = currentDestination
                    if(cd == Destination.PostDetail){
                        cd = Destination.Home
                    }
                    BottomBar(
                        navController = navController,
                        currentDestination = cd,
                    )
                }
            }
        }

    ) { innerPadding ->
        NavigationWrapper(
            navController= navController,
            //currentDestination = currentDestination,
            modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        )

    }
}


@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun BottomBar(
    navController: NavHostController,
    currentDestination: Destination
    //destinations: List<Destination>
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    //val currentRoute = navBackStackEntry?.destination?.route
    val NoRippleInteractionSource = MutableInteractionSource()

    NavigationBar {
        Destination.bottomBarItems.forEach { destination ->
            //val selected = currentRoute == destination.route

            NavigationBarItem(
                selected = currentDestination?.route == destination.route,
                modifier = Modifier.clickable(
                    interactionSource = NoRippleInteractionSource,
                    indication = null
                ){},
                onClick = {

                    navController.navigate(destination.route) {
                        launchSingleTop = true
                        popUpTo(Destination.Home.route)
                    }

                },
                icon = {
                    destination.icon?.let {
                        Icon(it, contentDescription = destination.contentDescription)
                    }
                },
                label = {
                    destination.label?.let { Text(it) }
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