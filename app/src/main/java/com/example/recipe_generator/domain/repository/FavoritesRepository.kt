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

    /** Stream reactivo de recetas marcadas como favoritas del usuario. */
    fun getFavoriteRecipes(userId: String): Flow<List<Recipe>>

    /** Stream reactivo de IDs de recetas favoritas del usuario. */
    fun getFavoriteIds(userId: String): Flow<Set<String>>

    /**
     * Alterna el estado favorito de una receta para el usuario dado.
     * Si es favorita la quita; si no lo es, la agrega.
     */
    suspend fun toggleFavorite(userId: String, recipeId: String)

    /** Elimina una receta específica de favoritos del usuario. */
    suspend fun removeFavorite(userId: String, recipeId: String)

    /** Verifica si una receta es favorita del usuario (consulta única, no stream). */
    suspend fun isFavorite(userId: String, recipeId: String): Boolean
}
