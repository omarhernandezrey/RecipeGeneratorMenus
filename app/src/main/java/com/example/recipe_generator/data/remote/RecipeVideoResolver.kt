package com.example.recipe_generator.data.remote

import android.util.Log
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap

class RecipeVideoResolver(
    private val videoSearchEngine: RecipeVideoSearchEngine = YouTubeVideoSearchService()
) {

    private data class CachedVideo(
        val url: String,
        val isFallback: Boolean
    )

    companion object {
        private const val TAG = "RecipeVideoResolver"
    }

    private val titleCache = ConcurrentHashMap<String, CachedVideo>()
    private val inFlightTitleLocks = ConcurrentHashMap<String, Mutex>()

    fun resolve(currentVideoUrl: String?, recipeTitle: String): String {
        if (!currentVideoUrl.isNullOrBlank() && !YouTubeVideoUrlUtils.needsResolution(currentVideoUrl)) {
            return YouTubeVideoUrlUtils.normalizeVideoUrl(currentVideoUrl)
        }

        val normalizedTitle = recipeTitle.trim().lowercase()
        titleCache[normalizedTitle]?.let { return it.url }

        val fallback = currentVideoUrl
            ?.takeIf(YouTubeVideoUrlUtils::isFallbackUrl)
            ?: buildSearchUrl(recipeTitle)
        titleCache[normalizedTitle] = CachedVideo(
            url = fallback,
            isFallback = true
        )
        logDebug("Usando fallback local para: $recipeTitle")
        return fallback
    }

    suspend fun resolveForPersistence(
        currentVideoUrl: String?,
        recipeTitle: String,
        youTubeApiKey: String
    ): String {
        if (!currentVideoUrl.isNullOrBlank() && !YouTubeVideoUrlUtils.needsResolution(currentVideoUrl)) {
            return YouTubeVideoUrlUtils.normalizeVideoUrl(currentVideoUrl)
        }

        val normalizedTitle = recipeTitle.trim().lowercase()
        titleCache[normalizedTitle]
            ?.takeIf { !it.isFallback }
            ?.let { return it.url }

        val lock = inFlightTitleLocks.getOrPut(normalizedTitle) { Mutex() }
        return lock.withLock {
            titleCache[normalizedTitle]
                ?.takeIf { !it.isFallback }
                ?.let { return@withLock it.url }

            val resolvedFromApi = runCatching {
                videoSearchEngine.searchBestVideoUrl(
                    recipeTitle = recipeTitle,
                    queries = getSearchQueries(recipeTitle),
                    priorityKeywords = getPriorityKeywords(),
                    apiKey = youTubeApiKey
                )
            }.getOrNull()

            val resolved = resolvedFromApi
                ?.takeIf { !YouTubeVideoUrlUtils.needsResolution(it) }
                ?.let(YouTubeVideoUrlUtils::normalizeVideoUrl)
                ?: currentVideoUrl
                    ?.takeIf(YouTubeVideoUrlUtils::isFallbackUrl)
                ?: buildSearchUrl(recipeTitle)

            val cachedVideo = CachedVideo(
                url = resolved,
                isFallback = YouTubeVideoUrlUtils.isFallbackUrl(resolved)
            )
            titleCache[normalizedTitle] = cachedVideo
            if (!cachedVideo.isFallback) {
                logDebug("Video resuelto con búsqueda exacta para: $recipeTitle")
            } else {
                logDebug("Video resuelto con fallback para: $recipeTitle")
            }
            resolved
        }
    }

    fun needsResolution(currentVideoUrl: String?): Boolean {
        return YouTubeVideoUrlUtils.needsResolution(currentVideoUrl)
    }

    private fun buildSearchUrl(recipeTitle: String): String {
        return YouTubeVideoUrlUtils.buildSearchUrl(recipeTitle)
    }

    fun getSearchQueries(recipeTitle: String): List<String> = listOf(
        recipeTitle,
        "como preparar $recipeTitle",
        "como hacer $recipeTitle",
        "$recipeTitle receta paso a paso",
        "receta de $recipeTitle",
        "$recipeTitle receta casera",
        "how to make $recipeTitle",
        "$recipeTitle recipe tutorial"
    )

    fun getPriorityKeywords(): List<String> = listOf(
        "receta", "tutorial", "original", "casera", "preparar", "como hacer", "paso a paso"
    )

    fun clearCache() {
        titleCache.clear()
    }

    private fun logDebug(message: String) {
        runCatching { Log.d(TAG, message) }
    }
}
