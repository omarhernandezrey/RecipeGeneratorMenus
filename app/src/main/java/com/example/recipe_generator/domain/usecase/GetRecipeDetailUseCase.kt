package com.example.recipe_generator.domain.usecase

import com.example.recipe_generator.domain.model.Recipe
import com.example.recipe_generator.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow

/**
 * Caso de uso — Obtener detalle completo de una receta.
 *
 * Obtiene una receta por ID incluyendo sus ingredientes y pasos
 * (usando la query @Transaction de Room via el repositorio).
 *
 * Capa: Domain
 */
class GetRecipeDetailUseCase(
    private val recipeRepository: RecipeRepository
) {
    /**
     * Ejecuta el caso de uso.
     * @param recipeId ID único de la receta.
     * @return Flow con la receta completa o null si no existe.
     */
    operator fun invoke(recipeId: String): Flow<Recipe?> {
        return recipeRepository.getRecipeById(recipeId)
    }
}
