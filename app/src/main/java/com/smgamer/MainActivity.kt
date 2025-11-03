package com.smgamer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration
import com.smgamer.ui.main.MainViewModel
import com.smgamer.ui.screens.navigationBar.NavigationBarScreen
import com.smgamer.ui.theme.LocalFontScale
import com.smgamer.ui.theme.LocalPaddingScale
import com.smgamer.ui.theme.SMGamerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

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
                    NavigationBarScreen()
                }

            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.setOnline()
    }

    override fun onStop() {
        super.onStop()
        viewModel.setOffline()
    }
}