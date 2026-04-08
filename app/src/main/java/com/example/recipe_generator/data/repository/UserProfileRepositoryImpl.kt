package com.example.recipe_generator.data.repository

import com.example.recipe_generator.data.local.dao.UserProfileDao
import com.example.recipe_generator.data.local.entity.UserProfileEntity
import com.example.recipe_generator.domain.model.UserProfile
import com.example.recipe_generator.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray

/**
 * Implementación de UserProfileRepository usando Room como caché local.
 *
 * Estrategia offline-first:
 *  - Al hacer login: Firestore descarga el perfil -> saveProfile() lo cachea en Room.
 *  - Al editar nombre/foto: updateName()/updatePhoto() actualizan Room localmente;
 *    FirestoreSyncService (C-12) sincroniza los cambios con Firestore.
 *  - Al cerrar sesión: deleteProfile() limpia el caché local.
 *
 * preferredDiets se serializa/deserializa con org.json.JSONArray,
 * disponible en Android sin dependencias extra.
 *
 * C-10
 * Capa: Data
 */
class UserProfileRepositoryImpl(
    private val userProfileDao: UserProfileDao
) : UserProfileRepository {

    // -- Consulta ------------------------------------------------------

    override fun getProfile(uid: String): Flow<UserProfile?> =
        userProfileDao.getProfile(uid).map { entity -> entity?.toDomain() }

    // -- Escritura -----------------------------------------------------

    override suspend fun saveProfile(profile: UserProfile) {
        userProfileDao.insertOrUpdate(profile.toEntity())
    }

    override suspend fun updateName(uid: String, displayName: String) {
        val current = userProfileDao.getProfileOnce(uid) ?: UserProfileEntity(uid = uid)
        userProfileDao.insertOrUpdate(current.copy(displayName = displayName))
    }

    override suspend fun updatePhoto(uid: String, photoUrl: String?) {
        val current = userProfileDao.getProfileOnce(uid) ?: UserProfileEntity(uid = uid)
        userProfileDao.insertOrUpdate(current.copy(photoUrl = photoUrl))
    }

    override suspend fun deleteProfile(uid: String) {
        userProfileDao.deleteProfile(uid)
    }
}

// -- Mapper Entity -> Domain ----------------------------------------------

private fun UserProfileEntity.toDomain(): UserProfile = UserProfile(
    uid = uid,
    displayName = displayName,
    email = email,
    photoUrl = photoUrl,
    preferredDiets = parseJsonArray(preferredDietsJson),
    defaultPortions = defaultPortions,
    createdAt = createdAt
)

// -- Mapper Domain -> Entity ----------------------------------------------

private fun UserProfile.toEntity(): UserProfileEntity = UserProfileEntity(
    uid = uid,
    displayName = displayName,
    email = email,
    photoUrl = photoUrl,
    preferredDietsJson = toJsonArray(preferredDiets),
    defaultPortions = defaultPortions,
    createdAt = createdAt
)

// -- Utilidades JSON -------------------------------------------------------

private fun parseJsonArray(json: String): List<String> = try {
    val array = JSONArray(json)
    List(array.length()) { i -> array.getString(i) }
} catch (e: Exception) {
    emptyList()
}

private fun toJsonArray(list: List<String>): String =
    JSONArray(list).toString()
