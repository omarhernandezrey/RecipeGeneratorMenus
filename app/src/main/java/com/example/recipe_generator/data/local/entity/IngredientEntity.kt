package com.example.recipe_generator.data.local.entity

/**
 * Entidad Room — IngredientEntity.
 *
 * Tabla "ingredients". Relación N:1 con recipes.
 * Anotaciones Room se activan en F2-17.
 *
 * Capa: Data
 */
// @Entity(tableName = "ingredients", foreignKeys = [...])
data class IngredientEntity(
    // @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    // @ColumnInfo(name = "recipe_id")
    val recipeId: String,
    val name: String,
    val quantity: String,
    val unit: String = ""
)
