package com.dp.radar.domain.repositories

import kotlinx.coroutines.flow.Flow

interface ILoginRepository {
    val isLoggedIn: Flow<Boolean>
    val userId: Flow<Long>

    suspend fun saveEmail(email: String)
    suspend fun saveUserId(userId: Long)
    suspend fun clearEmail()
}
