package com.example.recipe_generator.data.remote

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class GeminiRecipeDataSource(private val apiKey: String) {

    // Modelo con JSON mode — fuerza respuesta JSON pura sin markdown
    private val modelJson = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey,
        generationConfig = generationConfig {
            responseMimeType = "application/json"
        }
    )

    // Modelo de texto como fallback (por si la key no soporta JSON mode)
    private val modelText = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey
    )

    suspend fun generateRecipe(
        prompt: String,
        category: String
    ): JSONObject? = withContext(Dispatchers.IO) {

        val fullPrompt = buildPrompt(prompt, category)

        // Intento 1: JSON mode (respuesta pura JSON)
        val result = tryGenerate(modelJson, fullPrompt, tag = "JSON_MODE")
            ?: tryGenerate(modelText, fullPrompt, tag = "TEXT_MODE") // Intento 2: texto plano con extracción manual

        if (result == null) {
            Log.e("GEMINI_ERROR", "Ambos modos fallaron para: $prompt")
        }
        result
    }

    private suspend fun tryGenerate(
        model: GenerativeModel,
        prompt: String,
        tag: String
    ): JSONObject? {
        return try {
            val response = model.generateContent(prompt)
            val rawText = response.text

            if (rawText.isNullOrBlank()) {
                Log.w("GEMINI_$tag", "Respuesta vacía o nula")
                return null
            }

            Log.d("GEMINI_$tag", "Respuesta recibida (${rawText.length} chars): ${rawText.take(200)}")
            extractJson(rawText)
        } catch (e: Exception) {
            Log.e("GEMINI_$tag", "Excepción: ${e.javaClass.simpleName} — ${e.message}")
            null
        }
    }

    private fun extractJson(text: String): JSONObject? {
        // 1. Intentar parsear directamente (JSON mode ya devuelve limpio)
        try {
            return JSONObject(text.trim())
        } catch (_: Exception) {}

        // 2. Buscar bloque ```json ... ```
        val jsonBlockRegex = Regex("```(?:json)?\\s*\\{([\\s\\S]*?)\\}\\s*```")
        val blockMatch = jsonBlockRegex.find(text)
        if (blockMatch != null) {
            try {
                return JSONObject("{${blockMatch.groupValues[1]}}")
            } catch (_: Exception) {}
        }

        // 3. Extracción por primera { y última }
        val start = text.indexOf('{')
        val end = text.lastIndexOf('}')
        if (start != -1 && end > start) {
            try {
                return JSONObject(text.substring(start, end + 1))
            } catch (e: Exception) {
                Log.w("GEMINI_EXTRACT", "JSON malformado: ${e.message}")
            }
        }

        Log.w("GEMINI_EXTRACT", "No se encontró JSON en la respuesta")
        return null
    }

    private fun buildPrompt(recipeName: String, category: String): String = """
        Genera una receta de cocina de tipo $category llamada "$recipeName".
        Devuelve ÚNICAMENTE el siguiente JSON sin ningún texto adicional:
        {
          "title": "nombre del plato",
          "description": "descripción breve en 1-2 oraciones",
          "timeInMinutes": 30,
          "calories": 400,
          "difficulty": "Fácil",
          "ingredients": ["ingrediente 1 con cantidad", "ingrediente 2 con cantidad"],
          "steps": ["Paso 1: descripción", "Paso 2: descripción"],
          "imageSearchKeywords": "palabras clave para buscar imagen del plato en inglés"
        }
    """.trimIndent()
}
