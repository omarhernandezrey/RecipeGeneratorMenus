package com.example.recipe_generator.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.recipe_generator.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO — UserProfileDao.
 *
 * Operaciones sobre la tabla user_profile.
 * La clave primaria es el Firebase UID — siempre hay como máximo
 * un perfil por usuario en el dispositivo.
 *
 * insertOrUpdate usa REPLACE sobre la PK: si ya existe el perfil
 * lo sobreescribe, si no existe lo crea (upsert semántico).
 *
 * C-06
 * Capa: Data
 */
@Dao
interface UserProfileDao {

    // ── Consultas ─────────────────────────────────────────────────────

    /** Retorna el perfil del usuario como Flow reactivo (null si no existe caché local). */
    @Query("SELECT * FROM user_profile WHERE uid = :uid")
    fun getProfile(uid: String): Flow<UserProfileEntity?>

    /** Consulta puntual del perfil (suspend — para uso en operaciones de escritura). */
    @Query("SELECT * FROM user_profile WHERE uid = :uid")
    suspend fun getProfileOnce(uid: String): UserProfileEntity?

    // ── Insert / Update ───────────────────────────────────────────────

    /**
     * Inserta el perfil si no existe o lo reemplaza si ya existe (upsert).
     * Usar tanto al hacer login (sync desde Firebase) como al editar el perfil.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: UserProfileEntity)

    // ── Delete ────────────────────────────────────────────────────────

    /** Elimina el perfil cacheado del usuario (usado al cerrar sesión). */
    @Query("DELETE FROM user_profile WHERE uid = :uid")
    suspend fun deleteProfile(uid: String)
}
