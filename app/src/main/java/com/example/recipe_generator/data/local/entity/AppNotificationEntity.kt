package com.example.recipe_generator.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_notifications")
data class AppNotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val body: String,
    val type: String,
    val createdAt: Long,
    val isRead: Boolean
)
