package com.dp.radar.data.datasources.remote.dto

import com.dp.radar.domain.model.User
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Long,
    val username: String,
    val email: String,
    val avatarUrl: String? = null,
    val latLong: LatLong,
    val isOnline: Boolean,
) {
    /**
     * Mapper function to convert this DTO into the clean Domain Model (Entity).
     */
    fun toDomain(): User {
        return User(
            id = this.id,
            username = this.username,
            email = this.email,
            avatarUrl = this.avatarUrl ?: ("https://i.pravatar.cc/150?img=" + this.id),
            isOnline = this.isOnline,
            latLong = this.latLong,
        )
    }
}
