package com.example.recipe_generator.domain.usecase

import com.example.recipe_generator.domain.model.RecipeVideoResolutionResult
import com.example.recipe_generator.domain.repository.RecipeVideoRepository

class EnsureRecipeVideoUseCase(
    private val recipeVideoRepository: RecipeVideoRepository
) {
    suspend operator fun invoke(
        recipeId: String,
        recipeTitle: String,
        currentVideoUrl: String?,
        ownerUserId: String? = null
    ): RecipeVideoResolutionResult {
        return recipeVideoRepository.ensureVideoForRecipe(
            recipeId = recipeId,
            recipeTitle = recipeTitle,
            currentVideoUrl = currentVideoUrl,
            ownerUserId = ownerUserId
        )
    }
}

