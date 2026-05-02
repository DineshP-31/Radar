package com.dp.radar.data.datasources.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [UserEntity::class, PendingMessageEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class RadarDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun pendingMessageDao(): PendingMessageDao
}