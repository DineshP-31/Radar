package com.dp.radar.com.dp.radar.ui

import com.dp.radar.com.dp.radar.domain.model.User

sealed class ChatListIntent {
    object LoadChats : ChatListIntent()
    object RetryLoad : ChatListIntent()
}

// Represents the state of the UI
data class ChatListState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) {
    companion object {
        val Initial = UserListState(isLoading = true)
    }
}