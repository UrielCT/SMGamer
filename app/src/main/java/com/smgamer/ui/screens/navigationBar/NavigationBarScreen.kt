package com.smgamer.ui.screens.navigationBar

import android.graphics.Rect
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.smgamer.R
import com.smgamer.ui.navigation.Destination
import com.smgamer.ui.navigation.NavigationWrapper
import com.smgamer.ui.screens.editprofile.EditProfileViewModel
import com.smgamer.ui.screens.login.LoginViewModel
import com.smgamer.ui.screens.register.RegisterViewModel
import com.smgamer.ui.screens.userprofile.UserProfileViewModel
import com.smgamer.ui.viewmodels.PostsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationBarScreen(
    postsViewModel: PostsViewModel,
    registerViewModel: RegisterViewModel,
    userProfileViewModel: UserProfileViewModel,
    editProfileViewModel: EditProfileViewModel,
    loginViewModel: LoginViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    var expanded by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Estado para modo búsqueda
    var isSearching by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val showBottomBar = currentRoute in listOf(
        Destination.HOME.route,
        Destination.FILTERS.route,
        Destination.PROFILE.route,
        Destination.CHATS.route,
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
                        if (isSearching) {
                            SearchBarWithFocus(
                                searchQuery = searchQuery,
                                onSearchQueryChange = { searchQuery = it },
                                onCloseSearch = {
                                    isSearching = false
                                    searchQuery = ""
                                }
                            )
                        }


                        if(currentRoute == Destination.CHATS.route){
                            Text(stringResource(Destination.CHATS.labelRes!!) )
                        }else if(currentRoute == Destination.FILTERED_POSTS.route){
                            Text(stringResource(Destination.FILTERS.labelRes!!))
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
                                    verticalArrangement = Arrangement.Center
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


                                    //
                                    // TODO: boton de cerra sesion
                                    //
                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Cerrar sesión") },
                                            onClick = {
                                                expanded = false

                                                loginViewModel.logout(
                                                    onSuccess = {
                                                        navController.navigate(Destination.LOGIN.route) {
                                                            popUpTo(0) { inclusive = true }
                                                        }
                                                    },
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

                    },
                    navigationIcon = {
                        if(currentRoute == Destination.FILTERED_POSTS.route ||
                            currentRoute == Destination.CHAT_DETAIL.route){
                            IconButton(onClick = { navController.popBackStack() }
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
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
            registerViewModel = registerViewModel,
            userProfileViewModel = userProfileViewModel,
            editProfileViewModel = editProfileViewModel,
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
fun SearchBarWithFocus(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onCloseSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }
    var isKeyboardVisible by remember { mutableStateOf(false) }

    DisposableEffect(view) {
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val screenHeight = view.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            isKeyboardVisible = keypadHeight > screenHeight * 0.15
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(listener)
        onDispose { view.viewTreeObserver.removeOnGlobalLayoutListener(listener) }
    }

    LaunchedEffect(isKeyboardVisible) {
        if (!isKeyboardVisible) {
            focusManager.clearFocus()
        }
    }

    // 🔹 Presionar atrás mientras el campo tiene foco
    BackHandler(enabled = isFocused) {
        focusManager.clearFocus()
        keyboardController?.hide()
    }

    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        placeholder = { Text("Buscar...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
        singleLine = true,
        leadingIcon = {
            IconButton(
                onClick = {
                    focusRequester.requestFocus()
                    keyboardController?.show()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Borrar texto",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                IconButton(onClick = {
                    onCloseSearch()
                    focusManager.clearFocus()
                    keyboardController?.hide()
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar búsqueda",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .focusRequester(focusRequester)
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
                if (!focusState.isFocused) keyboardController?.hide()
            },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}


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
                alwaysShowLabel = true
            )
        }
    }
}
