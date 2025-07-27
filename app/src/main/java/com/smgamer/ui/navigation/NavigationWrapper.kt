package com.smgamer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smgamer.ui.screens.login.LoginScreen
import com.smgamer.ui.screens.login.LoginViewModel
import com.smgamer.ui.screens.register.RegisterScreen

@Composable
fun  NavigationWrapper(
    loginViewModel: LoginViewModel
){

    val navController = rememberNavController()

    NavHost(navController=navController,startDestination= Login){

        composable<Login> {
            LoginScreen(
                loginViewModel= loginViewModel,
                navToHome = {},
                navToRegister = {navController.navigate(Register)}
            )
        }

        composable<Register> {
            RegisterScreen(
                loginViewModel = loginViewModel,
                navBack = {navController.popBackStack() },
                navToHome = {}
            )
        }
    }
}