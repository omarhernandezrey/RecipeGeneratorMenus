package com.example.recipe_generator.data.sync

import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import org.json.JSONObject

class RecipeVideoStorageService(
    private val storage: FirebaseStorage
) {

    companion object {
        private const val TAG = "RecipeVideoStorageSvc"
    }

    suspend fun cacheVideoMetadata(
        recipeId: String,
        videoUrl: String,
        ownerUserId: String? = null,
        sourceCollection: String
    ) {
        if (recipeId.isBlank() || videoUrl.isBlank()) return

        val payload = JSONObject(
            mapOf(
                "recipeId" to recipeId,
                "videoYoutube" to videoUrl,
                "sourceCollection" to sourceCollection,
                "ownerUserId" to ownerUserId,
                "updatedAt" to System.currentTimeMillis()
            )
        ).toString().toByteArray(Charsets.UTF_8)

        try {
            storage.reference
                .child("recipe_videos/$recipeId.json")
                .putBytes(payload)
                .await()

            if (!ownerUserId.isNullOrBlank()) {
                storage.reference
                    .child("users/$ownerUserId/recipe_videos/$recipeId.json")
                    .putBytes(payload)
                    .await()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error cacheando video en Storage para $recipeId: ${e.message}")
            throw e
        }
    }
}

