package com.example.recipe_generator.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.recipe_generator.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO — FavoriteDao.
 *
 * Operaciones sobre la tabla "favorites".
 * Clave primaria compuesta (userId, recipeId) — aísla favoritos
 * por usuario autenticado (E-03).
 *
 * Capa: Data
 */
@Dao
interface FavoriteDao {

    @Query("SELECT recipeId FROM favorites WHERE userId = :userId")
    fun getAllFavoriteIds(userId: String): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE userId = :userId AND recipeId = :recipeId")
    suspend fun deleteFavorite(userId: String, recipeId: String)

    @Query("SELECT COUNT(*) FROM favorites WHERE userId = :userId AND recipeId = :recipeId")
    suspend fun isFavorite(userId: String, recipeId: String): Int

    @Query("DELETE FROM favorites WHERE userId = :userId")
    suspend fun deleteAllByUser(userId: String)
}