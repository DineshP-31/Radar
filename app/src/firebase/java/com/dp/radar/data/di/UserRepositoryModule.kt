package com.dp.radar.data.di

import com.dp.radar.data.NetworkMonitor
import com.dp.radar.data.datasources.db.UserDao
import com.dp.radar.data.repositories.FirebaseUserRepository
import com.dp.radar.domain.repositories.UserRepository
import com.google.firebase.database.FirebaseDatabase
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
    fun provideFirebaseDatabase(): FirebaseDatabase = FirebaseDatabase.getInstance()

    @Provides
    @Singleton
    fun providesUserRepository(
        database: FirebaseDatabase,
        networkMonitor: NetworkMonitor,
        userDao: UserDao,
    ): UserRepository = FirebaseUserRepository(database, networkMonitor, userDao)
}
