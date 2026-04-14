package com.example.recipe_generator.domain.repository

import com.example.recipe_generator.domain.model.GlobalRecipe

interface GlobalRecipeSearchRepository {
    suspend fun searchRecipe(query: String): GlobalRecipe
}

