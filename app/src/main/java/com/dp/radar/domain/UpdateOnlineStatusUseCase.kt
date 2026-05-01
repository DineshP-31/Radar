package com.dp.radar.domain

import com.dp.radar.domain.repositories.UserRepository
import javax.inject.Inject

class UpdateOnlineStatusUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(userId: Long, isOnline: Boolean): ApiResult<Unit> =
        repository.updateOnlineStatus(userId, isOnline)
}
