package com.example.recipe_generator.data.local.entity

/**
 * Entidad Room — RecipeEntity.
 *
 * Representa la tabla "recipes" en SQLite.
 * Anotaciones @Entity, @PrimaryKey etc. se agregarán en F2-16
 * cuando se configure Room en build.gradle.kts (F0-07).
 *
 * IMPORTANTE: Esta clase es SOLO de la capa de Datos.
 * La capa de Presentación NUNCA debe importar esta clase.
 * Usa siempre el modelo de dominio Recipe (domain/model/Recipe.kt).
 *
 * Capa: Data
 */
// @Entity(tableName = "recipes")  ← se activa en F2-16
data class RecipeEntity(
    // @PrimaryKey
    val id: String,
    val title: String,
    val imageRes: String,
    val timeInMinutes: Int,
    val calories: Int,
    val difficulty: String,
    val category: String,
    val categorySubtitle: String,
    val description: String,
    val isFavorite: Boolean = false,
    val rating: Double = 4.5,
    val proteinGrams: Int = 0,
    val carbsGrams: Int = 0,
    val fatGrams: Int = 0,
    val dayOfWeek: String
)
