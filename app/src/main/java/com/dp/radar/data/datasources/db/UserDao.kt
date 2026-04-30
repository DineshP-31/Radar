package com.dp.radar.data.datasources.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
abstract class UserDao {
    @Query("SELECT * FROM users")
    abstract fun observeAll(): Flow<List<UserEntity>>

    @Upsert
    protected abstract suspend fun upsertAll(users: List<UserEntity>)

    @Query("DELETE FROM users")
    protected abstract suspend fun deleteAll()

    @Query("UPDATE users SET isOnline = :isOnline WHERE id = :id")
    abstract suspend fun updateOnlineStatus(id: Long, isOnline: Boolean)

    @Transaction
    open suspend fun replaceAll(users: List<UserEntity>) {
        deleteAll()
        upsertAll(users)
    }
}
