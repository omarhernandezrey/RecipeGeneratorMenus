package com.example.recipe_generator.data.repository

import com.example.recipe_generator.data.local.dao.FavoriteDao
import com.example.recipe_generator.data.local.entity.FavoriteEntity
import com.example.recipe_generator.domain.model.Recipe
import com.example.recipe_generator.domain.repository.FavoritesRepository
import com.example.recipe_generator.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Implementación Room del repositorio de favoritos — F3-29.
 *
 * Usa FavoriteDao para persistir IDs de favoritos en SQLite.
 * Combina los IDs de Room con las recetas de RecipeRepository
 * para construir el stream reactivo de recetas favoritas.
 *
 * Capa: Data
 */
class RoomFavoritesRepositoryImpl(
    private val favoriteDao: FavoriteDao,
    private val recipeRepository: RecipeRepository
) : FavoritesRepository {

    override fun getFavoriteRecipes(): Flow<List<Recipe>> =
        combine(
            recipeRepository.getAllRecipes(),
            favoriteDao.getAllFavoriteIds()
        ) { recipes, favoriteIds ->
            val idSet = favoriteIds.toSet()
            recipes
                .filter { it.id in idSet }
                .map { it.copy(isFavorite = true) }
        }

    override fun getFavoriteIds(): Flow<Set<String>> =
        favoriteDao.getAllFavoriteIds().map { it.toSet() }

    override suspend fun toggleFavorite(recipeId: String) {
        if (favoriteDao.isFavorite(recipeId) > 0) {
            favoriteDao.deleteFavorite(recipeId)
        } else {
            favoriteDao.insertFavorite(FavoriteEntity(recipeId))
        }
    }

    override suspend fun removeFavorite(recipeId: String) {
        favoriteDao.deleteFavorite(recipeId)
    }

    override suspend fun isFavorite(recipeId: String): Boolean =
        getFavoriteIds().first().contains(recipeId)
}
