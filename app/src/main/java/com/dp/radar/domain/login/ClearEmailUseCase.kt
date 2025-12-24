package com.dp.radar.com.dp.radar.domain.login

import com.dp.radar.com.dp.radar.domain.repositories.ILoginRepository
import javax.inject.Inject

class ClearEmailUseCase @Inject constructor(
    private val repository: ILoginRepository
) {
    operator fun invoke() = repository.clearEmail()
}