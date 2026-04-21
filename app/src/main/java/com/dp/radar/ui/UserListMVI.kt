package com.dp.radar.com.dp.radar.ui

import com.dp.radar.com.dp.radar.domain.model.User

sealed class UserListIntent {
    object LoadUsers : UserListIntent()
    object RetryLoad : UserListIntent()
}

data class UserListState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) {
    companion object {
        val Initial = UserListState(isLoading = false)
    }
}

data class SignUpState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    companion object {
        val Initial = SignUpState(isLoading = false)
    }
}