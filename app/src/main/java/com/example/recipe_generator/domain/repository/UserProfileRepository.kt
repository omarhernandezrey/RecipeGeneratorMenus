package com.example.recipe_generator.domain.repository

import com.example.recipe_generator.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz del repositorio de perfil de usuario — Capa de Dominio.
 *
 * Define el contrato que la capa de Datos debe cumplir.
 * La capa de Presentación solo depende de esta interfaz, nunca de Room
 * ni de la implementación concreta. Principio D de SOLID.
 *
 * C-10
 * Capa: Domain
 */
interface UserProfileRepository {

    /**
     * Retorna el perfil del usuario como Flow reactivo.
     * Emite null si no hay perfil cacheado localmente (primer login o sesión limpia).
     */
    fun getProfile(uid: String): Flow<UserProfile?>

    /**
     * Guarda o reemplaza el perfil completo del usuario en Room.
     * Usado al hacer login para cachear los datos descargados de Firestore.
     */
    suspend fun saveProfile(profile: UserProfile)

    /**
     * Actualiza únicamente el nombre de display del usuario.
     * Carga el perfil actual, aplica el cambio y lo persiste.
     */
    suspend fun updateName(uid: String, displayName: String)

    /**
     * Actualiza únicamente la URL de la foto de perfil.
     * Carga el perfil actual, aplica el cambio y lo persiste.
     */
    suspend fun updatePhoto(uid: String, photoUrl: String?)

    /**
     * Elimina el perfil cacheado del usuario (usado al cerrar sesión).
     */
    suspend fun deleteProfile(uid: String)
}