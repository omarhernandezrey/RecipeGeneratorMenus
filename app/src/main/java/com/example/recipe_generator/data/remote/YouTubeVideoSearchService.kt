package com.example.recipe_generator.data.remote

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.ConcurrentHashMap

class YouTubeVideoSearchService {

    private data class VideoCandidate(
        val title: String,
        val url: String
    )

    companion object {
        private const val TAG = "YouTubeVideoSearchSvc"
        private const val REQUEST_DEBOUNCE_MS = 350L
        private val apiService: YouTubeApiService by lazy {
            Retrofit.Builder()
                .baseUrl("https://www.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(YouTubeApiService::class.java)
        }
    }

    private val queryCache = ConcurrentHashMap<String, List<VideoCandidate>>()
    private val queryDebounceLocks = ConcurrentHashMap<String, Mutex>()
    private val lastQueryRequestAt = ConcurrentHashMap<String, Long>()

    suspend fun searchBestVideoUrl(
        recipeTitle: String,
        queries: List<String>,
        priorityKeywords: List<String>,
        apiKey: String
    ): String? = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) return@withContext null

        var bestUrl: String? = null
        var bestScore = Int.MIN_VALUE

        queries.distinct().forEach { query ->
            val videos = searchVideosByQuery(query, apiKey)
            videos.forEach { candidate ->
                val score = scoreVideoTitle(candidate.title, recipeTitle, priorityKeywords)
                if (score > bestScore) {
                    bestScore = score
                    bestUrl = candidate.url
                }
            }
        }

        if (bestScore <= 0) null else bestUrl
    }

    private suspend fun searchVideosByQuery(query: String, apiKey: String): List<VideoCandidate> =
        withContext(Dispatchers.IO) {
            val cacheKey = "$apiKey|${query.trim().lowercase()}"
            queryCache[cacheKey]?.let { return@withContext it }
            debounceQuery(cacheKey)
            queryCache[cacheKey]?.let { return@withContext it }

            val candidates = runCatching {
                val response = apiService.searchVideos(
                    query = query.trim(),
                    apiKey = apiKey
                )
                response.items.orEmpty().mapNotNull { item ->
                    val videoId = item.id?.videoId.orEmpty()
                    val title = item.snippet?.title.orEmpty()
                    if (videoId.isBlank() || title.isBlank()) return@mapNotNull null
                    VideoCandidate(
                        title = title,
                        url = "https://www.youtube.com/watch?v=$videoId"
                    )
                }
            }.getOrElse {
                Log.w(TAG, "Error searching YouTube: ${it.message}")
                emptyList()
            }

            queryCache[cacheKey] = candidates
            candidates
        }

    private suspend fun debounceQuery(cacheKey: String) {
        val lock = queryDebounceLocks.getOrPut(cacheKey) { Mutex() }
        lock.withLock {
            val now = System.currentTimeMillis()
            val lastRequest = lastQueryRequestAt[cacheKey] ?: 0L
            val remaining = REQUEST_DEBOUNCE_MS - (now - lastRequest)
            if (remaining > 0L) delay(remaining)
            lastQueryRequestAt[cacheKey] = System.currentTimeMillis()
        }
    }

    private fun scoreVideoTitle(
        videoTitle: String,
        recipeTitle: String,
        priorityKeywords: List<String>
    ): Int {
        val normalizedVideo = videoTitle.lowercase()
        val normalizedRecipe = recipeTitle.lowercase().trim()

        var score = 0

        if (normalizedVideo.contains(normalizedRecipe)) {
            score += 100
        } else {
            normalizedRecipe.split(" ")
                .filter { it.length > 2 }
                .forEach { token ->
                    if (normalizedVideo.contains(token)) score += 12
                }
        }

        priorityKeywords.forEach { keyword ->
            if (normalizedVideo.contains(keyword.lowercase())) score += 6
        }

        if (normalizedVideo.contains("short")) score -= 5

        return score
    }
}
