package com.example.recipe_generator.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/**
 * Imagen de receta obtenida desde TheMealDB.
 * [name]     nombre del plato
 * [thumbUrl] URL de la miniatura (500×500)
 */
data class MealImage(
    val name: String,
    val thumbUrl: String
)

/**
 * Cliente HTTP para TheMealDB — sin API key, completamente gratuito.
 * Docs: https://www.themealdb.com/api.php
 */
object MealDbApi {

    private const val SEARCH_URL = "https://www.themealdb.com/api/json/v1/1/search.php"

    /**
     * Busca platos por nombre y devuelve la lista de imágenes disponibles.
     * Devuelve lista vacía ante cualquier error de red o JSON.
     */
    suspend fun searchMeals(query: String): List<MealImage> = withContext(Dispatchers.IO) {
        runCatching {
            val encoded = URLEncoder.encode(query.trim(), "UTF-8")
            val connection = (URL("$SEARCH_URL?s=$encoded").openConnection() as HttpURLConnection).apply {
                connectTimeout = 8_000
                readTimeout   = 8_000
                requestMethod = "GET"
                setRequestProperty("Accept", "application/json")
            }

            val body = connection.inputStream.bufferedReader().use { it.readText() }
            connection.disconnect()

            val mealsArray = JSONObject(body).optJSONArray("meals")
                ?: return@runCatching emptyList()

            List(mealsArray.length()) { i ->
                val meal = mealsArray.getJSONObject(i)
                MealImage(
                    name     = meal.optString("strMeal", ""),
                    thumbUrl = meal.optString("strMealThumb", "")
                )
            }.filter { it.thumbUrl.isNotBlank() }
        }.getOrElse { emptyList() }
    }
}
