package com.example.recipe_generator.domain.model

/**
 * Modelo de dominio — UserPreferences.
 *
 * Representa todas las preferencias persistidas del usuario.
 * Leídas/escritas a través de UserPrefsRepository → DataStore.
 *
 * Capa: Domain
 */
data class UserPreferences(
    /** "Claro" | "Oscuro" | "Sistema" */
    val theme: String = "Claro",
    /** "Español" | "Inglés" | "Portugués" */
    val language: String = "Español",
    /** Número de personas por defecto al escalar recetas */
    val defaultPortions: Int = 2,
    /** Dietas activas, p.ej. ["Vegetariano", "Sin Gluten"] */
    val selectedDiets: Set<String> = emptySet()
)
