package com.example.recipe_generator.data.repository

import com.example.recipe_generator.data.legacy.getAllRecipesAsDomainModel
import com.example.recipe_generator.domain.model.Recipe
import com.example.recipe_generator.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * Implementación del repositorio de recetas — Capa de Datos.
 *
 * En Fase 0 usa la fuente local legacy como seed para desacoplar la UI
 * de los datos mock. En F2 la implementación se cambia a Room sin tocar
 * la capa de Presentación.
 *
 * Capa: Data
 */
class RecipeRepositoryImpl : RecipeRepository {
    private val recipes = MutableStateFlow(getAllRecipesAsDomainModel())

    override fun getAllRecipes(): Flow<List<Recipe>> = recipes

    override fun getRecipesByDay(day: String): Flow<List<Recipe>> =
        recipes.map { allRecipes -> allRecipes.filter { it.dayOfWeek == day } }

    override fun getRecipesByCategory(category: String): Flow<List<Recipe>> =
        recipes.map { allRecipes ->
            allRecipes.filter { it.category.equals(category, ignoreCase = true) }
        }

    override fun getRecipeById(id: String): Flow<Recipe?> =
        recipes.map { allRecipes -> allRecipes.firstOrNull { it.id == id } }

    override fun searchRecipes(query: String): Flow<List<Recipe>> =
        recipes.map { allRecipes ->
            allRecipes.filter { recipe ->
                recipe.title.contains(query, ignoreCase = true) ||
                    recipe.description.contains(query, ignoreCase = true)
            }
        }

    override suspend fun insertAll(recipes: List<Recipe>) {
        this.recipes.value = recipes
    }

    override suspend fun count(): Int = recipes.value.size
}
