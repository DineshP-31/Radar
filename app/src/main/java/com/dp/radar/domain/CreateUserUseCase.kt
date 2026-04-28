package com.dp.radar.domain

import com.dp.radar.domain.model.User
import com.dp.radar.domain.model.UserRequestDto
import com.dp.radar.domain.repositories.UserRepository
import javax.inject.Inject

class CreateUserUseCase @Inject constructor(
    private val repository: UserRepository // Use the interface (Abstraction)
) {
    suspend operator fun invoke(userRequestDto: UserRequestDto): ApiResult<User> {
        return repository.createUser(userRequestDto)
    }
}
