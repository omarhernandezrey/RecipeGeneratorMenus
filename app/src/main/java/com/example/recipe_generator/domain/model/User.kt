package com.example.recipe_generator.domain.model

/**
 * Domain Model - Usuario autenticado
 * Representa los datos del usuario independiente del backend
 */
data class User(
    val uid: String,
    val email: String,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

