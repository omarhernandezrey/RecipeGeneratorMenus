package com.example.recipe_generator.data.repository

import com.example.recipe_generator.data.local.dao.UserRecipeDao
import com.example.recipe_generator.data.local.dao.WeeklyPlanDao
import com.example.recipe_generator.data.local.entity.UserRecipeEntity
import com.example.recipe_generator.domain.model.UserRecipe
import com.example.recipe_generator.domain.repository.UserRecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray

/**
 * Implementación de UserRecipeRepository usando Room como fuente de verdad local.
 *
 * Estrategia offline-first:
 *  - Todas las escrituras van primero a Room (isSynced = false).
 *  - FirestoreSyncService (C-12) se encarga de subir los cambios pendientes a Firestore.
 *  - Al hacer login, Firestore descarga al Room y marca isSynced = true.
 *
 * El mapper Entity ↔ Domain convierte ingredientsJson / stepsJson (String)
 * a List<String> usando org.json.JSONArray (disponible en Android sin dependencias extra).
 *
 * C-08
 * Capa: Data
 */
class UserRecipeRepositoryImpl(
    private val userRecipeDao: UserRecipeDao,
    private val weeklyPlanDao: WeeklyPlanDao? = null
) : UserRecipeRepository {

    // ── Queries ───────────────────────────────────────────────────────

    override fun getMyRecipes(userId: String): Flow<List<UserRecipe>> =
        userRecipeDao.getByUserId(userId).map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getRecipesForDay(userId: String, day: String): Flow<List<UserRecipe>> =
        userRecipeDao.getByDay(userId, day).map { entities ->
            entities.map { it.toDomain() }
        }

    override fun searchRecipes(userId: String, query: String): Flow<List<UserRecipe>> =
        userRecipeDao.searchByTitle(userId, query).map { entities ->
            entities.map { it.toDomain() }
        }

    // ── Escritura ─────────────────────────────────────────────────────

    override suspend fun addRecipe(recipe: UserRecipe) {
        userRecipeDao.insert(recipe.toEntity(isSynced = false))
    }

    override suspend fun updateRecipe(recipe: UserRecipe) {
        userRecipeDao.update(recipe.toEntity(isSynced = false))
    }

    override suspend fun deleteRecipe(recipe: UserRecipe) {
        weeklyPlanDao?.deleteMealsByRecipeId(recipe.userId, recipe.id)
        userRecipeDao.delete(recipe.toEntity(isSynced = recipe.isSynced))
    }

    override suspend fun deleteAllByUser(userId: String) {
        weeklyPlanDao?.deleteAllByUser(userId)
        userRecipeDao.deleteAllByUser(userId)
    }
}

// ── Mapper Entity → Domain ────────────────────────────────────────────────

private fun UserRecipeEntity.toDomain(): UserRecipe = UserRecipe(
    id = id,
    userId = userId,
    title = title,
    imageRes = imageRes,
    timeInMinutes = timeInMinutes,
    calories = calories,
    difficulty = difficulty,
    category = category,
    description = description,
    dayOfWeek = dayOfWeek,
    mealType = mealType,
    ingredients = parseJsonArray(ingredientsJson),
    steps = parseJsonArray(stepsJson),
    isSynced = isSynced,
    createdAt = createdAt,
    updatedAt = updatedAt
)

// ── Mapper Domain → Entity ────────────────────────────────────────────────

private fun UserRecipe.toEntity(isSynced: Boolean): UserRecipeEntity = UserRecipeEntity(
    id = id,
    userId = userId,
    title = title,
    imageRes = imageRes,
    timeInMinutes = timeInMinutes,
    calories = calories,
    difficulty = difficulty,
    category = category,
    description = description,
    dayOfWeek = dayOfWeek,
    mealType = mealType,
    ingredientsJson = toJsonArray(ingredients),
    stepsJson = toJsonArray(steps),
    isSynced = isSynced,
    createdAt = createdAt,
    updatedAt = System.currentTimeMillis()
)

// ── Utilidades JSON ───────────────────────────────────────────────────────

/** Parsea un JSON array de strings. Devuelve lista vacía si el JSON es inválido. */
private fun parseJsonArray(json: String): List<String> = try {
    val array = JSONArray(json)
    List(array.length()) { i -> array.getString(i) }
} catch (e: Exception) {
    emptyList()
}

/** Serializa una lista de strings a JSON array. */
private fun toJsonArray(list: List<String>): String =
    JSONArray(list).toString()
