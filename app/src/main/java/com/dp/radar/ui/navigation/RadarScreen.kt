package com.dp.radar.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface RadarScreen {

    @Serializable
    data object LoginScreen : RadarScreen

    @Serializable
    data object LoginSuccessScreen : RadarScreen

    @Serializable
    data class LocationFetchScreen(val name: String, val email: String) : RadarScreen

    @Serializable
    data object HomeScreen : RadarScreen

    @Serializable
    data class ChatDetailScreen(val userId: Long, val userName: String) : RadarScreen

    @Serializable
    data object ChatScreen : RadarScreen

    @Serializable
    data object SettingsScreen : RadarScreen
}