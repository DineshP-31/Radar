package com.dp.radar.domain.login

import com.dp.radar.domain.repositories.ILoginRepository
import javax.inject.Inject

class ClearEmailUseCase @Inject constructor(
    private val repository: ILoginRepository
) {
    suspend operator fun invoke() = repository.clearEmail()
}
