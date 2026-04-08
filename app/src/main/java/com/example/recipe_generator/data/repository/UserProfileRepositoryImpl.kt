package com.example.recipe_generator.data.repository

import com.example.recipe_generator.data.local.dao.UserProfileDao
import com.example.recipe_generator.data.local.entity.UserProfileEntity
import com.example.recipe_generator.domain.model.UserProfile
import com.example.recipe_generator.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray

/**
 * Implementación Room del perfil del usuario.
 *
 * Usa user_profile como caché local offline-first. Las actualizaciones
 * parciales preservan el resto de campos existentes cuando el perfil
 * ya fue persistido previamente.
 *
 * C-10
 * Capa: Data
 */
class UserProfileRepositoryImpl(
    private val userProfileDao: UserProfileDao
) : UserProfileRepository {

    override fun getProfile(uid: String): Flow<UserProfile?> =
        userProfileDao.getProfile(uid).map { entity ->
            entity?.toDomain()
        }

    override suspend fun saveProfile(profile: UserProfile) {
        userProfileDao.insertOrUpdate(profile.toEntity())
    }

    override suspend fun updateName(uid: String, displayName: String) {
        val currentProfile = userProfileDao.getProfileOnce(uid)

        userProfileDao.insertOrUpdate(
            UserProfileEntity(
                uid = uid,
                displayName = displayName,
                email = currentProfile?.email.orEmpty(),
                photoUrl = currentProfile?.photoUrl,
                preferredDietsJson = currentProfile?.preferredDietsJson ?: "[]",
                defaultPortions = currentProfile?.defaultPortions ?: 2,
                createdAt = currentProfile?.createdAt ?: System.currentTimeMillis()
            )
        )
    }

    override suspend fun updatePhoto(uid: String, photoUrl: String?) {
        val currentProfile = userProfileDao.getProfileOnce(uid)

        userProfileDao.insertOrUpdate(
            UserProfileEntity(
                uid = uid,
                displayName = currentProfile?.displayName.orEmpty(),
                email = currentProfile?.email.orEmpty(),
                photoUrl = photoUrl,
                preferredDietsJson = currentProfile?.preferredDietsJson ?: "[]",
                defaultPortions = currentProfile?.defaultPortions ?: 2,
                createdAt = currentProfile?.createdAt ?: System.currentTimeMillis()
            )
        )
    }

    override suspend fun deleteProfile(uid: String) {
        userProfileDao.deleteProfile(uid)
    }
}

private fun UserProfileEntity.toDomain(): UserProfile = UserProfile(
    uid = uid,
    displayName = displayName,
    email = email,
    photoUrl = photoUrl,
    preferredDiets = parseJsonArrayToSet(preferredDietsJson),
    defaultPortions = defaultPortions,
    createdAt = createdAt
)

private fun UserProfile.toEntity(): UserProfileEntity = UserProfileEntity(
    uid = uid,
    displayName = displayName,
    email = email,
    photoUrl = photoUrl,
    preferredDietsJson = JSONArray(preferredDiets.toList()).toString(),
    defaultPortions = defaultPortions,
    createdAt = createdAt
)

private fun parseJsonArrayToSet(json: String): Set<String> = try {
    val array = JSONArray(json)
    List(array.length()) { index -> array.getString(index) }.toSet()
} catch (_: Exception) {
    emptySet()
}
