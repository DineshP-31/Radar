package com.dp.radar.domain.repositories

import com.dp.radar.domain.ApiResult
import com.dp.radar.domain.model.Chat
import com.dp.radar.domain.model.Message
import com.dp.radar.domain.model.PendingMessage
import kotlinx.coroutines.flow.Flow

interface IChatRepository {
    suspend fun sendMessage(senderId: Long, receiverId: Long, chat: Chat): ApiResult<Unit>
    fun observeMessages(senderId: Long, receiverId: Long): Flow<List<Message>>
    fun observePendingMessages(senderId: Long, receiverId: Long): Flow<List<PendingMessage>>
    suspend fun getAllPendingMessages(): List<PendingMessage>
    suspend fun deletePendingMessage(localId: String)
}