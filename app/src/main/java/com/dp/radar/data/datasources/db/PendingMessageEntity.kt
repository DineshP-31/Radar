package com.dp.radar.data.datasources.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dp.radar.domain.model.PendingMessage

@Entity(tableName = "pending_messages")
data class PendingMessageEntity(
    @PrimaryKey val localId: String,
    val senderId: Long,
    val receiverId: Long,
    val message: String,
    val timestamp: Long,
)

fun PendingMessageEntity.toDomain() = PendingMessage(
    localId = localId,
    senderId = senderId,
    receiverId = receiverId,
    message = message,
    timestamp = timestamp,
)