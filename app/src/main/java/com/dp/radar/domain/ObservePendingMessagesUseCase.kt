package com.dp.radar.domain

import com.dp.radar.domain.model.PendingMessage
import com.dp.radar.domain.repositories.IChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePendingMessagesUseCase @Inject constructor(
    private val repository: IChatRepository,
) {
    operator fun invoke(senderId: Long, receiverId: Long): Flow<List<PendingMessage>> =
        repository.observePendingMessages(senderId, receiverId)
}