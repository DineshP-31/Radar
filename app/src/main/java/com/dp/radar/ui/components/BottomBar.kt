package com.dp.radar.ui.components

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.dp.radar.R
import com.dp.radar.ui.navigation.RadarScreen

@Composable
fun BottomBar(
    route: RadarScreen,
    onClick: (RadarScreen) -> Unit
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Chat,
        BottomNavItem.Settings,
    )

    NavigationBar {
        Log.e("ROUTE:", route.toString())

        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(item.drawableRes),
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        item.label,
                        fontSize = 14.sp
                    )
                },
                selected = route == item.route,
                onClick = {
                    onClick(item.route)
                }
            )
        }
    }
}

sealed class BottomNavItem(
    val route: RadarScreen,
    val label: String,
    @DrawableRes val drawableRes: Int
) {
    object Home : BottomNavItem(RadarScreen.HomeScreen, "Home", R.drawable.ic_home)
    object Chat : BottomNavItem(RadarScreen.ChatScreen, "Chat", R.drawable.ic_chat)
    object Settings : BottomNavItem(RadarScreen.SettingsScreen, "Settings", R.drawable.ic_settings)
}
