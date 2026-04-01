package com.example.recipe_generator.data.repository

import com.example.recipe_generator.domain.model.Recipe
import com.example.recipe_generator.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Implementación del repositorio de favoritos — Capa de Datos.
 *
 * Usa Room (campo isFavorite en RecipeEntity) como fuente de verdad.
 * Se completa en F3-29.
 *
 * Capa: Data
 */
class FavoritesRepositoryImpl(
    // private val recipeDao: RecipeDao  ← se inyecta en F3-29
) : FavoritesRepository {

    override fun getFavoriteRecipes(): Flow<List<Recipe>> = flowOf(emptyList())

    override fun getFavoriteIds(): Flow<Set<String>> = flowOf(emptySet())

    override suspend fun toggleFavorite(recipeId: String) { /* F3-29 */ }

    override suspend fun removeFavorite(recipeId: String) { /* F3-29 */ }

    override suspend fun isFavorite(recipeId: String): Boolean = false
}
