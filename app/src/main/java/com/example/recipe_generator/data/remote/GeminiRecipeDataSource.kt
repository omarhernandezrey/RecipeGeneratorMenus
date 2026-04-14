package com.example.recipe_generator.data.remote

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import android.util.Log

class GeminiRecipeDataSource(private val apiKey: String) {

    // Quitamos el responseMimeType para máxima compatibilidad con API Keys gratuitas
    private val model = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey
    )

    suspend fun generateRecipe(
        prompt: String,
        category: String
    ): JSONObject? = withContext(Dispatchers.IO) {
        try {
            val fullPrompt = """
                Eres un chef. Genera una receta de $category llamada "$prompt".
                Responde EXCLUSIVAMENTE con un objeto JSON (sin texto antes ni después, sin markdown):
                {
                  "title": "$prompt",
                  "description": "descripción corta",
                  "timeInMinutes": 30,
                  "calories": 400,
                  "difficulty": "Fácil",
                  "ingredients": ["ingrediente 1", "ingrediente 2"],
                  "steps": ["paso 1", "paso 2"],
                  "imageSearchKeywords": "$prompt professional food photography"
                }
            """.trimIndent()

            val response = model.generateContent(fullPrompt)
            val responseText = response.text ?: return@withContext null
            
            Log.d("GEMINI_RAW", "Respuesta IA: ${responseText}")

            // Extracción manual ultra-robusta
            val start = responseText.indexOf("{")
            val end = responseText.lastIndexOf("}")
            
            if (start != -1 && end != -1) {
                val cleanJson = responseText.substring(start, end + 1)
                return@withContext JSONObject(cleanJson)
            }
            
            null
        } catch (e: Exception) {
            Log.e("GEMINI_ERROR", "Error en IA: ${e.message}", e)
            null
        }
    }
}
