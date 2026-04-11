package com.example.recipe_generator.data.remote

object GlobalRecipeVideoPipeline {

    private val resolver = RecipeVideoResolver()

    fun ensureDisplayVideo(
        currentVideoUrl: String?,
        recipeTitle: String
    ): String {
        return resolver.resolve(
            currentVideoUrl = currentVideoUrl,
            recipeTitle = recipeTitle
        )
    }

    suspend fun ensurePersistenceVideo(
        currentVideoUrl: String?,
        recipeTitle: String,
        youTubeApiKey: String
    ): String {
        return runCatching {
            resolver.resolveForPersistence(
                currentVideoUrl = currentVideoUrl,
                recipeTitle = recipeTitle,
                youTubeApiKey = youTubeApiKey
            )
        }.getOrElse {
            resolver.resolve(
                currentVideoUrl = currentVideoUrl,
                recipeTitle = recipeTitle
            )
        }
    }

    fun needsResolution(currentVideoUrl: String?): Boolean {
        return resolver.needsResolution(currentVideoUrl)
    }
}

