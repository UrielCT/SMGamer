package com.smgamer.ui.navigation

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
import com.smgamer.ui.screens.editprofile.EditProfileViewModel
import com.smgamer.ui.screens.filteredposts.FilteredPostsScreen
import com.smgamer.ui.screens.filters.FiltersScreen
import com.smgamer.ui.screens.home.HomeScreen
import com.smgamer.ui.screens.login.LoginScreen
import com.smgamer.ui.screens.login.LoginViewModel
import com.smgamer.ui.screens.newpost.NewPostScreen
import com.smgamer.ui.screens.newpost.NewPostViewModel
import com.smgamer.ui.screens.postdetail.PostDetailScreen
import com.smgamer.ui.screens.register.RegisterScreen
import com.smgamer.ui.screens.register.RegisterViewModel
import com.smgamer.ui.screens.userprofile.UserProfileScreen
import com.smgamer.ui.screens.userprofile.UserProfileViewModel

@Composable
fun  NavigationWrapper(
    loginViewModel: LoginViewModel,
    registerViewModel: RegisterViewModel,
    userProfileViewModel: UserProfileViewModel,
    editProfileViewModel: EditProfileViewModel,
    newPostViewModel: NewPostViewModel,
    navController: NavHostController,
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
                    navToPostDetail = {
                        navController.navigate(Destination.POST_DETAIL.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(Destination.FILTERS.route) {
                FiltersScreen(modifier,
                    navToFilteredPosts = {
                        navController.navigate(Destination.FILTERED_POSTS.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(Destination.CHATS.route) {
                ChatsScreen(modifier,
                    navToChatDetail = {
                        navController.navigate(Destination.CHAT_DETAIL.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }


            composable(Destination.EDIT_PROFILE.route) {
                EditProfileScreen(
                    modifier = modifier,
                    editProfileViewModel = editProfileViewModel,
                    navBack = { navController.popBackStack() }
                )
            }

            composable(Destination.NEW_POST.route) {
                NewPostScreen(
                    modifier = modifier,
                    newPostViewModel = newPostViewModel,
                    navBack = {
                        navController.popBackStack()
                    })
            }

//            composable(Destination.PROFILE.route) {
//                ProfileScreen(modifier,
//                    navToEditProfile = {
//                        navController.navigate(Destination.EDIT_PROFILE.route) {
//                            launchSingleTop = true
//                        }
//                    }
//                )
//            }

            // mi perfil con el bottom bar
//            composable(Destination.PROFILE.route) {
//                UserProfileScreen(
//                    modifier = modifier,
//                    userProfileViewModel=userProfileViewModel,
//                    navBack = { navController.popBackStack() },
//                    navToChatDetail = {
//                        navController.navigate(Destination.CHAT_DETAIL.route) {
//                            launchSingleTop = true
//                        }
//                    },
//                    isMyProfile = true,
//                    isMyUser = true,
//                    navToEditProfile = {
//                        navController.navigate(Destination.EDIT_PROFILE.route) {
//                            launchSingleTop = true
//                        }
//                    }
//                )
//            }
            composable(Destination.PROFILE.route) {
                UserProfileScreen(
                    modifier = modifier,
                    isProfile = true,
                    userProfileViewModel = userProfileViewModel,
                    navBack = { navController.popBackStack() },
                    navToChatDetail = { navController.navigate(Destination.CHAT_DETAIL.route) },
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
                    modifier = modifier,
                    isProfile = false,
                    userProfileViewModel = userProfileViewModel,
                    userId = userId,
                    navBack = { navController.popBackStack() },
                    navToChatDetail = { navController.navigate(Destination.CHAT_DETAIL.route) },
                    navToEditProfile = { navController.navigate(Destination.EDIT_PROFILE.route) }
                )
            }


//            composable(Destination.USER_PROFILE.route) {
//                UserProfileScreen(
//                    modifier = modifier,
//                    userProfileViewModel=userProfileViewModel,
//                    navBack = { navController.popBackStack() },
//                    navToChatDetail = {
//                        navController.navigate(Destination.CHAT_DETAIL.route) {
//                            launchSingleTop = true
//                        }
//                    },
//                    isMyProfile = false,
//                    isMyUser = false, // esto seria la id del usuario para validar si esla mio o no
//                    navToEditProfile = {
//                        navController.navigate(Destination.EDIT_PROFILE.route) {
//                            launchSingleTop = true
//                        }
//                    }
//                )
//            }

//            composable(
//                route = "${Destination.USER_PROFILE.route}/{userId}",
//                arguments = listOf(navArgument("userId") { type = NavType.StringType })
//            ) { backStackEntry ->
//                val userId = backStackEntry.arguments?.getString("userId")
//                UserProfileScreen(
//                    modifier = modifier,
//                    userProfileViewModel = userProfileViewModel,
//                    userId = userId,
//                    navBack = { navController.popBackStack() },
//                    navToChatDetail = {
//                        navController.navigate(Destination.CHAT_DETAIL.route) {
//                            launchSingleTop = true
//                        }
//                    },
//                    navToEditProfile = {
//                        navController.navigate(Destination.EDIT_PROFILE.route) {
//                            launchSingleTop = true
//                        }
//                    }
//                )
//            }


            composable(Destination.FILTERED_POSTS.route) {
                FilteredPostsScreen(modifier = modifier,
                    navToPostDetail = {
                        navController.navigate(Destination.POST_DETAIL.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Destination.POST_DETAIL.route) {
                PostDetailScreen(modifier = modifier,
                    navBack = { navController.popBackStack() },
                    navToUserProfile = { userId ->
                        navController.navigate("${Destination.USER_PROFILE.route}/$userId") {
                            launchSingleTop = true
                        }
                    },
                    navToChatDetail = {
                        navController.navigate(Destination.CHAT_DETAIL.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }


            composable(Destination.CHAT_DETAIL.route) {
                ChatDetailScreen(modifier = modifier)
            }

            composable(Destination.LOGIN.route) {
                LoginScreen(
                    modifier = modifier,
                    loginViewModel = loginViewModel,
                    navToHome = {
//                        navController.navigate(Destination.Home.route) {
//                            popUpTo(0) { inclusive = true }
//                        }
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
                    modifier = modifier,
                    registerViewModel = registerViewModel,
                    navBack = { navController.popBackStack() },
                    navToHome = {
//                        navController.navigate(Destination.Home.route) {
//                            popUpTo(0) { inclusive = true }
//                        }
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