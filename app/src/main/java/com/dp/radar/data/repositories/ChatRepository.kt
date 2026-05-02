package com.dp.radar.data.repositories

import com.dp.radar.data.NetworkMonitor
import com.dp.radar.data.datasources.db.PendingMessageDao
import com.dp.radar.data.datasources.db.PendingMessageEntity
import com.dp.radar.data.datasources.db.toDomain
import com.dp.radar.data.worker.SendPendingMessagesWorker
import com.dp.radar.domain.ApiResult
import com.dp.radar.domain.model.Chat
import com.dp.radar.domain.model.Message
import com.dp.radar.domain.model.MessageType
import com.dp.radar.domain.model.PendingMessage
import com.dp.radar.domain.repositories.IChatRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class ChatRepository @Inject constructor(
    private val networkMonitor: NetworkMonitor,
    private val pendingMessageDao: PendingMessageDao,
    private val workManager: WorkManager,
) : IChatRepository {

    private val firebaseDb = FirebaseDatabase.getInstance().reference

    override suspend fun sendMessage(
        senderId: Long,
        receiverId: Long,
        chat: Chat,
    ): ApiResult<Unit> {
        return if (networkMonitor.isOnline()) {
            sendToFirebase(senderId, receiverId, chat)
        } else {
            pendingMessageDao.insert(
                PendingMessageEntity(
                    localId = chat.chatId,
                    senderId = senderId,
                    receiverId = receiverId,
                    message = chat.message,
                    timestamp = chat.timestamp,
                )
            )
            enqueueSendPendingWork()
            ApiResult.Success(Unit)
        }
    }

    override fun observeMessages(senderId: Long, receiverId: Long): Flow<List<Message>> =
        callbackFlow {
            val ref = firebaseDb
                .child("chats")
                .child(getChatId(senderId, receiverId))
                .child("messages")
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val messages = snapshot.children.mapNotNull { it.getValue(Message::class.java) }
                    trySend(messages)
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            }
            ref.addValueEventListener(listener)
            awaitClose { ref.removeEventListener(listener) }
        }

    override fun observePendingMessages(
        senderId: Long,
        receiverId: Long,
    ): Flow<List<PendingMessage>> =
        pendingMessageDao.observeByConversation(senderId, receiverId)
            .map { entities -> entities.map { it.toDomain() } }

    override suspend fun getAllPendingMessages(): List<PendingMessage> =
        pendingMessageDao.getAll().map { it.toDomain() }

    override suspend fun deletePendingMessage(localId: String) {
        pendingMessageDao.deleteById(localId)
    }

    private suspend fun sendToFirebase(
        senderId: Long,
        receiverId: Long,
        chat: Chat,
    ): ApiResult<Unit> = suspendCancellableCoroutine { cont ->
        val chatId = getChatId(senderId, receiverId)
        val ref = firebaseDb.child("chats").child(chatId).child("messages")
        val messageId = ref.push().key
        if (messageId == null) {
            cont.resume(ApiResult.Error("Failed to generate message ID"))
            return@suspendCancellableCoroutine
        }
        val message = Message(
            messageId = messageId,
            messageType = MessageType.SENT,
            timestamp = chat.timestamp,
            message = chat.message,
            senderId = senderId,
            receiverId = receiverId,
        )
        ref.child(messageId).setValue(message)
            .addOnSuccessListener { cont.resume(ApiResult.Success(Unit)) }
            .addOnFailureListener { e -> cont.resume(ApiResult.Error(e.message ?: "Send failed")) }
    }

    private fun enqueueSendPendingWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val request = OneTimeWorkRequestBuilder<SendPendingMessagesWorker>()
            .setConstraints(constraints)
            .build()
        workManager.enqueueUniqueWork(
            SendPendingMessagesWorker.WORK_NAME,
            ExistingWorkPolicy.KEEP,
            request,
        )
    }

    private fun getChatId(user1: Long, user2: Long): String =
        if (user1 < user2) "chat_${user1}_$user2" else "chat_${user2}_$user1"
}