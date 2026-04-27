package com.example.recipe_generator.domain.usecase

import com.example.recipe_generator.domain.model.GlobalRecipe
import com.example.recipe_generator.domain.repository.GlobalRecipeSearchRepository

class SearchGlobalRecipeUseCase(
    private val repository: GlobalRecipeSearchRepository
) {
    suspend operator fun invoke(query: String): GlobalRecipe {
        return repository.searchRecipe(query)
    }
}

