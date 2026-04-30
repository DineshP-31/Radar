package com.dp.radar.domain

import com.dp.radar.domain.model.User
import com.dp.radar.domain.repositories.UserRepository
import javax.inject.Inject

/**
 * Retrieves a list of users from the repository.
 * This is the only entry point into the Domain layer for fetching user data.
 */
class GetUsersUseCase @Inject constructor(
    private val repository: UserRepository // Use the interface (Abstraction)
) {
    suspend operator fun invoke(): ApiResult<List<User>> {
        return repository.getUsers()
    }
}
