package com.example.recipe_generator.domain.repository

import com.example.recipe_generator.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz del repositorio de favoritos — Capa de Dominio.
 *
 * Separado de RecipeRepository para cumplir el principio I de SOLID
 * (Interface Segregation): no todos los consumidores necesitan
 * todas las operaciones de Recipe.
 *
 * Implementación concreta: FavoritesRepositoryImpl (Room).
 *
 * Capa: Domain
 */
interface FavoritesRepository {

    /** Stream reactivo de recetas marcadas como favoritas. */
    fun getFavoriteRecipes(): Flow<List<Recipe>>

    /** Stream reactivo de IDs de recetas favoritas. */
    fun getFavoriteIds(): Flow<Set<String>>

    /**
     * Alterna el estado favorito de una receta.
     * Si es favorita la quita; si no lo es, la agrega.
     */
    suspend fun toggleFavorite(recipeId: String)

    /** Elimina una receta específica de favoritos. */
    suspend fun removeFavorite(recipeId: String)

    /** Verifica si una receta es favorita (consulta única, no stream). */
    suspend fun isFavorite(recipeId: String): Boolean
}
