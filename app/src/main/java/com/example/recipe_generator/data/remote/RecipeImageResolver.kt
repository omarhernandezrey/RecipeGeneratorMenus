package com.example.recipe_generator.data.remote

import java.util.concurrent.ConcurrentHashMap

/**
 * Resuelve la URL de imagen correcta para una receta.
 *
 * Estrategia en orden de confianza:
 *  1. Si ya viene una URL válida en [imageRes], la usa directamente.
 *  2. Intenta TheMealDB (imágenes muy precisas, base de datos culinaria).
 *  3. Fallback a Pixabay con términos más específicos ("food recipe dish").
 *
 * Todas las resoluciones se cachean en memoria para no repetir llamadas de red.
 */
object RecipeImageResolver {

    private val cache = ConcurrentHashMap<String, String>()

    /**
     * Devuelve la mejor URL de imagen disponible para [title].
     * Si [existingUrl] ya es una URL válida, la retorna de inmediato.
     * Retorna cadena vacía si no se encontró ninguna imagen.
     */
    suspend fun resolve(title: String, existingUrl: String = ""): String {
        // 0. Si ya hay URL válida, no hacer nada
        if (existingUrl.startsWith("http")) return existingUrl

        // 1. Caché en memoria
        cache[title]?.let { return it }

        // 2. TheMealDB — traducir al inglés para mejor coincidencia
        val englishQuery = FoodTranslator.toEnglish(title)
        val mealDbUrl = MealDbApi.searchMeals(englishQuery).firstOrNull()?.thumbUrl.orEmpty()
        if (mealDbUrl.isNotBlank()) {
            cache[title] = mealDbUrl
            return mealDbUrl
        }

        // 3. Si el nombre ya está en inglés (ej. recetas de TheMealDB), intentar directo
        if (englishQuery != title) {
            val directUrl = MealDbApi.searchMeals(title).firstOrNull()?.thumbUrl.orEmpty()
            if (directUrl.isNotBlank()) {
                cache[title] = directUrl
                return directUrl
            }
        }

        // 4. Pixabay con términos culinarios más específicos
        val pixabayUrl = PixabayApi.searchImages("$title food dish recipe").firstOrNull()?.thumbUrl.orEmpty()
        if (pixabayUrl.isNotBlank()) {
            cache[title] = pixabayUrl
            return pixabayUrl
        }

        // 5. Último intento: solo el nombre en Pixabay
        val pixabayFallback = PixabayApi.searchImages(title).firstOrNull()?.thumbUrl.orEmpty()
        if (pixabayFallback.isNotBlank()) {
            cache[title] = pixabayFallback
        }
        return pixabayFallback
    }

    /** Limpia el caché (útil en pruebas o al cambiar de usuario) */
    fun clearCache() = cache.clear()
}
