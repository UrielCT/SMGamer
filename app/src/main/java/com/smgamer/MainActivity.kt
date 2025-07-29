package com.smgamer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.smgamer.ui.navigation.NavigationWrapper
import com.smgamer.ui.screens.home.HomeScreen
import com.smgamer.ui.screens.login.LoginViewModel
import com.smgamer.ui.screens.navigationBar.NavigationBarScreen
import com.smgamer.ui.theme.SMGamerTheme

class MainActivity : ComponentActivity() {

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SMGamerTheme {
                //LoginScreen({},{},{})
                //RegisterScreen({},{})
                //HomeScreen()
                //FiltersScreen()
                //ChatsScreen()
                //ProfileScreen()
                //EditProfileScreen
                //UserChatScreen()
                //FilteredPostsScreen()
                //PostDetailScreen()
                //CreatePostScreen()
                //UserProfileScreen
                //NavigationWrapper(loginViewModel)
                //NavigationWrapper()
                NavigationBarScreen()
            }
        }
    }
}

