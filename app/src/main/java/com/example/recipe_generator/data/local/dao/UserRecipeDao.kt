package com.example.recipe_generator.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.recipe_generator.data.local.entity.UserRecipeEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO — UserRecipeDao.
 *
 * Operaciones CRUD sobre la tabla user_recipes.
 * Todas las queries filtran por userId (Firebase UID) para garantizar
 * aislamiento de datos entre usuarios.
 *
 * C-04
 * Capa: Data
 */
@Dao
interface UserRecipeDao {

    // ── Consultas ─────────────────────────────────────────────────────

    /** Retorna todas las recetas del usuario como Flow reactivo. */
    @Query("SELECT * FROM user_recipes WHERE userId = :userId ORDER BY createdAt DESC")
    fun getByUserId(userId: String): Flow<List<UserRecipeEntity>>

    /** Retorna las recetas del usuario asignadas a un día específico. */
    @Query(
        "SELECT * FROM user_recipes WHERE userId = :userId AND dayOfWeek = :day " +
        "ORDER BY mealType"
    )
    fun getByDay(userId: String, day: String): Flow<List<UserRecipeEntity>>

    /** Busca recetas del usuario cuyo título contenga el texto indicado. */
    @Query(
        "SELECT * FROM user_recipes WHERE userId = :userId " +
        "AND title LIKE '%' || :query || '%' ORDER BY createdAt DESC"
    )
    fun searchByTitle(userId: String, query: String): Flow<List<UserRecipeEntity>>

    /** Retorna una receta por su ID (suspending — para uso puntual). */
    @Query("SELECT * FROM user_recipes WHERE id = :id")
    suspend fun getById(id: String): UserRecipeEntity?

    // ── Insert ────────────────────────────────────────────────────────

    /** Inserta o reemplaza una receta. Útil también para sync desde Firestore. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recipe: UserRecipeEntity)

    /** Inserta o reemplaza múltiples recetas en una sola transacción. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(recipes: List<UserRecipeEntity>)

    // ── Update ────────────────────────────────────────────────────────

    /** Actualiza los datos de una receta existente. */
    @Update
    suspend fun update(recipe: UserRecipeEntity)

    /** Marca una receta como sincronizada con Firestore. */
    @Query("UPDATE user_recipes SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)

    // ── Delete ────────────────────────────────────────────────────────

    /** Elimina una receta del usuario. */
    @Delete
    suspend fun delete(recipe: UserRecipeEntity)

    /** Elimina todas las recetas de un usuario (usado al cerrar sesión). */
    @Query("DELETE FROM user_recipes WHERE userId = :userId")
    suspend fun deleteAllByUser(userId: String)
}
