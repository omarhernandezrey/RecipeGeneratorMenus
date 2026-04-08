package com.example.recipe_generator.data.repository

import com.example.recipe_generator.data.local.dao.RecipeDao
import com.example.recipe_generator.data.local.dao.UserRecipeDao
import com.example.recipe_generator.data.local.dao.WeeklyPlanDao
import com.example.recipe_generator.data.local.entity.WeeklyPlanEntity
import com.example.recipe_generator.domain.model.WeeklyPlan
import com.example.recipe_generator.domain.repository.WeeklyPlanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementacion Room del plan semanal del usuario.
 *
 * setMeal hace upsert sobre la celda (userId + dia + tipo de comida)
 * y cachea el titulo de la receta para evitar consultas extra en UI.
 *
 * C-09
 * Capa: Data
 */
class WeeklyPlanRepositoryImpl(
    private val weeklyPlanDao: WeeklyPlanDao,
    private val userRecipeDao: UserRecipeDao,
    private val recipeDao: RecipeDao
) : WeeklyPlanRepository {

    override fun getWeeklyPlan(userId: String): Flow<List<WeeklyPlan>> =
        weeklyPlanDao.getPlanForUser(userId).map { entries ->
            entries.map { it.toDomain() }
        }

    override fun getDay(userId: String, day: String): Flow<List<WeeklyPlan>> =
        weeklyPlanDao.getPlanForDay(userId, day).map { entries ->
            entries.map { it.toDomain() }
        }

    override suspend fun setMeal(userId: String, day: String, mealType: String, recipeId: String) {
        val currentEntry = weeklyPlanDao.getMeal(userId, day, mealType)
        val recipeTitle = resolveRecipeTitle(userId, recipeId)

        weeklyPlanDao.upsert(
            WeeklyPlanEntity(
                userId = userId,
                dayOfWeek = day,
                mealType = mealType,
                recipeId = recipeId,
                recipeTitle = recipeTitle,
                notes = currentEntry?.notes.orEmpty(),
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun removeMeal(userId: String, day: String, mealType: String) {
        weeklyPlanDao.deleteMeal(userId, day, mealType)
    }

    private suspend fun resolveRecipeTitle(userId: String, recipeId: String): String {
        val userRecipeTitle = userRecipeDao.getById(recipeId)
            ?.takeIf { it.userId == userId }
            ?.title

        if (!userRecipeTitle.isNullOrBlank()) {
            return userRecipeTitle
        }

        return recipeDao.getRecipeById(recipeId)?.title.orEmpty()
    }
}

private fun WeeklyPlanEntity.toDomain(): WeeklyPlan = WeeklyPlan(
    userId = userId,
    dayOfWeek = dayOfWeek,
    mealType = mealType,
    recipeId = recipeId,
    recipeTitle = recipeTitle,
    notes = notes,
    updatedAt = updatedAt
)
