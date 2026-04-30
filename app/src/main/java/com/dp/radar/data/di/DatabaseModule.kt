package com.dp.radar.data.di

import android.content.Context
import androidx.room.Room
import com.dp.radar.data.datasources.db.RadarDatabase
import com.dp.radar.data.datasources.db.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): RadarDatabase =
        Room.databaseBuilder(context, RadarDatabase::class.java, "radar.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideUserDao(database: RadarDatabase): UserDao = database.userDao()
}