package com.example.recipe_generator.domain.repository

import com.example.recipe_generator.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * Contrato del repositorio del perfil del usuario.
 *
 * Todas las operaciones reciben el UID autenticado para mantener
 * aislados los datos locales por usuario.
 *
 * C-10
 * Capa: Domain
 */
interface UserProfileRepository {

    /** Retorna el perfil del usuario como Flow reactivo. */
    fun getProfile(uid: String): Flow<UserProfile?>

    /** Inserta o reemplaza el perfil completo del usuario. */
    suspend fun saveProfile(profile: UserProfile)

    /** Actualiza solo el nombre visible del usuario. */
    suspend fun updateName(uid: String, displayName: String)

    /** Actualiza solo la foto de perfil del usuario. */
    suspend fun updatePhoto(uid: String, photoUrl: String?)

    /** Elimina el perfil local del usuario. */
    suspend fun deleteProfile(uid: String)
}
