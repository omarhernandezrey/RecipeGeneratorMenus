package com.example.recipe_generator.domain.repository

import com.example.recipe_generator.domain.model.RecipeVideoResolutionResult

interface RecipeVideoRepository {
    suspend fun resolveVideoUrl(
        currentVideoUrl: String?,
        recipeTitle: String
    ): String

    suspend fun ensureVideoForRecipe(
        recipeId: String,
        recipeTitle: String,
        currentVideoUrl: String?,
        ownerUserId: String? = null
    ): RecipeVideoResolutionResult
}

