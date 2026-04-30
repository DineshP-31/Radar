package com.dp.radar.data.repositories

import com.dp.radar.data.NetworkMonitor
import com.dp.radar.data.datasources.db.UserDao
import com.dp.radar.data.datasources.db.toDomain
import com.dp.radar.data.datasources.db.toEntity
import com.dp.radar.data.datasources.remote.RadarApiService
import com.dp.radar.domain.ApiResult
import com.dp.radar.domain.model.User
import com.dp.radar.domain.model.UserRequestDto
import com.dp.radar.domain.repositories.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultUserRepository
@Inject
constructor(
    private val apiService: RadarApiService,
    private val networkMonitor: NetworkMonitor,
    private val userDao: UserDao,
) : UserRepository {
    override fun observeUsers(): Flow<List<User>> =
        userDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getUsers(userId: Long): ApiResult<List<User>> = getUsers()

    override suspend fun getUsers(): ApiResult<List<User>> {
        if (!networkMonitor.isOnline()) return ApiResult.Error("Network unavailable")
        return try {
            val response = apiService.getUsers()
            if (response.isSuccessful) {
                val users = response.body()!!.map { it.toDomain() }
                userDao.replaceAll(users.map { it.toEntity() })
                ApiResult.Success(users)
            } else {
                ApiResult.Error("Failed to load users")
            }
        } catch (e: Exception) {
            ApiResult.Error("Failed to load users: ${e.message}")
        }
    }

    override suspend fun getChats(): ApiResult<List<User>> {
        if (!networkMonitor.isOnline()) return ApiResult.Error("Network unavailable")
        return try {
            val response = apiService.getUserChat()
            if (response.isSuccessful) {
                val users = response.body()!!.map { it.toDomain() }
                ApiResult.Success(users)
            } else {
                ApiResult.Error("Failed to load users")
            }
        } catch (e: Exception) {
            ApiResult.Error("Failed to load users: ${e.message}")
        }
    }

    override suspend fun createUser(userRequestDto: UserRequestDto): ApiResult<User> {
        if (!networkMonitor.isOnline()) return ApiResult.Error("Network unavailable")
        return try {
            val response = apiService.createUser(userRequestDto)
            if (response.isSuccessful) {
                val user = response.body()!!.toDomain()
                ApiResult.Success(user)
            } else {
                ApiResult.Error("Failed to create user")
            }
        } catch (e: Exception) {
            ApiResult.Error("Failed to create user: ${e.message}")
        }
    }
}
