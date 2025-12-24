package com.dp.radar.com.dp.radar.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.dp.radar.R
import com.dp.radar.com.dp.radar.ui.composable.LocationFetchScreen
import com.dp.radar.ui.composable.login.LoginScreen
import com.dp.radar.ui.composable.login.LoginSuccessScreen
import com.dp.radar.ui.navigation.RadarScreen
import com.dp.radar.ui.viewmodel.RadarViewModel

@Composable
fun LoginFlow(radarViewModel: RadarViewModel) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorResource(R.color.primary_background)
    ) {
        val navController = rememberNavController()
        NavHost(
            navController, startDestination = RadarScreen.LoginScreen,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween())
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween())
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween())
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween())
            },
            modifier = Modifier.padding(16.dp),
        ) {

            composable<RadarScreen.LoginScreen> {
                LoginScreen(
                    onLoginSuccess = { name, email ->
                        navController.navigate(RadarScreen.LocationFetchScreen(name, email))
                    }
                )
            }

            composable<RadarScreen.LoginSuccessScreen> {
                LoginSuccessScreen(goToHome = {
                    radarViewModel.updateLoginState(true)
                })
            }

            composable<RadarScreen.LocationFetchScreen> { backStackEntry ->
                val route = backStackEntry.toRoute<RadarScreen.LocationFetchScreen>()
                LocationFetchScreen(
                    username = route.name,
                    email = route.email,
                    goToSuccessScreen = {
                        navController.navigate(RadarScreen.LoginSuccessScreen)
                    })
            }
        }
    }
}