package com.dp.radar.com.dp.radar.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dp.radar.com.dp.radar.domain.model.Chat
import com.dp.radar.com.dp.radar.domain.model.MessageType
import com.dp.radar.data.repositories.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
) : ViewModel() {

    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats

    fun sendMessage(senderId: Long, receiverId: Long, chat: Chat) {
        viewModelScope.launch {
            chatRepository.sendMessage(senderId = senderId, receiverId, chat = chat)
        }
    }

    fun observeChats(senderId: Long, receiverId: Long) {
        viewModelScope.launch {
            chatRepository.observeMessage(
                senderId = senderId,
                receiverId = receiverId,
                onResult = { messages ->
                    val chatList = messages.map {
                        val messageType = if (it.senderId == senderId) {
                            MessageType.SENT
                        } else {
                            MessageType.RECEIVED
                        }
                        Chat(
                            chatId = it.messageId ?: "", message = it.message ?: "",
                            messageType = messageType, timestamp = it.timestamp
                        )
                    }
                    _chats.value = chatList
                })
        }
    }
}