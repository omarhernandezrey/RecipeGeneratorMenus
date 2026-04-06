package com.example.recipe_generator.data.local.entity

import androidx.room.Entity

/**
 * Entidad Room — WeeklyPlanEntity.
 *
 * Tabla "weekly_plan": plan de comidas semanal personalizado por usuario.
 *
 * Clave primaria compuesta (userId, dayOfWeek, mealType):
 *  - Un usuario solo puede tener UNA receta por comida por día.
 *  - Un upsert (insert or replace) sobreescribe la celda del plan.
 *
 * Valores esperados:
 *  - dayOfWeek : "Lunes" | "Martes" | "Miércoles" | "Jueves" | "Viernes" | "Sábado" | "Domingo"
 *  - mealType  : "Desayuno" | "Almuerzo" | "Cena"
 *
 * C-02
 * Capa: Data
 */
@Entity(
    tableName = "weekly_plan",
    primaryKeys = ["userId", "dayOfWeek", "mealType"]
)
data class WeeklyPlanEntity(
    /** Firebase UID del propietario */
    val userId: String,

    /** "Lunes" | "Martes" | "Miércoles" | "Jueves" | "Viernes" | "Sábado" | "Domingo" */
    val dayOfWeek: String,

    /** "Desayuno" | "Almuerzo" | "Cena" */
    val mealType: String,

    /** ID de la receta asignada (referencia a user_recipes.id o recipes.id) */
    val recipeId: String,

    /** Título cacheado para mostrar en la UI sin hacer join */
    val recipeTitle: String = "",

    /** Nota libre del usuario para esta comida */
    val notes: String = "",

    /** Epoch millis — última modificación de esta celda */
    val updatedAt: Long = System.currentTimeMillis()
)
