package com.dp.radar.com.dp.radar.data.repositories

import com.dp.radar.com.dp.radar.data.NetworkMonitor
import com.dp.radar.com.dp.radar.data.datasources.remote.RadarApiService
import com.dp.radar.com.dp.radar.domain.ApiResult
import com.dp.radar.com.dp.radar.domain.model.User
import com.dp.radar.com.dp.radar.domain.model.UserRequestDto
import com.dp.radar.com.dp.radar.domain.repositories.UserRepository
import javax.inject.Inject

class DefaultUserRepository @Inject constructor(
    private val apiService: RadarApiService,
    private val networkMonitor: NetworkMonitor
) : UserRepository {

    override suspend fun getUsers(userId: Long): ApiResult<List<User>> {
        if (!networkMonitor.isOnline()) {
            return ApiResult.Error("Network unavailable")
        }
        return try {
            val response = apiService.getUsers(userId = userId)
            if (response.isSuccessful) {
                val userDto = response.body()
                val domainUsers = userDto!!.map { it.toDomain() }
                ApiResult.Success(domainUsers)

            } else {
                ApiResult.Error("Failed to load users")
            }
        } catch (e: Exception) {
            ApiResult.Error("Failed to load users:" + e.message)
        }
    }

    override suspend fun getUsers(): ApiResult<List<User>> {
        if (!networkMonitor.isOnline()) {
            return ApiResult.Error("Network unavailable")
        }
        return try {
            val response = apiService.getUsers()
            if (response.isSuccessful) {
                val userDto = response.body()
                val domainUsers = userDto!!.map { it.toDomain() }
                ApiResult.Success(domainUsers)

            } else {
                ApiResult.Error("Failed to load users")
            }
        } catch (e: Exception) {
            ApiResult.Error("Failed to load users:" + e.message)
        }
    }

    override suspend fun getChats(): ApiResult<List<User>> {
        if (!networkMonitor.isOnline()) {
            return ApiResult.Error("Network unavailable")
        }
        return try {
            val response = apiService.getUserChat()
            if (response.isSuccessful) {
                val userDto = response.body()
                val domainUsers = userDto!!.map { it.toDomain() }
                ApiResult.Success(domainUsers)

            } else {
                ApiResult.Error("Failed to load users")
            }
        } catch (e: Exception) {
            ApiResult.Error("Failed to load users:" + e.message)
        }
    }

    override suspend fun createUser(userRequestDto: UserRequestDto): ApiResult<User> {
        if (!networkMonitor.isOnline()) {
            return ApiResult.Error("Network unavailable")
        }
        return try {
            val response = apiService.createUser(userRequestDto)
            if (response.isSuccessful) {
                val userDto = response.body()
                val domainUsers = userDto!!.toDomain()
                ApiResult.Success(domainUsers)

            } else {
                ApiResult.Error("Failed to load users")
            }
        } catch (e: Exception) {
            ApiResult.Error("Failed to load users:" + e.message)
        }
    }
}