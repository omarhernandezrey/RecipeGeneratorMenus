package com.example.recipe_generator.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.recipe_generator.data.local.entity.WeeklyPlanEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO — WeeklyPlanDao.
 *
 * Operaciones sobre la tabla weekly_plan.
 * La clave primaria compuesta (userId, dayOfWeek, mealType) garantiza que
 * cada usuario tenga como máximo una receta por comida por día.
 *
 * El upsert se logra con @Insert(onConflict = REPLACE): si ya existe una
 * entrada para esa combinación de clave, la sobreescribe automáticamente.
 *
 * C-05
 * Capa: Data
 */
@Dao
interface WeeklyPlanDao {

    // ── Consultas ─────────────────────────────────────────────────────

    /** Retorna el plan semanal completo del usuario como Flow reactivo. */
    @Query(
        "SELECT * FROM weekly_plan WHERE userId = :userId " +
        "ORDER BY dayOfWeek, mealType"
    )
    fun getPlanForUser(userId: String): Flow<List<WeeklyPlanEntity>>

    /** Retorna las comidas del usuario para un día específico. */
    @Query(
        "SELECT * FROM weekly_plan WHERE userId = :userId AND dayOfWeek = :day " +
        "ORDER BY mealType"
    )
    fun getPlanForDay(userId: String, day: String): Flow<List<WeeklyPlanEntity>>

    /** Retorna una celda específica del plan (userId + día + tipo de comida). */
    @Query(
        "SELECT * FROM weekly_plan WHERE userId = :userId " +
        "AND dayOfWeek = :day AND mealType = :mealType"
    )
    suspend fun getMeal(userId: String, day: String, mealType: String): WeeklyPlanEntity?

    // ── Upsert ────────────────────────────────────────────────────────

    /**
     * Inserta o reemplaza una celda del plan semanal.
     * Gracias a la clave compuesta (userId, dayOfWeek, mealType) con REPLACE,
     * actúa como upsert: crea la celda si no existe, la sobreescribe si ya existe.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(planEntry: WeeklyPlanEntity)

    /** Inserta o reemplaza múltiples celdas en una sola transacción (útil para sync). */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(planEntries: List<WeeklyPlanEntity>)

    // ── Delete ────────────────────────────────────────────────────────

    /** Elimina la receta asignada a una comida específica de un día. */
    @Query(
        "DELETE FROM weekly_plan WHERE userId = :userId " +
        "AND dayOfWeek = :day AND mealType = :mealType"
    )
    suspend fun deleteMeal(userId: String, day: String, mealType: String)

    /** Elimina todas las celdas del plan que referencian una receta específica del usuario. */
    @Query("DELETE FROM weekly_plan WHERE userId = :userId AND recipeId = :recipeId")
    suspend fun deleteMealsByRecipeId(userId: String, recipeId: String)

    /** Elimina todas las entradas del plan semanal de un usuario (usado al cerrar sesión). */
    @Query("DELETE FROM weekly_plan WHERE userId = :userId")
    suspend fun deleteAllByUser(userId: String)
}
