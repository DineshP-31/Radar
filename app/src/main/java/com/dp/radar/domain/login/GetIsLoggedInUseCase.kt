package com.dp.radar.domain.login

import com.dp.radar.domain.repositories.ILoginRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetIsLoggedInUseCase
@Inject
constructor(
    private val repository: ILoginRepository,
) {
    operator fun invoke(): Flow<Boolean> = repository.isLoggedIn
}
