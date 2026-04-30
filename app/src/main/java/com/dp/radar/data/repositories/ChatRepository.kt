package com.dp.radar.data.repositories

import android.util.Log
import com.dp.radar.domain.model.Chat
import com.dp.radar.domain.model.Message
import com.dp.radar.domain.model.MessageType
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatRepository {
    val firebaseDb = FirebaseDatabase.getInstance().reference

    fun sendMessage(
        senderId: Long,
        receiverId: Long,
        chat: Chat,
    ) {
        val chatId = getChatId(senderId, receiverId)
        val messageId =
            firebaseDb
                .child("chats")
                .child(chatId)
                .child("messages")
                .push()
                .key ?: return
        val message =
            Message(
                messageId = messageId,
                messageType = MessageType.SENT,
                timestamp = System.currentTimeMillis(),
                message = chat.message,
                senderId = senderId,
                receiverId = receiverId,
            )
        firebaseDb
            .child("chats")
            .child(chatId)
            .child("messages")
            .child(messageId)
            .setValue(message)
    }

    fun observeMessage(
        senderId: Long,
        receiverId: Long,
        onResult: (List<Message>) -> Unit,
    ) {
        firebaseDb
            .child("chats")
            .child(getChatId(senderId, receiverId))
            .child("messages")
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val messages = mutableListOf<Message>()
                        for (child in snapshot.children) {
                            val msg = child.getValue(Message::class.java)
                            msg?.let { messages.add(it) }
                        }
                        onResult(messages)
                        Log.e("Message Data", messages.toString())
                    }

                    override fun onCancelled(error: DatabaseError) {}
                },
            )
    }

    private fun getChatId(
        user1: Long,
        user2: Long,
    ): String =
        if (user1 < user2) {
            "chat_${user1}_$user2"
        } else {
            "chat_${user2}_$user1"
        }
}
