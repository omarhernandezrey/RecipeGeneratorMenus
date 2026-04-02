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
 * Expone un Flow para observar cambios en tiempo real — F3-29.
 *
 * Capa: Data
 */
@Dao
interface FavoriteDao {

    @Query("SELECT recipeId FROM favorites")
    fun getAllFavoriteIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE recipeId = :recipeId")
    suspend fun deleteFavorite(recipeId: String)

    @Query("SELECT COUNT(*) FROM favorites WHERE recipeId = :recipeId")
    suspend fun isFavorite(recipeId: String): Int
}
