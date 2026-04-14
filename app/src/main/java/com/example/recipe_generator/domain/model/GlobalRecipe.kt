package com.example.recipe_generator.domain.model

enum class GlobalRecipeSource {
    FIRESTORE,
    THEMEALDB,
    FALLBACK
}

data class GlobalRecipe(
    val id: String,
    val nombre: String,
    val imagen: String,
    val aliases: List<String>,
    val pais: String,
    val keywords: List<String>,
    val source: GlobalRecipeSource
)

