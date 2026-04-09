package com.example.recipe_generator.domain.model

/**
 * Modelo de dominio para una celda del plan semanal del usuario.
 *
 * Mantiene la información necesaria para pintar la UI sin depender
 * de entidades Room ni de joins adicionales en la capa de presentación.
 *
 * C-09
 * Capa: Domain
 */
data class WeeklyPlan(
    /** Firebase UID del propietario */
    val userId: String,

    /** "Lunes" | "Martes" | ... | "Domingo" */
    val dayOfWeek: String,

    /** "Desayuno" | "Almuerzo" | "Cena" */
    val mealType: String,

    /** ID de la receta asignada a la celda */
    val recipeId: String,

    /** Titulo cacheado de la receta para mostrar en la UI */
    val recipeTitle: String = "",

    /** imageRes cacheado — file:// path local o URL https para mostrar en la celda */
    val imageRes: String = "",

    /** Nota libre opcional del usuario */
    val notes: String = "",

    /** Epoch millis de la ultima actualizacion */
    val updatedAt: Long = System.currentTimeMillis()
)
