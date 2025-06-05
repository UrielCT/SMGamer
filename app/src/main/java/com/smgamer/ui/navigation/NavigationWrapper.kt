package com.smgamer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smgamer.ui.screens.login.LoginScreen
import com.smgamer.ui.screens.register.RegisterScreen

@Composable
fun  NavigationWrapper(){

    val navController = rememberNavController()

    NavHost(navController=navController,startDestination= Login){

        composable<Login> {
            LoginScreen(
                navToRegister = {navController.navigate(Register)}
            )
        }

        composable<Register> {
            RegisterScreen(
                navBack = {navController.popBackStack() },
                navToHome = {}
            )
        }
    }
}