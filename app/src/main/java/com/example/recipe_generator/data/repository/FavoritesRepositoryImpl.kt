package com.example.recipe_generator.data.repository

import android.content.Context
import com.example.recipe_generator.data.legacy.LegacyFavoritesRepository
import com.example.recipe_generator.domain.model.Recipe
import com.example.recipe_generator.domain.repository.FavoritesRepository
import com.example.recipe_generator.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

/**
 * Implementación del repositorio de favoritos — Capa de Datos.
 *
 * En Fase 0 usa DataStore para IDs favoritos y el repositorio de recetas
 * como fuente de contenido. En F3 puede migrar a Room sin tocar la capa
 * de Presentación.
 *
 * Capa: Data
 */
class FavoritesRepositoryImpl(
    context: Context,
    recipeRepository: RecipeRepository
) : FavoritesRepository {
    private val legacyFavoritesRepository = LegacyFavoritesRepository(context.applicationContext)
    private val allRecipes = recipeRepository.getAllRecipes()

    override fun getFavoriteRecipes(): Flow<List<Recipe>> =
        combine(allRecipes, legacyFavoritesRepository.favoriteRecipeIds) { recipes, favoriteIds ->
            recipes
                .filter { it.id in favoriteIds }
                .map { it.copy(isFavorite = true) }
        }

    override fun getFavoriteIds(): Flow<Set<String>> = legacyFavoritesRepository.favoriteRecipeIds

    override suspend fun toggleFavorite(recipeId: String) {
        legacyFavoritesRepository.toggleFavorite(recipeId)
    }

    override suspend fun removeFavorite(recipeId: String) {
        legacyFavoritesRepository.removeFavorite(recipeId)
    }

    override suspend fun isFavorite(recipeId: String): Boolean {
        return getFavoriteIds().first().contains(recipeId)
    }
}
