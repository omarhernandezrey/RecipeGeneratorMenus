package com.example.recipe_generator.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad Room — IngredientEntity.
 *
 * Tabla "ingredients". Relación N:1 con recipes (FK con CASCADE).
 * F2-17: Anotaciones Room activadas.
 *
 * Capa: Data
 */
@Entity(
    tableName = "ingredients",
    foreignKeys = [
        ForeignKey(
            entity = RecipeEntity::class,
            parentColumns = ["id"],
            childColumns = ["recipe_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("recipe_id")]
)
data class IngredientEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "recipe_id")
    val recipeId: String,
    val name: String,
    val quantity: String,
    val unit: String = ""
)
