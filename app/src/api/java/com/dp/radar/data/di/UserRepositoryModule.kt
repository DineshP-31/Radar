package com.dp.radar.data.di

import com.dp.radar.data.NetworkMonitor
import com.dp.radar.data.datasources.remote.RadarApiService
import com.dp.radar.data.repositories.DefaultUserRepository
import com.dp.radar.domain.repositories.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserRepositoryModule {

    @Provides
    @Singleton
    fun providesUserRepository(
        radarApiService: RadarApiService,
        networkMonitor: NetworkMonitor
    ): UserRepository = DefaultUserRepository(radarApiService, networkMonitor)
}