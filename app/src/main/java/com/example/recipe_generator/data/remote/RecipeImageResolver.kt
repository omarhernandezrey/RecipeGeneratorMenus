package com.example.recipe_generator.data.remote

import java.util.concurrent.ConcurrentHashMap

/**
 * Resuelve la URL de imagen correcta para una receta.
 */
object RecipeImageResolver {

    private val cache = ConcurrentHashMap<String, String>()

    /**
     * Búsqueda inteligente usando palabras clave específicas de la IA.
     * Esto evita resultados "chistosos" al ser extremadamente descriptivo.
     */
    suspend fun resolveWithKeywords(keywords: String): String {
        if (keywords.isBlank()) return ""
        
        // Prioridad: Pixabay con filtros de comida profesional
        val pixabayUrl = PixabayApi.searchImages(keywords).firstOrNull()?.thumbUrl.orEmpty()
        return pixabayUrl
    }

    suspend fun resolve(title: String, existingUrl: String = ""): String {
        if (existingUrl.startsWith("http")) return existingUrl
        cache[title]?.let { return it }

        val englishQuery = FoodTranslator.toEnglish(title)
        val mealDbUrl = MealDbApi.searchMeals(englishQuery).firstOrNull()?.thumbUrl.orEmpty()
        if (mealDbUrl.isNotBlank()) {
            cache[title] = mealDbUrl
            return mealDbUrl
        }

        // Búsqueda reforzada para evitar "pollos vivos"
        val safeQuery = "$title gourmet plated food professional photography"
        val pixabayUrl = PixabayApi.searchImages(safeQuery).firstOrNull()?.thumbUrl.orEmpty()
        if (pixabayUrl.isNotBlank()) {
            cache[title] = pixabayUrl
        }
        return pixabayUrl
    }
}
