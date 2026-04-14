package com.example.recipe_generator.data.remote

interface RecipeVideoSearchEngine {
    suspend fun searchBestVideoUrl(
        recipeTitle: String,
        queries: List<String>,
        priorityKeywords: List<String>,
        apiKey: String
    ): String?
}
