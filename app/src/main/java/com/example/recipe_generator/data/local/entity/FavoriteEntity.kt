package com.example.recipe_generator.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Room — FavoriteEntity.
 *
 * Tabla "favorites" para persistir IDs de recetas marcadas como favoritas.
 * Implementa F3-29: FavoritesRepository con Room.
 *
 * Capa: Data
 */
@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val recipeId: String
)
