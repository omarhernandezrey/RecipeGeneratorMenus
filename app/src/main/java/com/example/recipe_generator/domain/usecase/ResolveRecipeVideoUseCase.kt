package com.example.recipe_generator.domain.usecase

import com.example.recipe_generator.domain.repository.RecipeVideoRepository

class ResolveRecipeVideoUseCase(
    private val recipeVideoRepository: RecipeVideoRepository
) {
    suspend operator fun invoke(
        currentVideoUrl: String?,
        recipeTitle: String
    ): String {
        return recipeVideoRepository.resolveVideoUrl(
            currentVideoUrl = currentVideoUrl,
            recipeTitle = recipeTitle
        )
    }
}

