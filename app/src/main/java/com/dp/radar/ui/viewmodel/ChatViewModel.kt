package com.dp.radar.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dp.radar.domain.ObserveMessagesUseCase
import com.dp.radar.domain.ObservePendingMessagesUseCase
import com.dp.radar.domain.SendMessageUseCase
import com.dp.radar.domain.model.Chat
import com.dp.radar.domain.model.MessageType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val sendMessageUseCase: SendMessageUseCase,
    private val observeMessagesUseCase: ObserveMessagesUseCase,
    private val observePendingMessagesUseCase: ObservePendingMessagesUseCase,
) : ViewModel() {

    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats

    private var observeJob: Job? = null

    fun sendMessage(senderId: Long, receiverId: Long, chat: Chat) {
        viewModelScope.launch {
            sendMessageUseCase(senderId, receiverId, chat)
        }
    }

    fun observeChats(senderId: Long, receiverId: Long) {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            combine(
                observeMessagesUseCase(senderId, receiverId),
                observePendingMessagesUseCase(senderId, receiverId),
            ) { messages, pendingMessages ->
                val firebaseChats = messages.map { msg ->
                    val type = if (msg.senderId == senderId) MessageType.SENT else MessageType.RECEIVED
                    Chat(
                        chatId = msg.messageId ?: "",
                        message = msg.message ?: "",
                        messageType = type,
                        timestamp = msg.timestamp,
                    )
                }
                val sentChatIds = firebaseChats.map { it.chatId }.toSet()
                val pendingChats = pendingMessages
                    .filter { it.localId !in sentChatIds }
                    .map { pending ->
                        Chat(
                            chatId = pending.localId,
                            message = pending.message,
                            messageType = MessageType.SENT,
                            timestamp = pending.timestamp,
                            isPending = true,
                        )
                    }
                (firebaseChats + pendingChats).sortedBy { it.timestamp }
            }.collect { _chats.value = it }
        }
    }
}