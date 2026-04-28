package com.dp.radar.domain.login

import com.dp.radar.domain.repositories.ILoginRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserIdUseCase @Inject constructor(
    private val repository: ILoginRepository
) {
    operator fun invoke(): Flow<Long> = repository.userId
}
