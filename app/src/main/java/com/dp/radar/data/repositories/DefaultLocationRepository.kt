package com.dp.radar.data.repositories

import android.annotation.SuppressLint
import android.content.Context
import com.dp.radar.domain.model.LocationData
import com.dp.radar.domain.repositories.LocationRepository
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine

class DefaultLocationRepository(
    @ApplicationContext private val context: Context
) : LocationRepository {

    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): LocationData =
        suspendCancellableCoroutine { continuation ->

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    location?.let {
                        continuation.resume(
                            LocationData(
                                it.latitude,
                                it.longitude
                            ),
                            null
                        )
                    } ?: continuation.cancel()
                }
                .addOnFailureListener {
                    continuation.cancel(it)
                }
        }
}
