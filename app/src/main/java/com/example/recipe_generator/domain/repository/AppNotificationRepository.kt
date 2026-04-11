package com.example.recipe_generator.domain.repository

import com.example.recipe_generator.domain.model.AppNotification
import kotlinx.coroutines.flow.Flow

interface AppNotificationRepository {
    fun getNotifications(): Flow<List<AppNotification>>
    fun getUnreadCount(): Flow<Int>
    suspend fun insert(notification: AppNotification)
    suspend fun markAllRead()
    suspend fun deleteById(id: String)
    suspend fun deleteExpired(cutoffMs: Long)
}
