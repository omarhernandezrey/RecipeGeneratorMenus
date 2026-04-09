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
 * Receta completa importada desde TheMealDB.
 * Incluye ingredientes con medidas y los pasos de preparación.
 */
data class MealDbFullRecipe(
    val id: String,
    val name: String,
    val category: String,
    val area: String,
    val thumbUrl: String,
    val instructions: String,
    /** Lista "medida + ingrediente", ej. "3/4 cup Soy Sauce" */
    val ingredients: List<String>
)

/**
 * Cliente HTTP para TheMealDB — sin API key, completamente gratuito.
 * Docs: https://www.themealdb.com/api.php
 */
object MealDbApi {

    private const val BASE = "https://www.themealdb.com/api/json/v1/1"

    /**
     * Busca platos por nombre y devuelve la lista de imágenes disponibles.
     * Devuelve lista vacía ante cualquier error de red o JSON.
     */
    suspend fun searchMeals(query: String): List<MealImage> = withContext(Dispatchers.IO) {
        runCatching {
            val encoded = URLEncoder.encode(query.trim(), "UTF-8")
            val connection = (URL("$BASE/search.php?s=$encoded").openConnection() as HttpURLConnection).apply {
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

    /**
     * Busca recetas completas (ingredientes + instrucciones) por nombre.
     * Devuelve lista vacía ante cualquier error.
     */
    suspend fun searchFullRecipes(query: String): List<MealDbFullRecipe> = withContext(Dispatchers.IO) {
        runCatching {
            val encoded = URLEncoder.encode(query.trim(), "UTF-8")
            val connection = (URL("$BASE/search.php?s=$encoded").openConnection() as HttpURLConnection).apply {
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
                parseMeal(mealsArray.getJSONObject(i))
            }
        }.getOrElse { emptyList() }
    }

    private fun parseMeal(meal: JSONObject): MealDbFullRecipe {
        val ingredients = buildList {
            for (n in 1..20) {
                val ingredient = meal.optString("strIngredient$n", "").trim()
                val measure    = meal.optString("strMeasure$n", "").trim()
                if (ingredient.isNotBlank()) {
                    add(if (measure.isNotBlank()) "$measure $ingredient" else ingredient)
                }
            }
        }
        return MealDbFullRecipe(
            id           = meal.optString("idMeal", ""),
            name         = meal.optString("strMeal", ""),
            category     = meal.optString("strCategory", ""),
            area         = meal.optString("strArea", ""),
            thumbUrl     = meal.optString("strMealThumb", ""),
            instructions = meal.optString("strInstructions", ""),
            ingredients  = ingredients
        )
    }
}
