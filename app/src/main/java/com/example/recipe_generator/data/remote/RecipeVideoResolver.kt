package com.example.recipe_generator.data.remote

import android.net.Uri
import android.util.Log
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap

class RecipeVideoResolver(
    private val youTubeVideoSearchService: YouTubeVideoSearchService = YouTubeVideoSearchService()
) {

    companion object {
        private const val TAG = "RecipeVideoResolver"
        private const val YOUTUBE_SEARCH_BASE = "https://www.youtube.com/results?search_query="
    }

    private val titleCache = ConcurrentHashMap<String, String>()
    private val inFlightTitleLocks = ConcurrentHashMap<String, Mutex>()

    fun resolve(currentVideoUrl: String?, recipeTitle: String): String {
        if (!currentVideoUrl.isNullOrBlank() && isValidYoutubeUrl(currentVideoUrl)) {
            return currentVideoUrl
        }

        val normalizedTitle = recipeTitle.trim().lowercase()
        titleCache[normalizedTitle]?.let { return it }

        val fallback = buildSearchUrl(recipeTitle)
        titleCache[normalizedTitle] = fallback
        Log.d(TAG, "Usando fallback local para: $recipeTitle")
        return fallback
    }

    suspend fun resolveForPersistence(
        currentVideoUrl: String?,
        recipeTitle: String,
        youTubeApiKey: String
    ): String {
        if (!currentVideoUrl.isNullOrBlank() && isValidYoutubeUrl(currentVideoUrl)) {
            return currentVideoUrl
        }

        val normalizedTitle = recipeTitle.trim().lowercase()
        titleCache[normalizedTitle]?.let { return it }

        val lock = inFlightTitleLocks.getOrPut(normalizedTitle) { Mutex() }
        return lock.withLock {
            titleCache[normalizedTitle]?.let { return@withLock it }

            val resolvedFromApi = runCatching {
                youTubeVideoSearchService.searchBestVideoUrl(
                    recipeTitle = recipeTitle,
                    queries = getSearchQueries(recipeTitle),
                    priorityKeywords = getPriorityKeywords(),
                    apiKey = youTubeApiKey
                )
            }.getOrNull()

            val resolved = resolvedFromApi
                ?.takeIf { isValidYoutubeUrl(it) }
                ?: buildSearchUrl(recipeTitle)

            titleCache[normalizedTitle] = resolved
            if (resolvedFromApi != null) {
                Log.d(TAG, "Video resuelto con YouTube API para: $recipeTitle")
            } else {
                Log.d(TAG, "Video resuelto con fallback para: $recipeTitle")
            }
            resolved
        }
    }

    fun needsResolution(currentVideoUrl: String?): Boolean {
        return currentVideoUrl.isNullOrBlank() || !isValidYoutubeUrl(currentVideoUrl)
    }

    private fun buildSearchUrl(recipeTitle: String): String {
        val query = "como preparar $recipeTitle receta tutorial"
        return "$YOUTUBE_SEARCH_BASE${Uri.encode(query)}"
    }

    fun getSearchQueries(recipeTitle: String): List<String> = listOf(
        "como preparar $recipeTitle",
        "receta de $recipeTitle",
        "how to make $recipeTitle",
        "$recipeTitle recipe tutorial"
    )

    fun getPriorityKeywords(): List<String> = listOf(
        "receta", "tutorial", "original", "casera", "preparar", "cocinar"
    )

    fun clearCache() {
        titleCache.clear()
    }

    private fun isValidYoutubeUrl(url: String): Boolean {
        return url.startsWith("https://www.youtube.com/") ||
                url.startsWith("https://youtube.com/") ||
                url.startsWith("https://youtu.be/") ||
                url.startsWith("https://m.youtube.com/")
    }
}
