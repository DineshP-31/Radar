package com.dp.radar.domain.login

import com.dp.radar.domain.repositories.ILoginRepository
import javax.inject.Inject

class SaveEmailUseCase @Inject constructor(
    private val repository: ILoginRepository
) {
    suspend operator fun invoke(email: String) = repository.saveEmail(email)
}
