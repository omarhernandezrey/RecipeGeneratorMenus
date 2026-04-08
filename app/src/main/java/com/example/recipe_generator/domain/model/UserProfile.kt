package com.example.recipe_generator.domain.model

/**
 * Modelo de dominio — UserProfile.
 *
 * Representa el perfil completo del usuario autenticado, incluyendo
 * preferencias personales que van más allá de los datos básicos de Auth.
 *
 * A diferencia de User (datos mínimos de autenticación), UserProfile
 * contiene preferencias dietéticas y configuración de la app.
 * Se cachea localmente en Room (UserProfileEntity) y se sincroniza
 * con Firestore como fuente de verdad en la nube.
 *
 * Capa: Domain
 */
data class UserProfile(
    /** Firebase UID — identificador único del usuario */
    val uid: String,

    val displayName: String = "",

    val email: String = "",

    /** URL de la foto de perfil (null si no tiene) */
    val photoUrl: String? = null,

    /** Dietas preferidas. Ej: ["Vegetariano", "Sin gluten"] */
    val preferredDiets: List<String> = emptyList(),

    /** Número de porciones por defecto para el generador de menús (1–10) */
    val defaultPortions: Int = 2,

    val createdAt: Long = System.currentTimeMillis()
)
