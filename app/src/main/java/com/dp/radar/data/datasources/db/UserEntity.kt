package com.dp.radar.data.datasources.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dp.radar.data.datasources.remote.dto.LatLong
import com.dp.radar.domain.model.User

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Long,
    val username: String,
    val email: String,
    val avatarUrl: String,
    val isOnline: Boolean,
    val lat: Double,
    val lon: Double,
)

fun UserEntity.toDomain() = User(
    id = id,
    username = username,
    email = email,
    avatarUrl = avatarUrl,
    isOnline = isOnline,
    latLong = LatLong(lat, lon),
)

fun User.toEntity() = UserEntity(
    id = id,
    username = username,
    email = email,
    avatarUrl = avatarUrl,
    isOnline = isOnline,
    lat = latLong.lat,
    lon = latLong.lon,
)