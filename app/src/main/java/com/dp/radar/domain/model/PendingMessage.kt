package com.dp.radar.domain.model

data class PendingMessage(
    val localId: String,
    val senderId: Long,
    val receiverId: Long,
    val message: String,
    val timestamp: Long,
)