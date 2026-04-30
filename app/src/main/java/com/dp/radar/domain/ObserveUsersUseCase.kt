package com.dp.radar.domain

import com.dp.radar.domain.model.User
import com.dp.radar.domain.repositories.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveUsersUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(): Flow<List<User>> = repository.observeUsers()
}