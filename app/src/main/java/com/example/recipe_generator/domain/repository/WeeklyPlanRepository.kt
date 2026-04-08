package com.example.recipe_generator.domain.repository

import com.example.recipe_generator.domain.model.WeeklyPlan
import kotlinx.coroutines.flow.Flow

/**
 * Contrato del repositorio del plan semanal del usuario.
 *
 * Todas las operaciones reciben userId para garantizar aislamiento
 * total entre usuarios en la capa de datos local.
 *
 * C-09
 * Capa: Domain
 */
interface WeeklyPlanRepository {

    /** Retorna el plan semanal completo del usuario como Flow reactivo. */
    fun getWeeklyPlan(userId: String): Flow<List<WeeklyPlan>>

    /** Retorna las comidas asignadas de un dia especifico. */
    fun getDay(userId: String, day: String): Flow<List<WeeklyPlan>>

    /** Asigna o reemplaza una receta en una celda del plan semanal. */
    suspend fun setMeal(userId: String, day: String, mealType: String, recipeId: String)

    /** Elimina la receta asignada a una celda del plan semanal. */
    suspend fun removeMeal(userId: String, day: String, mealType: String)
}
