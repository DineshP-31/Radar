package com.dp.radar.com.dp.radar.domain

import com.dp.radar.com.dp.radar.domain.model.User
import com.dp.radar.com.dp.radar.domain.repositories.UserRepository
import javax.inject.Inject

class GetUserChatUseCase @Inject constructor(
    private val repository: UserRepository // Use the interface (Abstraction)
) {
    suspend operator fun invoke(): ApiResult<List<User>> {
        return repository.getChats()
    }
}