package com.example.recipe_generator.data.local.entity

/**
 * Entidad Room — StepEntity.
 *
 * Tabla "steps". Relación N:1 con recipes.
 * Anotaciones Room se activan en F2-17.
 *
 * Capa: Data
 */
// @Entity(tableName = "steps", foreignKeys = [...])
data class StepEntity(
    // @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    // @ColumnInfo(name = "recipe_id")
    val recipeId: String,
    val stepNumber: Int,
    val title: String,
    val description: String
)
