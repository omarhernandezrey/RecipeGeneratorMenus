package com.example.recipe_generator.domain.model

/**
 * Modelo de dominio — RecipeStep.
 *
 * Representa un paso de preparación de una receta.
 * Pertenece exclusivamente a la capa de Dominio.
 *
 * Capa: Domain
 */
data class RecipeStep(
    val id: Int = 0,
    val stepNumber: Int,
    val title: String,
    val description: String
)
