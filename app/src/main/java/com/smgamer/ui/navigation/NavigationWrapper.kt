package com.smgamer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.smgamer.ui.screens.chatdetail.ChatDetailScreen
import com.smgamer.ui.screens.chats.ChatsScreen
import com.smgamer.ui.screens.editprofile.EditProfileScreen
import com.smgamer.ui.screens.filteredposts.FilteredPostsScreen
import com.smgamer.ui.screens.filters.FiltersScreen
import com.smgamer.ui.screens.home.HomeScreen
import com.smgamer.ui.screens.login.LoginScreen
import com.smgamer.ui.viewmodels.LoginViewModel
import com.smgamer.ui.screens.newpost.NewPostScreen
import com.smgamer.ui.screens.postdetail.PostDetailScreen
import com.smgamer.ui.screens.profile.ProfileScreen
import com.smgamer.ui.screens.register.RegisterScreen
import com.smgamer.ui.screens.userprofile.UserProfileScreen

@Composable
fun  NavigationWrapper(
    loginViewModel: LoginViewModel,
    navController: NavHostController,
    modifier: Modifier
){

    val auth = Firebase.auth
    val startDestination = if (auth.currentUser != null) {
        Destination.Home.route
    } else {
        Destination.Login.route
    }

    NavHost(
        navController = navController,
        startDestination= startDestination
    ){

        composable(Destination.Home.route){
            HomeScreen(modifier,
                navToPostDetail = {
                    navController.navigate(Destination.PostDetail.route)
                }
            )
        }
        composable(Destination.Filters.route){
            FiltersScreen(modifier,
                navToFilteredPosts = {
                    navController.navigate(Destination.FilteredPosts.route)
                }
            )
        }
        composable(Destination.Chats.route){
            ChatsScreen(modifier,
                navToChatDetail = { navController.navigate(Destination.ChatDetail.route) }
            )
        }
        composable(Destination.Profile.route){
            ProfileScreen(modifier,
                navToEditProfile = {
                    navController.navigate(Destination.EditProfile.route)
                }
            )
        }

        composable (Destination.EditProfile.route) {
            EditProfileScreen(modifier = modifier, navBack = {
                navController.popBackStack()
            })
        }

        composable (Destination.NewPost.route) {
            NewPostScreen (modifier = modifier, navBack = {
                navController.popBackStack()
            })
        }

        composable (Destination.UserProfile.route) {
            UserProfileScreen(modifier = modifier, navBack = {
                navController.popBackStack()
            })
        }

        composable (Destination.FilteredPosts.route) {
            FilteredPostsScreen(modifier = modifier,
                navToPostDetail = {
                    navController.navigate(Destination.PostDetail.route)
                }
            )
        }

        composable (Destination.PostDetail.route) {
            PostDetailScreen(modifier = modifier,
                navBack = { navController.popBackStack() },
                navToUserProfile = { navController.navigate(Destination.UserProfile.route) },
                navToChatDetail = {navController.navigate(Destination.ChatDetail.route) }
            )
        }


        composable (Destination.ChatDetail.route) {
            ChatDetailScreen(modifier = modifier)
        }

        composable(Destination.Login.route) {
            LoginScreen(
                modifier = modifier,
                loginViewModel= loginViewModel,
                navToHome = {
                    navController.navigate(Destination.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                navToRegister = { navController.navigate(Destination.Register.route) },
            )
        }

        composable (Destination.Register.route) {
            RegisterScreen(
                modifier = modifier,
                loginViewModel = loginViewModel,
                navBack = {navController.popBackStack() },
                navToHome = {
                    navController.navigate(Destination.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}