package com.example.recipe_generator.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.recipe_generator.data.local.entity.AppNotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppNotificationDao {

    @Query("SELECT * FROM app_notifications ORDER BY createdAt DESC")
    fun getAll(): Flow<List<AppNotificationEntity>>

    @Query("SELECT COUNT(*) FROM app_notifications WHERE isRead = 0")
    fun getUnreadCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notification: AppNotificationEntity)

    @Query("UPDATE app_notifications SET isRead = 1")
    suspend fun markAllRead()

    @Query("DELETE FROM app_notifications WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM app_notifications WHERE createdAt < :cutoff")
    suspend fun deleteExpired(cutoff: Long)
}
