package com.dp.radar.data.repositories

import com.dp.radar.data.NetworkMonitor
import com.dp.radar.data.datasources.db.UserDao
import com.dp.radar.data.datasources.db.toDomain
import com.dp.radar.data.datasources.db.toEntity
import com.dp.radar.data.datasources.remote.dto.LatLong
import com.dp.radar.domain.ApiResult
import com.dp.radar.domain.model.User
import com.dp.radar.domain.model.UserRequestDto
import com.dp.radar.domain.repositories.UserRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume

class FirebaseUserRepository
@Inject
constructor(
    private val database: FirebaseDatabase,
    private val networkMonitor: NetworkMonitor,
    private val userDao: UserDao,
    private val ioDispatcher: CoroutineDispatcher,
) : UserRepository {
    private val usersRef get() = database.getReference("users")

    override fun observeUsers(): Flow<List<User>> =
        userDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getUsers(userId: Long): ApiResult<List<User>> = getUsers()

    override suspend fun getUsers(): ApiResult<List<User>> = withContext(ioDispatcher) {
        if (!networkMonitor.isOnline()) return@withContext ApiResult.Error("Network unavailable")
        val result = suspendCancellableCoroutine<ApiResult<List<User>>> { cont ->
            val listener =
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val users = snapshot.children.mapNotNull { it.toUser() }
                        cont.resume(ApiResult.Success(users))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        cont.resume(ApiResult.Error(error.message))
                    }
                }
            usersRef.addListenerForSingleValueEvent(listener)
            cont.invokeOnCancellation { usersRef.removeEventListener(listener) }
        }
        if (result is ApiResult.Success) {
            userDao.replaceAll(result.data.map { it.toEntity() })
        }
        result
    }

    override suspend fun updateOnlineStatus(userId: Long, isOnline: Boolean): ApiResult<Unit> =
        withContext(ioDispatcher) {
            val result = suspendCancellableCoroutine<ApiResult<Unit>> { cont ->
                usersRef.child(userId.toString()).child("isOnline").setValue(isOnline)
                    .addOnSuccessListener { cont.resume(ApiResult.Success(Unit)) }
                    .addOnFailureListener { e -> cont.resume(ApiResult.Error(e.message ?: "Failed to update status")) }
            }
            if (result is ApiResult.Success) {
                userDao.updateOnlineStatus(userId, isOnline)
            }
            result
        }

    override suspend fun getChats(): ApiResult<List<User>> = getUsers()

    override suspend fun createUser(userRequestDto: UserRequestDto): ApiResult<User> =
        withContext(ioDispatcher) {
            if (!networkMonitor.isOnline()) return@withContext ApiResult.Error("Network unavailable")
            suspendCancellableCoroutine<ApiResult<User>> { cont ->
                val id = System.currentTimeMillis()
                val user =
                    User(
                        id = id,
                        username = userRequestDto.username,
                        email = userRequestDto.email,
                        avatarUrl = userRequestDto.avatarUrl,
                        isOnline = userRequestDto.isOnline,
                        latLong = userRequestDto.latLong,
                    )
                usersRef
                    .child(id.toString())
                    .setValue(user.toMap())
                    .addOnSuccessListener { cont.resume(ApiResult.Success(user)) }
                    .addOnFailureListener { e ->
                        cont.resume(ApiResult.Error(e.message ?: "Failed to create user"))
                    }
            }
        }

    private fun DataSnapshot.toUser(): User? =
        runCatching {
            User(
                id = child("id").getValue(Long::class.java)!!,
                username = child("username").getValue(String::class.java)!!,
                email = child("email").getValue(String::class.java)!!,
                avatarUrl = child("avatarUrl").getValue(String::class.java) ?: "https://i.pravatar.cc/150?img=1",
                isOnline = child("isOnline").getValue(Boolean::class.java) ?: false,
                latLong =
                LatLong(
                    lat = child("lat").getValue(Double::class.java) ?: 0.0,
                    lon = child("lon").getValue(Double::class.java) ?: 0.0,
                ),
            )
        }.getOrNull()

    private fun User.toMap(): Map<String, Any> =
        mapOf(
            "id" to id,
            "username" to username,
            "email" to email,
            "avatarUrl" to avatarUrl,
            "isOnline" to isOnline,
            "lat" to latLong.lat,
            "lon" to latLong.lon,
        )
}
