package com.smgamer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.smgamer.ui.viewmodels.LoginViewModel
import com.smgamer.ui.screens.navigationBar.NavigationBarScreen
import com.smgamer.ui.theme.SMGamerTheme
import com.smgamer.ui.viewmodels.PostsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val loginViewModel: LoginViewModel by viewModels()
    private val postsViewModel: PostsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SMGamerTheme {
                NavigationBarScreen(
                    loginViewModel = loginViewModel,
                    postsViewModel = postsViewModel
                )
            }
        }
    }
}

