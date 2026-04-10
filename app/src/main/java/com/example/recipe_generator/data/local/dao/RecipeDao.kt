package com.example.recipe_generator.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.recipe_generator.data.local.entity.IngredientEntity
import com.example.recipe_generator.data.local.entity.RecipeEntity
import com.example.recipe_generator.data.local.entity.StepEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO — RecipeDao.
 *
 * Operaciones CRUD sobre las tablas recipes, ingredients y steps.
 * F2-18: @Query, @Insert, @Update, @Delete implementados.
 *
 * Capa: Data
 */
@Dao
interface RecipeDao {

    // ── Queries de recetas ────────────────────────────────────────────

    @Query("SELECT * FROM recipes ORDER BY dayOfWeek, CASE category WHEN 'Desayuno' THEN 1 WHEN 'Almuerzo' THEN 2 WHEN 'Cena' THEN 3 ELSE 4 END")
    fun getAllRecipes(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE dayOfWeek = :day ORDER BY CASE category WHEN 'Desayuno' THEN 1 WHEN 'Almuerzo' THEN 2 WHEN 'Cena' THEN 3 ELSE 4 END")
    fun getRecipesByDay(day: String): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE isFavorite = 1")
    fun getFavoriteRecipes(): Flow<List<RecipeEntity>>

    @Query(
        "SELECT * FROM recipes WHERE " +
        "title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'"
    )
    fun searchRecipes(query: String): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getRecipeById(id: String): RecipeEntity?

    @Query("SELECT * FROM recipes WHERE category = :category")
    fun getRecipesByCategory(category: String): Flow<List<RecipeEntity>>

    // ── Queries de ingredientes y pasos ───────────────────────────────

    @Query("SELECT * FROM ingredients WHERE recipe_id = :recipeId")
    suspend fun getIngredientsByRecipeId(recipeId: String): List<IngredientEntity>

    @Query("SELECT * FROM steps WHERE recipe_id = :recipeId ORDER BY stepNumber")
    suspend fun getStepsByRecipeId(recipeId: String): List<StepEntity>

    // ── Insert ────────────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipes: List<RecipeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredients(ingredients: List<IngredientEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSteps(steps: List<StepEntity>)

    // ── Update ────────────────────────────────────────────────────────

    @Update
    suspend fun updateRecipe(recipe: RecipeEntity)

    @Query("UPDATE recipes SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: String, isFavorite: Boolean)

    // ── Delete ────────────────────────────────────────────────────────

    @Delete
    suspend fun deleteRecipe(recipe: RecipeEntity)

    // ── Conteo ────────────────────────────────────────────────────────

    @Query("SELECT COUNT(*) FROM recipes")
    suspend fun count(): Int
}
