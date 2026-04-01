package com.example.recipe_generator.data.repository

import com.example.recipe_generator.domain.model.Recipe
import com.example.recipe_generator.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Implementación del repositorio de recetas — Capa de Datos.
 *
 * Implementa RecipeRepository (interfaz de Dominio) usando Room como
 * fuente de datos. El DAO se inyectará en F2-18/F2-19 cuando se
 * configure Room.
 *
 * Por ahora retorna datos vacíos como placeholder para que compile.
 * Se completa en F2-21.
 *
 * Capa: Data
 */
class RecipeRepositoryImpl(
    // private val recipeDao: RecipeDao  ← se inyecta en F2-21
) : RecipeRepository {

    override fun getAllRecipes(): Flow<List<Recipe>> = flowOf(emptyList())

    override fun getRecipesByDay(day: String): Flow<List<Recipe>> = flowOf(emptyList())

    override fun getRecipesByCategory(category: String): Flow<List<Recipe>> = flowOf(emptyList())

    override fun getRecipeById(id: String): Flow<Recipe?> = flowOf(null)

    override fun searchRecipes(query: String): Flow<List<Recipe>> = flowOf(emptyList())

    override suspend fun insertAll(recipes: List<Recipe>) { /* F2-21 */ }

    override suspend fun count(): Int = 0
}
