package com.smgamer.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.smgamer.ui.screens.chatdetail.ChatDetailScreen
import com.smgamer.ui.screens.chats.ChatsScreen
import com.smgamer.ui.screens.editprofile.EditProfileScreen
import com.smgamer.ui.screens.filteredposts.FilteredPostsScreen
import com.smgamer.ui.screens.filters.FiltersScreen
import com.smgamer.ui.screens.home.HomeScreen
import com.smgamer.ui.screens.login.LoginScreen
import com.smgamer.ui.screens.newpost.NewPostScreen
import com.smgamer.ui.screens.postdetail.PostDetailScreen
import com.smgamer.ui.screens.register.RegisterScreen
import com.smgamer.ui.screens.userprofile.UserProfileScreen

@Composable
fun  NavigationWrapper(
    navController: NavHostController,
    innerPadding: PaddingValues,
    modifier: Modifier
){

//    val auth = Firebase.auth
//    val startDestination = if (auth.currentUser != null) {
//        Destination.Home.route
//    } else {
//        Destination.Login.route
//    }

    val auth = Firebase.auth
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(auth.currentUser) {
        startDestination = if (auth.currentUser != null) {
            Destination.HOME.route
        } else {
            Destination.LOGIN.route
        }

    }

    if (startDestination != null) {

        NavHost(
            navController = navController,
            startDestination = startDestination!!
        ) {

            composable(Destination.HOME.route) {
                HomeScreen(
                    modifier = modifier,
                    navToPostDetail = { postId ->
                        navController.navigate("${Destination.POST_DETAIL.route}/${postId}") {
                            launchSingleTop = true
                        }
                    },
                    navToLogin = {
                        navController.navigate(Destination.LOGIN.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Destination.FILTERS.route) {
                FiltersScreen(
                    modifier = modifier.padding(innerPadding),
                    navToFilteredPosts = { category ->
                        navController.navigate("${Destination.FILTERED_POSTS.route}/$category") {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Destination.CHATS.route) {
                ChatsScreen(modifier.padding(innerPadding),
                    navToChatDetail = {/* chatId, senderId, receiverId */ otherId ->
                        //navController.navigate("${ Destination.CHAT_DETAIL.route }/$chatId/$senderId/$receiverId") {
                        navController.navigate("${ Destination.CHAT_DETAIL.route }/$otherId") {
                            launchSingleTop = true
                        }
                    }
                )
            }


            composable(Destination.EDIT_PROFILE.route) {
                EditProfileScreen(
                    modifier = modifier.padding(innerPadding),
                    navBack = { navController.popBackStack() }
                )
            }

            composable(Destination.NEW_POST.route) {
                NewPostScreen(
                    modifier = modifier.padding(innerPadding),
                    navBack = {
                        navController.popBackStack()
                    })
            }


            composable(Destination.PROFILE.route) {
                UserProfileScreen(
                    modifier = modifier.padding(innerPadding),
                    isProfile = true,
                    navBack = { navController.popBackStack() },
                    navToChatDetail = { otherId ->
                        navController.navigate("${Destination.CHAT_DETAIL.route}/$otherId")
                    },
                    navToEditProfile = { navController.navigate(Destination.EDIT_PROFILE.route) }
                )
            }

            // perfil del usuario, mio o de otro, sin el bottom bar
            composable(
                route = "${Destination.USER_PROFILE.route}/{userId}",
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId")
                UserProfileScreen(
                    modifier = modifier.padding(innerPadding),
                    isProfile = false,
                    userId = userId,
                    navBack = { navController.popBackStack() },
                    navToChatDetail = {otherId -> navController.navigate("${Destination.CHAT_DETAIL.route}/$otherId") },
                    navToEditProfile = { navController.navigate(Destination.EDIT_PROFILE.route) }
                )
            }


            composable(
                route = "${Destination.FILTERED_POSTS.route}/{category}",
                arguments = listOf(navArgument("category") { type = NavType.StringType })
            ) { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: ""
                FilteredPostsScreen(
                    modifier = modifier.padding(innerPadding),
                    category = category,
                    navToPostDetail = { postId ->
                        navController.navigate("${Destination.POST_DETAIL.route}/$postId") {
                            launchSingleTop = true
                        }
                    },
                )
            }


            composable(
                route = "${Destination.POST_DETAIL.route}/{postId}",
                arguments = listOf(navArgument("postId") { type = NavType.StringType })
            ) { backStackEntry ->
                val postId = backStackEntry.arguments?.getString("postId") ?: ""
                PostDetailScreen(
                    modifier = modifier.padding(innerPadding),
                    postId = postId,
                    navBack = { navController.popBackStack() },
                    navToUserProfile = { userId ->
                        navController.navigate("${Destination.USER_PROFILE.route}/$userId") {
                            launchSingleTop = true
                        }
                    },
                )
            }


            composable(
                //route = "${Destination.CHAT_DETAIL.route}/{chatId}/{senderId}/{receiverId}",
                route = "${Destination.CHAT_DETAIL.route}/{otherId}",
                arguments = listOf(
                    navArgument("otherId") { type = NavType.StringType },
                    //navArgument("chatId") { type = NavType.StringType },
                    //navArgument("senderId") { type = NavType.StringType },
                    //navArgument("receiverId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val otherId = backStackEntry.arguments?.getString("otherId") ?: ""
                //val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
                //val senderId = backStackEntry.arguments?.getString("senderId") ?: ""
                //val receiverId = backStackEntry.arguments?.getString("receiverId") ?: ""

                ChatDetailScreen(
                    modifier = modifier.padding(innerPadding),
                    otherId = otherId,
                    navBack = {
                        navController.popBackStack()
                    }
                    //chatId = chatId,
                    //senderId = senderId,
                    //receiverId = receiverId
                )
            }


            composable(Destination.LOGIN.route) {
                LoginScreen(
                    modifier = modifier.padding(innerPadding),
                    navToHome = {
                        navController.navigate(Destination.HOME.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            launchSingleTop = true
                        }

                    },
                    navToRegister = {
                        navController.navigate(Destination.REGISTER.route) {
                            launchSingleTop = true
                        }
                    },
                )
            }

            composable(Destination.REGISTER.route) {
                RegisterScreen(
                    modifier = modifier.padding(innerPadding),
                    navBack = { navController.popBackStack() },
                    navToHome = {
                        navController.navigate(Destination.HOME.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            launchSingleTop = true
                        }

                    }
                )
            }
        }
    }
}