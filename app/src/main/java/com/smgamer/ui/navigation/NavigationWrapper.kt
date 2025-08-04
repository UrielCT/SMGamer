package com.smgamer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.smgamer.ui.screens.chats.ChatsScreen
import com.smgamer.ui.screens.editprofile.EditProfileScreen
import com.smgamer.ui.screens.filteredposts.FilteredPostsScreen
import com.smgamer.ui.screens.filters.FiltersScreen
import com.smgamer.ui.screens.home.HomeScreen
import com.smgamer.ui.screens.login.LoginScreen
import com.smgamer.ui.screens.newpost.NewPostScreen
import com.smgamer.ui.screens.postdetail.PostDetailScreen
import com.smgamer.ui.screens.profile.ProfileScreen
import com.smgamer.ui.screens.register.RegisterScreen
import com.smgamer.ui.screens.userprofile.UserProfileScreen

@Composable
fun  NavigationWrapper(
    //loginViewModel: LoginViewModel,
    navController: NavHostController,
    //currentDestination: Destination,
    modifier: Modifier
){

    NavHost(
        navController = navController,
        startDestination= Destination.Home.route
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
            ChatsScreen(modifier)
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
                navToUserProfile = {navController.navigate(Destination.UserProfile.route)}
            )
        }


        composable(Destination.Login.route) {
            LoginScreen(
                //loginViewModel= loginViewModel,
                navToHome = {
                    navController.navigate(Destination.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                navToRegister = { navController.navigate(Destination.Register.route) }
            )
        }

        composable (Destination.Register.route) {
            RegisterScreen(
                //loginViewModel = loginViewModel,
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