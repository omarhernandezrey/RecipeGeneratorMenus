package com.example.recipe_generator.data.sync

import android.util.Log
import com.example.recipe_generator.data.local.dao.UserProfileDao
import com.example.recipe_generator.data.local.dao.UserRecipeDao
import com.example.recipe_generator.data.local.entity.UserProfileEntity
import com.example.recipe_generator.data.local.entity.UserRecipeEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

/**
 * FirestoreSyncService — Sincronización bidireccional Room ↔ Firestore.
 *
 * Estrategia offline-first:
 *  - ESCRITURA (crear/editar/eliminar): primero Room (isSynced=false),
 *    luego intenta subir a Firestore. Si falla la nube, el dato queda
 *    en Room con isSynced=false para reintentarlo después.
 *  - LOGIN (Firestore → Room): descarga todas las recetas y perfil del
 *    usuario desde Firestore y los cachea en Room (isSynced=true).
 *  - SYNC PENDIENTES: sube todas las entradas con isSynced=false a Firestore.
 *
 * Estructura Firestore:
 *  users/{uid}/recipes/{recipeId}   → campos de UserRecipeEntity
 *  users/{uid}/profile/data         → campos de UserProfileEntity
 *
 * C-12
 * Capa: Data
 */
class FirestoreSyncService(
    private val firestore: FirebaseFirestore,
    private val userRecipeDao: UserRecipeDao,
    private val userProfileDao: UserProfileDao
) {

    companion object {
        private const val TAG = "FirestoreSyncService"
        private const val USERS = "users"
        private const val RECIPES = "recipes"
        private const val PROFILE = "profile"
        private const val PROFILE_DOC = "data"
    }

    // ── Referencias Firestore ─────────────────────────────────────────

    private fun recipesCollection(userId: String) =
        firestore.collection(USERS).document(userId).collection(RECIPES)

    private fun profileDocument(userId: String) =
        firestore.collection(USERS).document(userId).collection(PROFILE).document(PROFILE_DOC)

    // ── ESCRITURA: Room + Firestore ───────────────────────────────────

    /**
     * Sube una receta a Firestore después de haberla guardado en Room.
     * Si falla la nube, isSynced queda en false para reintento posterior.
     */
    suspend fun uploadRecipe(recipe: UserRecipeEntity) {
        try {
            recipesCollection(recipe.userId)
                .document(recipe.id)
                .set(recipe.toFirestoreMap(), SetOptions.merge())
                .await()
            userRecipeDao.markSynced(recipe.id)
            Log.d(TAG, "Receta subida a Firestore: ${recipe.id}")
        } catch (e: Exception) {
            Log.w(TAG, "Error al subir receta ${recipe.id}: ${e.message}")
            // isSynced permanece false — se reintentará en syncPendingRecipes()
        }
    }

    /**
     * Elimina una receta de Firestore.
     * Room ya debe haber eliminado el registro local antes de llamar esto.
     */
    suspend fun deleteRecipeFromCloud(userId: String, recipeId: String) {
        try {
            recipesCollection(userId).document(recipeId).delete().await()
            Log.d(TAG, "Receta eliminada de Firestore: $recipeId")
        } catch (e: Exception) {
            Log.w(TAG, "Error al eliminar receta $recipeId de Firestore: ${e.message}")
        }
    }

    /**
     * Sube el perfil del usuario a Firestore.
     * Se llama después de updateName() o updatePhoto() en UserProfileRepository.
     */
    suspend fun uploadProfile(profile: UserProfileEntity) {
        try {
            profileDocument(profile.uid)
                .set(profile.toFirestoreMap(), SetOptions.merge())
                .await()
            Log.d(TAG, "Perfil subido a Firestore: ${profile.uid}")
        } catch (e: Exception) {
            Log.w(TAG, "Error al subir perfil ${profile.uid}: ${e.message}")
        }
    }

    // ── LOGIN: Firestore → Room ───────────────────────────────────────

    /**
     * Descarga todas las recetas del usuario desde Firestore y las cachea en Room.
     * Llamar justo después de autenticarse. Marca isSynced=true en Room.
     */
    suspend fun syncRecipesFromFirestore(userId: String) {
        try {
            val snapshot = recipesCollection(userId).get().await()
            val entities = snapshot.documents.mapNotNull { doc ->
                doc.toRecipeEntity(userId)
            }
            if (entities.isNotEmpty()) {
                userRecipeDao.insertAll(entities.map { it.copy(isSynced = true) })
                Log.d(TAG, "Sincronizadas ${entities.size} recetas desde Firestore para $userId")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error al sincronizar recetas desde Firestore: ${e.message}")
        }
    }

    /**
     * Descarga el perfil del usuario desde Firestore y lo cachea en Room.
     * Llamar justo después de autenticarse.
     */
    suspend fun syncProfileFromFirestore(userId: String) {
        try {
            val doc = profileDocument(userId).get().await()
            if (doc.exists()) {
                val entity = doc.toProfileEntity(userId)
                if (entity != null) {
                    userProfileDao.insertOrUpdate(entity)
                    Log.d(TAG, "Perfil sincronizado desde Firestore para $userId")
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error al sincronizar perfil desde Firestore: ${e.message}")
        }
    }

    /**
     * Sincronización completa al hacer login:
     * descarga recetas + perfil desde Firestore → Room.
     */
    suspend fun syncOnLogin(userId: String) {
        syncProfileFromFirestore(userId)
        syncRecipesFromFirestore(userId)
    }

    // ── SYNC PENDIENTES: isSynced=false → Firestore ───────────────────

    /**
     * Sube todas las recetas locales con isSynced=false a Firestore.
     * Llamar al recuperar conexión a internet.
     */
    suspend fun syncPendingRecipes(userId: String) {
        try {
            val pending = userRecipeDao.getByUserId(userId)
            // Recoge el primer valor del Flow para procesar las pendientes
            var collected = false
            pending.collect { recipes ->
                if (!collected) {
                    collected = true
                    val unsynced = recipes.filter { !it.isSynced }
                    Log.d(TAG, "Subiendo ${unsynced.size} recetas pendientes a Firestore")
                    unsynced.forEach { uploadRecipe(it) }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error al sincronizar recetas pendientes: ${e.message}")
        }
    }

    // ── LOGOUT: limpiar Room local ────────────────────────────────────

    /**
     * Elimina todos los datos locales del usuario al cerrar sesión.
     * Firestore (nube) NO se modifica — los datos persisten en la nube.
     */
    suspend fun clearLocalData(userId: String) {
        try {
            userRecipeDao.deleteAllByUser(userId)
            userProfileDao.deleteProfile(userId)
            Log.d(TAG, "Datos locales eliminados para $userId")
        } catch (e: Exception) {
            Log.w(TAG, "Error al limpiar datos locales: ${e.message}")
        }
    }
}

// ── Mapper Entity → Firestore Map ────────────────────────────────────────

private fun UserRecipeEntity.toFirestoreMap(): Map<String, Any?> = mapOf(
    "id" to id,
    "userId" to userId,
    "title" to title,
    "imageRes" to imageRes,
    "timeInMinutes" to timeInMinutes,
    "calories" to calories,
    "difficulty" to difficulty,
    "category" to category,
    "description" to description,
    "dayOfWeek" to dayOfWeek,
    "mealType" to mealType,
    "ingredientsJson" to ingredientsJson,
    "stepsJson" to stepsJson,
    "createdAt" to createdAt,
    "updatedAt" to updatedAt
)

private fun UserProfileEntity.toFirestoreMap(): Map<String, Any?> = mapOf(
    "uid" to uid,
    "displayName" to displayName,
    "email" to email,
    "photoUrl" to photoUrl,
    "preferredDietsJson" to preferredDietsJson,
    "defaultPortions" to defaultPortions,
    "createdAt" to createdAt
)

// ── Mapper Firestore Document → Entity ───────────────────────────────────

private fun com.google.firebase.firestore.DocumentSnapshot.toRecipeEntity(
    userId: String
): UserRecipeEntity? = try {
    UserRecipeEntity(
        id = getString("id") ?: id,
        userId = getString("userId") ?: userId,
        title = getString("title") ?: "",
        imageRes = getString("imageRes") ?: "",
        timeInMinutes = getLong("timeInMinutes")?.toInt() ?: 0,
        calories = getLong("calories")?.toInt() ?: 0,
        difficulty = getString("difficulty") ?: "Fácil",
        category = getString("category") ?: "",
        description = getString("description") ?: "",
        dayOfWeek = getString("dayOfWeek") ?: "",
        mealType = getString("mealType") ?: "",
        ingredientsJson = getString("ingredientsJson") ?: "[]",
        stepsJson = getString("stepsJson") ?: "[]",
        isSynced = true,
        createdAt = getLong("createdAt") ?: System.currentTimeMillis(),
        updatedAt = getLong("updatedAt") ?: System.currentTimeMillis()
    )
} catch (e: Exception) {
    Log.w("FirestoreSyncService", "Error al mapear documento ${this.id}: ${e.message}")
    null
}

private fun com.google.firebase.firestore.DocumentSnapshot.toProfileEntity(
    userId: String
): UserProfileEntity? = try {
    UserProfileEntity(
        uid = getString("uid") ?: userId,
        displayName = getString("displayName") ?: "",
        email = getString("email") ?: "",
        photoUrl = getString("photoUrl"),
        preferredDietsJson = getString("preferredDietsJson") ?: "[]",
        defaultPortions = getLong("defaultPortions")?.toInt() ?: 2,
        createdAt = getLong("createdAt") ?: System.currentTimeMillis()
    )
} catch (e: Exception) {
    Log.w("FirestoreSyncService", "Error al mapear perfil ${this.id}: ${e.message}")
    null
}
