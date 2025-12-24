package com.dp.radar.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.dp.radar.com.dp.radar.ui.components.BottomBar
import com.dp.radar.com.dp.radar.ui.components.RadarTopBar
import com.dp.radar.com.dp.radar.ui.composable.ChatScreen
import com.dp.radar.ui.composable.home.ChatDetailScreen
import com.dp.radar.ui.composable.home.HomeScreen
import com.dp.radar.ui.viewmodel.RadarViewModel

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainFLow(radarViewModel: RadarViewModel) {
    val navController = rememberNavController()
    val bottomBarState by radarViewModel.bottomBarState.collectAsState()
    val topBarTitle by radarViewModel.topBarTitle.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val enableBackArrow = remember(navBackStackEntry) {
        navBackStackEntry?.destination?.route?.contains("ChatDetailScreen") ?: false
    }
    Scaffold(
        topBar = {
            RadarTopBar(
                title = topBarTitle,
                enableBackArrow = enableBackArrow,
                navController = navController,
            )
        },

        bottomBar = {
            if (bottomBarState.show) BottomBar(bottomBarState.route, onClick = { route ->
                navController.navigate(route)
            })
        },
        modifier = Modifier
            .fillMaxSize()
    ) { padding ->

        NavHost(
            navController, startDestination = RadarScreen.HomeScreen,
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
            modifier = Modifier.padding(padding),
        ) {

            composable<RadarScreen.HomeScreen> {
                HomeScreen(onItemClicked = { user ->
                    navController.navigate(
                        RadarScreen.ChatDetailScreen(
                            userId = user.id,
                            userName = user.username
                        )
                    ) {
                    }
                })
                radarViewModel.updateBottomBar(true, RadarScreen.HomeScreen)
                radarViewModel.updateTopBarTitle("Radar")
            }

            composable<RadarScreen.ChatDetailScreen> { backStackEntry ->
                val route = backStackEntry.toRoute<RadarScreen.ChatDetailScreen>()
                ChatDetailScreen(senderId = radarViewModel.getUserId(), receiverId = route.userId)
                radarViewModel.updateBottomBar(
                    false,
                    RadarScreen.ChatDetailScreen(route.userId, route.userName)
                )
                radarViewModel.updateTopBarTitle(route.userName)
            }

            composable<RadarScreen.ChatScreen> {
                ChatScreen(onItemClicked = { user ->
                    navController.navigate(
                        RadarScreen.ChatDetailScreen(
                            userId = user.id,
                            userName = user.username
                        )
                    ) {
                    }
                })
                radarViewModel.updateBottomBar(true, RadarScreen.ChatScreen)
                radarViewModel.updateTopBarTitle("Chats")
            }
        }
    }
}

