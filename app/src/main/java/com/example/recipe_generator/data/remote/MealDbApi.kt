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
            category     = translateCategory(meal.optString("strCategory", "")),
            area         = translateArea(meal.optString("strArea", "")),
            thumbUrl     = meal.optString("strMealThumb", ""),
            instructions = meal.optString("strInstructions", ""),
            ingredients  = ingredients
        )
    }

    /** Traduce el nombre de la categoría de TheMealDB al español */
    private fun translateCategory(en: String): String = when (en.lowercase()) {
        "beef"          -> "Carne de Res"
        "chicken"       -> "Pollo"
        "pork"          -> "Cerdo"
        "lamb"          -> "Cordero"
        "seafood"       -> "Mariscos"
        "pasta"         -> "Pasta"
        "vegetarian"    -> "Vegetariano"
        "vegan"         -> "Vegano"
        "dessert"       -> "Postre"
        "breakfast"     -> "Desayuno"
        "side"          -> "Acompañamiento"
        "starter"       -> "Entrada"
        "goat"          -> "Cabrito"
        "miscellaneous" -> "Varios"
        else            -> en
    }

    /** Traduce el país/región de TheMealDB al español */
    private fun translateArea(en: String): String = when (en.lowercase()) {
        "american"      -> "Americana"
        "british"       -> "Británica"
        "canadian"      -> "Canadiense"
        "chinese"       -> "China"
        "croatian"      -> "Croata"
        "dutch"         -> "Holandesa"
        "egyptian"      -> "Egipcia"
        "french"        -> "Francesa"
        "greek"         -> "Griega"
        "indian"        -> "India"
        "irish"         -> "Irlandesa"
        "italian"       -> "Italiana"
        "jamaican"      -> "Jamaicana"
        "japanese"      -> "Japonesa"
        "kenyan"        -> "Keniana"
        "malaysian"     -> "Malaya"
        "mexican"       -> "Mexicana"
        "moroccan"      -> "Marroquí"
        "polish"        -> "Polaca"
        "portuguese"    -> "Portuguesa"
        "russian"       -> "Rusa"
        "spanish"       -> "Española"
        "thai"          -> "Tailandesa"
        "tunisian"      -> "Tunecina"
        "turkish"       -> "Turca"
        "ukrainian"     -> "Ucraniana"
        "vietnamese"    -> "Vietnamita"
        "unknown"       -> "Desconocida"
        else            -> en
    }
}

/** Traduce términos de comida de español a inglés para consultar TheMealDB */
object FoodTranslator {

    private val dictionary = mapOf(
        // Carnes
        "pollo" to "chicken", "pechuga" to "chicken", "muslo" to "chicken",
        "carne" to "beef", "res" to "beef", "bistec" to "steak", "filete" to "steak",
        "lomo" to "beef", "costillas" to "ribs", "cordero" to "lamb",
        "cerdo" to "pork", "chancho" to "pork", "tocino" to "bacon",
        "salchicha" to "sausage", "chorizo" to "sausage",
        // Pescados y mariscos
        "pescado" to "fish", "salmon" to "salmon", "atun" to "tuna",
        "trucha" to "trout", "bacalao" to "cod", "tilapia" to "tilapia",
        "mariscos" to "seafood", "camaron" to "prawn", "camarones" to "prawn",
        "langosta" to "lobster", "cangrejo" to "crab", "mejillones" to "mussels",
        "calamar" to "squid", "pulpo" to "octopus",
        // Pasta y arroz
        "pasta" to "pasta", "espagueti" to "spaghetti", "fideos" to "noodles",
        "lasana" to "lasagne", "ravioles" to "ravioli",
        "arroz" to "rice", "risotto" to "risotto",
        // Otros platos
        "pizza" to "pizza", "hamburguesa" to "burger", "sandwich" to "sandwich",
        "sopa" to "soup", "ensalada" to "salad", "tacos" to "tacos",
        "curry" to "curry", "sushi" to "sushi", "paella" to "paella",
        "tortilla" to "omelette", "huevos revueltos" to "scrambled eggs",
        "huevo" to "egg", "huevos" to "eggs",
        // Vegetales y frutas
        "tomate" to "tomato", "papa" to "potato", "patata" to "potato",
        "zanahoria" to "carrot", "cebolla" to "onion", "ajo" to "garlic",
        "brocoli" to "broccoli", "espinaca" to "spinach", "lechuga" to "lettuce",
        "pimiento" to "pepper", "maiz" to "corn", "aguacate" to "avocado",
        "limon" to "lemon", "naranja" to "orange", "mango" to "mango",
        "platano" to "banana", "pina" to "pineapple", "fresa" to "strawberry",
        "manzana" to "apple", "pera" to "pear", "uva" to "grape",
        // Postres y lácteos
        "pastel" to "cake", "torta" to "cake", "galleta" to "cookie",
        "pan" to "bread", "queso" to "cheese", "leche" to "milk",
        "mantequilla" to "butter", "helado" to "ice cream",
        "chocolate" to "chocolate", "flan" to "flan",
        // Tiempo de comida
        "desayuno" to "breakfast", "almuerzo" to "lunch", "cena" to "dinner",
        "postre" to "dessert", "entrada" to "starter", "aperitivo" to "starter"
    )

    /**
     * Intenta traducir la consulta al inglés para TheMealDB.
     * Si no encuentra traducción, retorna la consulta original.
     */
    fun toEnglish(query: String): String {
        val lower = query.lowercase().trim()
        // Busca la frase completa primero
        dictionary[lower]?.let { return it }
        // Luego busca la primera palabra que coincida
        for (word in lower.split(" ", ",")) {
            dictionary[word.trim()]?.let { return it }
        }
        return query.trim()
    }
}
