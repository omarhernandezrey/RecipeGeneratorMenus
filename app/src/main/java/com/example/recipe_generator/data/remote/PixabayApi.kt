package com.example.recipe_generator.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/**
 * Cliente HTTP para Pixabay — millones de fotos de comida de todo el mundo.
 * Funciona con búsquedas en español: "bandeja paisa", "ajiaco", "sancocho", etc.
 * https://pixabay.com/api/docs/
 */
object PixabayApi {

    private const val API_KEY  = "47705177-b69b93f77cf498a59bf5d6444"
    private const val BASE_URL = "https://pixabay.com/api/"

    /**
     * Busca imágenes por texto libre.
     * Devuelve lista vacía ante cualquier error de red o JSON.
     * [thumbUrl] es webformatURL (640px) — ideal para grid y descarga.
     */
    suspend fun searchImages(query: String): List<MealImage> = withContext(Dispatchers.IO) {
        runCatching {
            val encoded = URLEncoder.encode(query.trim(), "UTF-8")
            val url = "$BASE_URL?key=$API_KEY&q=$encoded&image_type=photo&per_page=24&order=popular&safesearch=true"

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
                MealImage(
                    name     = hit.optString("tags", ""),
                    thumbUrl = hit.optString("webformatURL", "")
                )
            }.filter { it.thumbUrl.isNotBlank() }
        }.getOrElse { emptyList() }
    }
}
