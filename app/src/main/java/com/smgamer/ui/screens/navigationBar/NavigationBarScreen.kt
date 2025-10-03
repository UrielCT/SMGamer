package com.smgamer.ui.screens.navigationBar

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.smgamer.R
import com.smgamer.ui.navigation.Destination
import com.smgamer.ui.navigation.NavigationWrapper
import com.smgamer.ui.viewmodels.LoginViewModel
import com.smgamer.ui.viewmodels.PostsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationBarScreen(
    postsViewModel: PostsViewModel,
    loginViewModel: LoginViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    //val currentDestination = Destination.fromRoute(backStackEntry?.destination?.route)
    var expanded by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Estado para modo búsqueda
    var isSearching by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }

    // 👇 Solo muestra BottomNav en ciertas pantallas
    val showBottomBar = currentRoute in listOf(
        Destination.HOME.route,
        Destination.FILTERS.route,
        Destination.PROFILE.route,
        Destination.CHATS.route,
        Destination.POST_DETAIL.route
    )

    val showTopBar = currentRoute in listOf(
        Destination.HOME.route,
        Destination.CHATS.route,
        Destination.FILTERED_POSTS.route,
        Destination.CHAT_DETAIL.route
    )


    val showFab = currentRoute == Destination.HOME.route



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
                        if(currentRoute == Destination.CHATS.route){
                            Text("Chats")
                        }else if(currentRoute == Destination.FILTERED_POSTS.route){
                            Text("Filters")
                        }else if(currentRoute == Destination.CHAT_DETAIL.route){

                            val userImage = ContextCompat.getDrawable(context, R.drawable.ic_person)

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding( vertical = 12.dp)
                            ) {
                                userImage?.let {
                                    Image(
                                        bitmap = it.toBitmap().asImageBitmap(),
                                        contentDescription = "Profile picture",
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(Color.Gray)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Juan",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "en linea",
                                        fontSize = 14.sp,
                                        color = Color.Gray,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }


                            }
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
                                                Firebase.auth.signOut()
                                                expanded = false

                                                navController.navigate(Destination.LOGIN.route) {
                                                    popUpTo(0) { inclusive = true }
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }

                    },
                    navigationIcon = {
                        if(currentRoute == Destination.FILTERED_POSTS.route ||
                            currentRoute == Destination.CHAT_DETAIL.route){
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
                    navController.navigate(Destination.NEW_POST.route)
                }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
        },


        bottomBar = {
            if (showBottomBar) {
                if (currentRoute != null) {
                    var cd = currentRoute
                    if(cd == Destination.POST_DETAIL.route){
                        cd = Destination.HOME.route
                    }
                    BottomBar(
                        navController = navController,
                        currentRoute = cd,
                    )
                }
            }
        }

    ) { innerPadding ->
        NavigationWrapper(
            loginViewModel = loginViewModel,
            postsViewModel = postsViewModel,
            navController= navController,
            modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        )

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomBar(
    navController: NavHostController,
    currentRoute: String
) {
    val navOptionsBuilder: NavOptionsBuilder.() -> Unit = remember(navController) {
        {
            popUpTo(navController.graph.id) {
                inclusive = false
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    // Solo los destinos que van en la BottomBar
    val bottomBarDestinations = listOf(
        Destination.HOME,
        Destination.FILTERS,
        Destination.CHATS,
        Destination.PROFILE
    )

    NavigationBar {
        bottomBarDestinations.forEach { destination ->
            NavigationBarItem(
                selected = currentRoute == destination.route,
                onClick = {
                    if (currentRoute != destination.route) {
                        navController.navigate(destination.route, builder = navOptionsBuilder)
                    }
                },
                icon = {
                    destination.icon?.let { Icon(it, contentDescription = destination.contentDescription) }
                },
                label = {
                    destination.labelRes?.let { Text(stringResource(it)) }
                },
                alwaysShowLabel = true // Opcional, true para que siempre se vea el texto
            )
        }
    }
}


//@Composable
//fun BottomBar(
//    navController: NavHostController,
//    currentRoute: String
//) {
//
//    val navOptionsBuilder: NavOptionsBuilder.() -> Unit = remember(navController) {
//        {
//            popUpTo(navController.graph.id) {
//                inclusive = false
//                saveState = true
//            }
//            launchSingleTop = true
//            restoreState = true
//        }
//    }
//
//
//
//    NavigationBar{
//        Destination.entries.forEach { destination ->
//            NavigationBarItem(
//                selected = currentRoute == destination.route,
//                onClick = {
//                    if (currentRoute != destination.route) {
//                        navController.navigate(
//                            route = destination.route,
//                            builder = navOptionsBuilder
//                        )
//                    }
//                },
//                icon = {
//                    destination.icon?.let {
//                        Icon(
//                            it,
//                            contentDescription = null
//                        )
//                    }
//                },
//                label = {
//                    destination.labelRes?.let { res ->
//                        Text(stringResource(res))
//                    }
//                }
//            )
//        }
//    }
//
////    NavigationBar {
////        Destination.entries.forEach { destination ->
////
////            NavigationBarItem(
////                selected = currentDestination.route == destination.route,
////                modifier = Modifier.clickable(
////                    interactionSource = noRippleInteractionSource,
////                    indication = null
////                ){},
////                onClick = {
////                    navController.navigate(destination.route) {
////                        launchSingleTop = true
////                        popUpTo(Destination.Home.route)
////                    }
////                },
////                icon = {
////                    destination.icon?.let {
////                        Icon(it, contentDescription = destination.contentDescription)
////                    }
////                },
////                label = { destination.label?.let { Text(it) } },
////                alwaysShowLabel = false,
////
////            )
////        }
////    }
//}






//@Preview(showBackground = true)
//@Composable
//fun NavigationBarScreenPreview(){
//    NavigationBarScreen(loginViewModel = LoginViewMode )
//}