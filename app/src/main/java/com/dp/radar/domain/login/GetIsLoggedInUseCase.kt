package com.dp.radar.com.dp.radar.domain.login

import com.dp.radar.com.dp.radar.domain.repositories.ILoginRepository
import javax.inject.Inject

class GetIsLoggedInUseCase @Inject constructor(
    private val repository: ILoginRepository
) {
    operator fun invoke(): Boolean = repository.isLoggedIn()
}