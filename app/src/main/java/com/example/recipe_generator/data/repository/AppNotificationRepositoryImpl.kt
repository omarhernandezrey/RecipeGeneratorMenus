package com.example.recipe_generator.data.repository

import com.example.recipe_generator.data.local.dao.AppNotificationDao
import com.example.recipe_generator.data.local.entity.AppNotificationEntity
import com.example.recipe_generator.domain.model.AppNotification
import com.example.recipe_generator.domain.repository.AppNotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppNotificationRepositoryImpl(
    private val dao: AppNotificationDao
) : AppNotificationRepository {

    override fun getNotifications(): Flow<List<AppNotification>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override fun getUnreadCount(): Flow<Int> = dao.getUnreadCount()

    override suspend fun insert(notification: AppNotification) =
        dao.insert(notification.toEntity())

    override suspend fun markAllRead() = dao.markAllRead()

    override suspend fun deleteById(id: String) = dao.deleteById(id)

    override suspend fun deleteExpired(cutoffMs: Long) = dao.deleteExpired(cutoffMs)

    private fun AppNotificationEntity.toDomain() = AppNotification(
        id = id, title = title, body = body, type = type,
        createdAt = createdAt, isRead = isRead
    )

    private fun AppNotification.toEntity() = AppNotificationEntity(
        id = id, title = title, body = body, type = type,
        createdAt = createdAt, isRead = isRead
    )
}
