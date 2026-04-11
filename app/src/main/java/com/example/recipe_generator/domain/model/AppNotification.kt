package com.example.recipe_generator.domain.model

data class AppNotification(
    val id: String,
    val title: String,
    val body: String,
    val type: String,          // "plan_generated" | "recipe_created"
    val createdAt: Long,
    val isRead: Boolean = false
)
