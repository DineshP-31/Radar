package com.dp.radar.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dp.radar.com.dp.radar.ui.Study
import com.dp.radar.com.dp.radar.ui.navigation.LoginFlow
import com.dp.radar.ui.navigation.MainFLow
import com.dp.radar.ui.theme.RadarTheme
import com.dp.radar.ui.viewmodel.RadarViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val radarViewModel: RadarViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val study = Study().launchTest()
            val isLoggedIn by radarViewModel.isLoggedIn.collectAsStateWithLifecycle()

            RadarTheme {
                StartFlow(isLoggedIn)
            }
        }
    }

    @Composable
    private fun StartFlow(isLoggedIn: Boolean) {
        if (isLoggedIn) {
            MainFLow(radarViewModel = radarViewModel)
        } else {
            LoginFlow(radarViewModel = radarViewModel)
        }
    }
}

