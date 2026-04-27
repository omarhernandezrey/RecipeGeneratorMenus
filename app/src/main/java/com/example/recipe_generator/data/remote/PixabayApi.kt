package com.example.recipe_generator.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/**
 * Cliente HTTP para Pixabay optimizado para Gastronomía Profesional.
 * F-Images: Filtros estrictos para evitar imágenes no deseadas.
 */
object PixabayApi {

    private const val API_KEY  = "47705177-b69b93f77cf498a59bf5d6444"
    private const val BASE_URL = "https://pixabay.com/api/"

    /**
     * Busca imágenes culinarias profesionales.
     * Filtra estrictamente por:
     * - image_type=photo (Evita dibujos/caricaturas)
     * - category=food (Fuerza contexto gastronómico)
     * - safesearch=true (Filtro de seguridad)
     */
    suspend fun searchImages(query: String): List<MealImage> = withContext(Dispatchers.IO) {
        runCatching {
            // Limpiamos la query de términos que puedan traer "chistes"
            val cleanQuery = query.lowercase()
                .replace("funny", "")
                .replace("caricatura", "")
                .replace("cartoon", "")
                .trim()

            val encoded = URLEncoder.encode(cleanQuery, "UTF-8")
            // Añadimos category=food y image_type=photo para máxima seriedad
            val url = "$BASE_URL?key=$API_KEY&q=$encoded&image_type=photo&category=food&per_page=10&order=popular&safesearch=true&min_width=600"

            val connection = (URL(url).openConnection() as HttpURLConnection).apply {
                connectTimeout = 8_000
                readTimeout    = 8_000
                requestMethod  = "GET"
                setRequestProperty("Accept", "application/json")
            }

            val body = connection.inputStream.bufferedReader().use { it.readText() }
            connection.disconnect()

            val hits = JSONObject(body).optJSONArray("hits")
                ?: return@runCatching emptyList()

            List(hits.length()) { i ->
                val hit = hits.getJSONObject(i)
                val tags = hit.optString("tags", "").lowercase()
                
                // Doble validación: Si los tags contienen palabras "sospechosas", ignoramos
                if (tags.contains("cartoon") || tags.contains("drawing") || tags.contains("illustration")) {
                    null
                } else {
                    MealImage(
                        name     = hit.optString("tags", ""),
                        thumbUrl = hit.optString("webformatURL", "")
                    )
                }
            }.filterNotNull().filter { it.thumbUrl.isNotBlank() }
        }.getOrElse { emptyList() }
    }
}
