package com.dp.radar.domain.repositories

import com.dp.radar.domain.ApiResult
import com.dp.radar.domain.model.User
import com.dp.radar.domain.model.UserRequestDto
import retrofit2.http.GET
import retrofit2.http.Query

interface UserRepository {
    @GET("users")
    suspend fun getUsers(
        @Query("userId") userId: Long
    ): ApiResult<List<User>>

    @GET("users")
    suspend fun getUsers(): ApiResult<List<User>>

    suspend fun getChats(): ApiResult<List<User>>

    suspend fun createUser(userRequestDto: UserRequestDto): ApiResult<User>
}
