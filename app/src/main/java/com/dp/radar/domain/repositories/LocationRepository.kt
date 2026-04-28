package com.dp.radar.domain.repositories

import com.dp.radar.domain.model.LocationData

interface LocationRepository {
    suspend fun getCurrentLocation(): LocationData
}
