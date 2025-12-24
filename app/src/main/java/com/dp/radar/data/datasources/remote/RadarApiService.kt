package com.dp.radar.com.dp.radar.data.datasources.remote

import com.dp.radar.com.dp.radar.domain.model.User
import com.dp.radar.com.dp.radar.domain.model.UserRequestDto
import com.dp.radar.data.datasources.remote.dto.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RadarApiService {

    @GET("users")
    suspend fun getUsers(
        @Query("userId") userId: Long
    ): Response<List<UserDto>>

    @GET("users")
    suspend fun getUsers(
    ): Response<List<UserDto>>

    @GET("chat")
    suspend fun getUserChat(): Response<List<UserDto>>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") userId: Int): UserDto

    @POST("users")
    suspend fun createUser(@Body userRequestDto: UserRequestDto): Response<UserDto>
}