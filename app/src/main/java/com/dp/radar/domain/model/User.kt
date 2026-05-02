package com.dp.radar.domain.model

import com.dp.radar.data.datasources.remote.dto.LatLong
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long,
    val username: String,
    val email: String,
    val avatarUrl: String = "https://i.pravatar.cc/150?img=1",
    val isOnline: Boolean = true,
    val latLong: LatLong,
)

@Serializable
data class UserRequestDto(
    val username: String,
    val email: String,
    val avatarUrl: String = "https://i.pravatar.cc/150?img=1",
    val isOnline: Boolean = true,
    val latLong: LatLong,
)

@Serializable
data class Chat(
    val chatId: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val messageType: MessageType = MessageType.RECEIVED,
    val isPending: Boolean = false,
)

@Serializable
data class Message(
    val messageId: String? = null,
    val message: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val messageType: MessageType = MessageType.RECEIVED,
    val senderId: Long? = null,
    val receiverId: Long? = null,
)

@Serializable
enum class MessageType {
    SENT,
    RECEIVED,
}
