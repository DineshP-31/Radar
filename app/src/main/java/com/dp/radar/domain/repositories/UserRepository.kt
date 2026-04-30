package com.dp.radar.domain.repositories

import com.dp.radar.domain.ApiResult
import com.dp.radar.domain.model.User
import com.dp.radar.domain.model.UserRequestDto
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun observeUsers(): Flow<List<User>>
    suspend fun getUsers(userId: Long): ApiResult<List<User>>
    suspend fun getUsers(): ApiResult<List<User>>
    suspend fun getChats(): ApiResult<List<User>>
    suspend fun createUser(userRequestDto: UserRequestDto): ApiResult<User>
}