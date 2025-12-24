package com.dp.radar.com.dp.radar.domain

import com.dp.radar.com.dp.radar.domain.model.LocationData
import com.dp.radar.com.dp.radar.domain.repositories.LocationRepository
import javax.inject.Inject

class GetCurrentLocationUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(): LocationData {
        return repository.getCurrentLocation()
    }
}