package com.dp.radar.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.dp.radar.data.AndroidNetworkMonitor
import com.dp.radar.data.NetworkMonitor
import com.dp.radar.data.repositories.ChatRepository
import com.dp.radar.data.repositories.DefaultLocationRepository
import com.dp.radar.data.repositories.login.LoginRepository
import com.dp.radar.domain.repositories.ILoginRepository
import com.dp.radar.domain.repositories.LocationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RadarModule {
    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            produceFile = { java.io.File(context.filesDir, "datastore/user_prefs.preferences_pb") },
        )

    @Provides
    @Singleton
    fun providesLoginRepository(dataStore: DataStore<Preferences>): ILoginRepository = LoginRepository(dataStore)

    @Provides
    fun provideNetworkStatus(
        @ApplicationContext context: Context,
    ): NetworkMonitor = AndroidNetworkMonitor(context)

    @Provides
    @Singleton
    fun providesChatRepository(): ChatRepository = ChatRepository()

    @Provides
    fun provideCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    fun provideLocationRepository(
        @ApplicationContext context: Context,
    ): LocationRepository = DefaultLocationRepository(context)
}
