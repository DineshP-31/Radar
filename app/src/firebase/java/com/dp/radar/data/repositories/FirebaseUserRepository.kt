package com.dp.radar.data.repositories

import com.dp.radar.data.NetworkMonitor
import com.dp.radar.data.datasources.remote.dto.LatLong
import com.dp.radar.domain.ApiResult
import com.dp.radar.domain.model.User
import com.dp.radar.domain.model.UserRequestDto
import com.dp.radar.domain.repositories.UserRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class FirebaseUserRepository @Inject constructor(
    private val database: FirebaseDatabase,
    private val networkMonitor: NetworkMonitor,
) : UserRepository {

    private val usersRef get() = database.getReference("users")

    override suspend fun getUsers(userId: Long): ApiResult<List<User>> {
        if (!networkMonitor.isOnline()) return ApiResult.Error("Network unavailable")
        return getUsers()
    }

    override suspend fun getUsers(): ApiResult<List<User>> {
        if (!networkMonitor.isOnline()) return ApiResult.Error("Network unavailable")
        return suspendCancellableCoroutine { cont ->
            val listener = object : ValueEventListener {
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
    }

    override suspend fun getChats(): ApiResult<List<User>> {
        if (!networkMonitor.isOnline()) return ApiResult.Error("Network unavailable")
        return getUsers()
    }

    override suspend fun createUser(userRequestDto: UserRequestDto): ApiResult<User> {
        if (!networkMonitor.isOnline()) return ApiResult.Error("Network unavailable")
        return suspendCancellableCoroutine { cont ->
            val id = System.currentTimeMillis()
            val user = User(
                id = id,
                username = userRequestDto.username,
                email = userRequestDto.email,
                avatarUrl = userRequestDto.avatarUrl,
                isOnline = userRequestDto.isOnline,
                latLong = userRequestDto.latLong,
            )
            usersRef.child(id.toString()).setValue(user.toMap())
                .addOnSuccessListener { cont.resume(ApiResult.Success(user)) }
                .addOnFailureListener { e ->
                    cont.resume(ApiResult.Error(e.message ?: "Failed to create user"))
                }
        }
    }

    private fun DataSnapshot.toUser(): User? = runCatching {
        User(
            id = child("id").getValue(Long::class.java)!!,
            username = child("username").getValue(String::class.java)!!,
            email = child("email").getValue(String::class.java)!!,
            avatarUrl = child("avatarUrl").getValue(String::class.java) ?: "https://i.pravatar.cc/150?img=1",
            isOnline = child("isOnline").getValue(Boolean::class.java) ?: false,
            latLong = LatLong(
                lat = child("lat").getValue(Double::class.java) ?: 0.0,
                lon = child("lon").getValue(Double::class.java) ?: 0.0,
            ),
        )
    }.getOrNull()

    private fun User.toMap(): Map<String, Any> = mapOf(
        "id" to id,
        "username" to username,
        "email" to email,
        "avatarUrl" to avatarUrl,
        "isOnline" to isOnline,
        "lat" to latLong.lat,
        "lon" to latLong.lon,
    )
}