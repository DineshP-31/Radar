package com.dp.radar.domain

import com.dp.radar.domain.model.Chat
import com.dp.radar.domain.repositories.IChatRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val repository: IChatRepository,
) {
    suspend operator fun invoke(senderId: Long, receiverId: Long, chat: Chat): ApiResult<Unit> =
        repository.sendMessage(senderId, receiverId, chat)
}