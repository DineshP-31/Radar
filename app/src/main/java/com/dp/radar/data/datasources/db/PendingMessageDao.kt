package com.dp.radar.data.datasources.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PendingMessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: PendingMessageEntity)

    @Query("SELECT * FROM pending_messages ORDER BY timestamp ASC")
    suspend fun getAll(): List<PendingMessageEntity>

    @Query(
        "SELECT * FROM pending_messages WHERE senderId = :senderId AND receiverId = :receiverId ORDER BY timestamp ASC"
    )
    fun observeByConversation(senderId: Long, receiverId: Long): Flow<List<PendingMessageEntity>>

    @Query("DELETE FROM pending_messages WHERE localId = :localId")
    suspend fun deleteById(localId: String)
}