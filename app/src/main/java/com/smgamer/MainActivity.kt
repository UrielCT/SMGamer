package com.smgamer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration
import com.smgamer.ui.screens.editprofile.EditProfileViewModel
import com.smgamer.ui.screens.login.LoginViewModel
import com.smgamer.ui.screens.navigationBar.NavigationBarScreen
import com.smgamer.ui.screens.register.RegisterViewModel
import com.smgamer.ui.screens.userprofile.UserProfileViewModel
import com.smgamer.ui.theme.LocalFontScale
import com.smgamer.ui.theme.LocalPaddingScale
import com.smgamer.ui.theme.SMGamerTheme
import com.smgamer.ui.viewmodels.PostsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val loginViewModel: LoginViewModel by viewModels()
    private val registerViewModel: RegisterViewModel by viewModels()
    private val postsViewModel: PostsViewModel by viewModels()
    private val userProfileViewModel: UserProfileViewModel by viewModels()
    private val editProfileViewModel: EditProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SMGamerTheme {

                val configuration = LocalConfiguration.current
                val screenWidth = configuration.screenWidthDp
                val screenHeight = configuration.screenHeightDp

                // Escalas adaptativas
                val fontScale = screenWidth / 411f
                val paddingScale = screenHeight / 891f

                CompositionLocalProvider(
                    LocalPaddingScale provides paddingScale,
                    LocalFontScale provides fontScale
                ) {
                    NavigationBarScreen(
                        loginViewModel = loginViewModel,
                        registerViewModel = registerViewModel,
                        userProfileViewModel = userProfileViewModel,
                        editProfileViewModel = editProfileViewModel,
                        postsViewModel = postsViewModel
                    )
                }

            }
        }
    }
}

