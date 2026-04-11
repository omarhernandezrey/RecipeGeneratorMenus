package com.example.recipe_generator.data.repository

import android.util.Log
import com.example.recipe_generator.data.remote.GlobalRecipeVideoPipeline
import com.example.recipe_generator.data.sync.RecipeVideoStorageService
import com.example.recipe_generator.domain.model.RecipeVideoResolutionResult
import com.example.recipe_generator.domain.repository.RecipeVideoRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RecipeVideoRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val recipeVideoStorageService: RecipeVideoStorageService,
    private val youtubeApiKey: String
) : RecipeVideoRepository {

    companion object {
        private const val TAG = "RecipeVideoRepository"
    }

    override suspend fun resolveVideoUrl(
        currentVideoUrl: String?,
        recipeTitle: String
    ): String {
        return GlobalRecipeVideoPipeline.ensurePersistenceVideo(
            currentVideoUrl = currentVideoUrl,
            recipeTitle = recipeTitle,
            youTubeApiKey = youtubeApiKey
        )
    }

    override suspend fun ensureVideoForRecipe(
        recipeId: String,
        recipeTitle: String,
        currentVideoUrl: String?,
        ownerUserId: String?
    ): RecipeVideoResolutionResult {
        val resolvedVideoUrl = resolveVideoUrl(
            currentVideoUrl = currentVideoUrl,
            recipeTitle = recipeTitle
        )

        if (recipeId.isBlank() || !GlobalRecipeVideoPipeline.needsResolution(currentVideoUrl)) {
            return RecipeVideoResolutionResult(
                resolvedVideoUrl = resolvedVideoUrl,
                persisted = true
            )
        }

        val persisted = updateFirestoreVideo(recipeId, resolvedVideoUrl)
        if (persisted) {
            recipeVideoStorageService.cacheVideoMetadata(
                recipeId = recipeId,
                videoUrl = resolvedVideoUrl,
                sourceCollection = "recipes"
            )
            if (!ownerUserId.isNullOrBlank()) {
                recipeVideoStorageService.cacheVideoMetadata(
                    recipeId = recipeId,
                    videoUrl = resolvedVideoUrl,
                    ownerUserId = ownerUserId,
                    sourceCollection = "user_recipes"
                )
            }
        }

        return RecipeVideoResolutionResult(
            resolvedVideoUrl = resolvedVideoUrl,
            persisted = persisted
        )
    }

    private suspend fun updateFirestoreVideo(recipeId: String, resolvedVideoUrl: String): Boolean {
        return try {
            firestore.collection("recipes")
                .document(recipeId)
                .update("videoYoutube", resolvedVideoUrl)
                .await()
            true
        } catch (_: Exception) {
            try {
                firestore.collection("user_recipes")
                    .document(recipeId)
                    .update("videoYoutube", resolvedVideoUrl)
                    .await()
                true
            } catch (userRecipeError: Exception) {
                Log.e(TAG, "Error cacheando video para recipeId=$recipeId", userRecipeError)
                false
            }
        }
    }
}

