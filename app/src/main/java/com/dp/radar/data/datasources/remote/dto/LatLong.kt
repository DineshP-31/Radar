package com.dp.radar.data.datasources.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LatLong(
    val lat: Double,
    val lon: Double,
)
