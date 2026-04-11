package com.example.recipe_generator.domain.model

/**
 * Modelo de dominio — Recipe.
 *
 * Representa una receta en la capa de Dominio.
 * Es un POJO puro: sin anotaciones de Room, sin dependencias de Android.
 * La capa de Datos mapea RecipeEntity → Recipe mediante RecipeMapper.
 *
 * Capa: Domain
 */
data class Recipe(
    val id: String,
    val title: String,
    /** Nombre del recurso drawable local, p.ej. "img_scrambled_eggs" */
    val imageRes: String,
    val timeInMinutes: Int,
    val calories: Int,
    /** "Fácil" | "Medio" | "Difícil" */
    val difficulty: String,
    /** "Desayuno" | "Almuerzo" | "Cena" */
    val category: String,
    val categorySubtitle: String,
    val description: String,
    val isFavorite: Boolean = false,
    val rating: Double = 4.5,
    val proteinGrams: Int = 0,
    val carbsGrams: Int = 0,
    val fatGrams: Int = 0,
    /** "Lunes" | "Martes" | ... | "Domingo" */
    val dayOfWeek: String,
    val videoYoutube: String? = null,
    val ingredients: List<Ingredient> = emptyList(),
    val ingredientTags: List<String> = emptyList(),
    val steps: List<RecipeStep> = emptyList()
)
