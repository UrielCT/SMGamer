package com.smgamer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.smgamer.ui.viewmodels.PostsViewModel

@Composable
fun  NavigationWrapper(
    loginViewModel: LoginViewModel,
    postsViewModel: PostsViewModel,
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
                    postsViewModel = postsViewModel,
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
                EditProfileScreen(modifier = modifier, navBack = {
                    navController.popBackStack()
                })
            }

            composable(Destination.NEW_POST.route) {
                NewPostScreen(
                    modifier = modifier,
                    postsViewModel = postsViewModel,
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

            composable(Destination.PROFILE.route) {
                UserProfileScreen(
                    modifier = modifier,
                    navBack = { navController.popBackStack() },
                    navToChatDetail = {
                        navController.navigate(Destination.CHAT_DETAIL.route) {
                            launchSingleTop = true
                        }
                    },
                    isMyProfile = true,
                    isMyUser = true,
                    navToEditProfile = {
                        navController.navigate(Destination.EDIT_PROFILE.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(Destination.USER_PROFILE.route) {
                UserProfileScreen(
                    modifier = modifier,
                    navBack = { navController.popBackStack() },
                    navToChatDetail = {
                        navController.navigate(Destination.CHAT_DETAIL.route) {
                            launchSingleTop = true
                        }
                    },
                    isMyProfile = false,
                    isMyUser = true, // esto seria la id del usuario para validar si esla mio o no
                    navToEditProfile = {
                        navController.navigate(Destination.EDIT_PROFILE.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }

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
                    navToUserProfile = {
                        navController.navigate(Destination.USER_PROFILE.route) {
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
                    loginViewModel = loginViewModel,
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