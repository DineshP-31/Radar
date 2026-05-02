package com.dp.radar.domain

import com.dp.radar.domain.model.Message
import com.dp.radar.domain.repositories.IChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveMessagesUseCase @Inject constructor(
    private val repository: IChatRepository,
) {
    operator fun invoke(senderId: Long, receiverId: Long): Flow<List<Message>> =
        repository.observeMessages(senderId, receiverId)
}