package com.dp.radar.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dp.radar.domain.ApiResult
import com.dp.radar.domain.model.Chat
import com.dp.radar.domain.model.MessageType
import com.dp.radar.domain.repositories.IChatRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

class SendPendingMessagesWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WorkerEntryPoint {
        fun chatRepository(): IChatRepository
    }

    override suspend fun doWork(): Result {
        val chatRepository = EntryPointAccessors
            .fromApplication(applicationContext, WorkerEntryPoint::class.java)
            .chatRepository()

        val pending = chatRepository.getAllPendingMessages()
        var allSucceeded = true

        pending.forEach { pendingMessage ->
            val chat = Chat(
                chatId = pendingMessage.localId,
                message = pendingMessage.message,
                timestamp = pendingMessage.timestamp,
                messageType = MessageType.SENT,
            )
            val result = chatRepository.sendMessage(
                senderId = pendingMessage.senderId,
                receiverId = pendingMessage.receiverId,
                chat = chat,
            )
            if (result is ApiResult.Success) {
                chatRepository.deletePendingMessage(pendingMessage.localId)
            } else {
                allSucceeded = false
            }
        }

        return if (allSucceeded) Result.success() else Result.retry()
    }

    companion object {
        const val WORK_NAME = "send_pending_messages"
    }
}