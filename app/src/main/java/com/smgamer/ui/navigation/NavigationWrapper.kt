package com.smgamer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smgamer.ui.screens.chats.ChatsScreen
import com.smgamer.ui.screens.filters.FiltersScreen
import com.smgamer.ui.screens.home.HomeScreen
import com.smgamer.ui.screens.login.LoginScreen
import com.smgamer.ui.screens.login.LoginViewModel
import com.smgamer.ui.screens.profile.ProfileScreen
import com.smgamer.ui.screens.register.RegisterScreen

@Composable
fun  NavigationWrapper(
    //loginViewModel: LoginViewModel,
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier = Modifier
){

    //val navController = rememberNavController()

    //var startDestination: Destination

    NavHost(navController = navController, startDestination= startDestination.route){

//        composable(route= Destination.HOME.route){
//            HomeScreen()
//        }
//        composable(route= Destination.FILTERS.route){
//            HomeScreen()
//        }
//        composable(route= Destination.CHATS.route){
//            HomeScreen()
//        }
//        composable(route= Destination.PROFILE.route){
//            HomeScreen()
//        }

        Destination.entries.forEach{ destination ->
            composable(destination.route) {
                when(destination){
                    Destination.HOME -> HomeScreen(modifier)
                    Destination.FILTERS -> FiltersScreen(modifier)
                    Destination.CHATS -> ChatsScreen(modifier)
                    Destination.PROFILE -> ProfileScreen(modifier)
                }
            }
        }


//        composable<Login> {
//            LoginScreen(
//                loginViewModel= loginViewModel,
//                navToHome = {},
//                navToRegister = {navController.navigate(Register)}
//            )
//        }

//        composable<Register> {
//            RegisterScreen(
//                loginViewModel = loginViewModel,
//                navBack = {navController.popBackStack() },
//                navToHome = {}
//            )
//        }
    }
}