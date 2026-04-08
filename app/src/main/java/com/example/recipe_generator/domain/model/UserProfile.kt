package com.example.recipe_generator.domain.model

/**
 * Modelo de dominio del perfil del usuario autenticado.
 *
 * Permite a Presentation y Domain trabajar con el perfil sin depender
 * de Room ni de detalles de serialización.
 *
 * C-10
 * Capa: Domain
 */
data class UserProfile(
    val uid: String,
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val preferredDiets: Set<String> = emptySet(),
    val defaultPortions: Int = 2,
    val createdAt: Long = System.currentTimeMillis()
)
