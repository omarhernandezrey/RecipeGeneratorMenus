package com.example.recipe_generator.domain.model

/**
 * Modelo de dominio — Ingredient.
 *
 * Representa un ingrediente de una receta con cantidad y unidad.
 *
 * Capa: Domain
 */
data class Ingredient(
    val id: Int = 0,
    val name: String,
    val quantity: String,
    val unit: String = ""
)
