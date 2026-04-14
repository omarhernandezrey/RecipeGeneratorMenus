package com.example.recipe_generator.data.repository

import com.example.recipe_generator.data.legacy.getAllRecipesAsDomainModel
import com.example.recipe_generator.data.local.dao.RecipeDao
import com.example.recipe_generator.data.mapper.toEntity
import com.example.recipe_generator.data.mapper.toDomain
import com.example.recipe_generator.data.remote.GlobalRecipeVideoPipeline
import com.example.recipe_generator.domain.model.Recipe
import com.example.recipe_generator.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * Implementación del repositorio de recetas — F2-18 / F2-21.
 *
 * Dos modos de operación:
 * - Con RecipeDao (producción): fuente de verdad = Room.
 * - Sin RecipeDao (tests unitarios): fuente de verdad = datos legacy en memoria.
 *
 * Capa: Data
 */
class RecipeRepositoryImpl(
    private val recipeDao: RecipeDao? = null
) : RecipeRepository {

    /** Fuente in-memory para tests unitarios (cuando recipeDao es null). */
    private val inMemoryRecipes = if (recipeDao == null)
        MutableStateFlow(getAllRecipesAsDomainModel()) else null

    // ── Queries ───────────────────────────────────────────────────────

    override fun getAllRecipes(): Flow<List<Recipe>> =
        if (recipeDao != null)
            recipeDao.getAllRecipes().map { entities ->
                entities.map { it.toDomain() }.withGuaranteedVideo()
            }
        else
            inMemoryRecipes!!.map { it }

    override fun getRecipesByDay(day: String): Flow<List<Recipe>> =
        if (recipeDao != null)
            recipeDao.getRecipesByDay(day).map { entities ->
                entities.map { it.toDomain() }.withGuaranteedVideo()
            }
        else
            inMemoryRecipes!!.map { all ->
                all.filter { it.dayOfWeek == day }
                    .sortedBy { mealTypeOrder(it.category) }
            }

    override fun getRecipesByCategory(category: String): Flow<List<Recipe>> =
        if (recipeDao != null)
            recipeDao.getRecipesByCategory(category).map { entities ->
                entities.map { it.toDomain() }.withGuaranteedVideo()
            }
        else
            inMemoryRecipes!!.map { all ->
                all.filter { it.category.equals(category, ignoreCase = true) }
            }

    override fun getRecipeById(id: String): Flow<Recipe?> =
        if (recipeDao != null) {
            flow {
                val entity = recipeDao.getRecipeById(id)
                if (entity != null) {
                    val ingredients = recipeDao.getIngredientsByRecipeId(id)
                    val steps = recipeDao.getStepsByRecipeId(id)
                    emit(entity.toDomain(ingredients, steps).withGuaranteedVideo())
                } else {
                    emit(null)
                }
            }
        } else {
            inMemoryRecipes!!.map { all ->
                all.firstOrNull { it.id == id }
            }
        }

    override fun searchRecipes(query: String): Flow<List<Recipe>> =
        if (recipeDao != null)
            recipeDao.searchRecipes(query).map { entities ->
                entities.map { it.toDomain() }.withGuaranteedVideo()
            }
        else
            inMemoryRecipes!!.map { all ->
                all.filter { recipe ->
                    recipe.title.contains(query, ignoreCase = true) ||
                        recipe.description.contains(query, ignoreCase = true)
                }
            }

    // ── Escritura ─────────────────────────────────────────────────────

    override suspend fun insertAll(recipes: List<Recipe>) {
        val normalizedRecipes = if (recipeDao != null) recipes.withGuaranteedVideo() else recipes
        if (recipeDao != null) {
            recipeDao.insertRecipes(normalizedRecipes.map { it.toEntity() })
            normalizedRecipes.forEach { recipe ->
                if (recipe.ingredients.isNotEmpty()) {
                    recipeDao.insertIngredients(recipe.ingredients.map { it.toEntity(recipe.id) })
                }
                if (recipe.steps.isNotEmpty()) {
                    recipeDao.insertSteps(recipe.steps.map { it.toEntity(recipe.id) })
                }
            }
        } else {
            inMemoryRecipes!!.value = normalizedRecipes
        }
    }

    override suspend fun count(): Int =
        if (recipeDao != null) recipeDao.count()
        else inMemoryRecipes!!.value.size
}

private fun mealTypeOrder(category: String) = when (category) {
    "Desayuno" -> 1
    "Almuerzo" -> 2
    "Cena"     -> 3
    else       -> 4
}

private fun List<Recipe>.withGuaranteedVideo(): List<Recipe> = map { it.withGuaranteedVideo() }

private fun Recipe.withGuaranteedVideo(): Recipe = copy(
    videoYoutube = GlobalRecipeVideoPipeline.ensureDisplayVideo(
        currentVideoUrl = videoYoutube,
        recipeTitle = title
    )
)
