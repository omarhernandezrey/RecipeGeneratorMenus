package com.example.recipe_generator.data.remote

import android.util.Log
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.Normalizer
import java.util.concurrent.ConcurrentHashMap

class YouTubeVideoSearchService : RecipeVideoSearchEngine {

    private data class VideoCandidate(
        val title: String,
        val url: String
    )

    companion object {
        private const val TAG = "YouTubeVideoSearchSvc"
        private const val REQUEST_DEBOUNCE_MS = 350L
        private const val MIN_SCORE_FOR_MATCH = 40

        private val STOPWORDS = setOf(
            "con", "para", "como", "una", "unos", "unas", "the", "and", "del", "las", "los",
            "por", "que", "recipe", "receta"
        )

        private val NEGATIVE_KEYWORDS = setOf(
            "asmr", "mukbang", "review", "reaction", "comiendo", "probando", "restaurante",
            "challenge", "broma", "cover", "remix"
        )

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

    override suspend fun searchBestVideoUrl(
        recipeTitle: String,
        queries: List<String>,
        priorityKeywords: List<String>,
        apiKey: String
    ): String? = withContext(Dispatchers.IO) {
        var bestUrl: String? = null
        var bestScore = Int.MIN_VALUE

        queries.distinct().forEach { query ->
            val videos = searchVideosByQuery(query, apiKey)
            videos.forEach { candidate ->
                val score = scoreVideoCandidate(candidate.title, recipeTitle, priorityKeywords)
                if (score > bestScore) {
                    bestScore = score
                    bestUrl = candidate.url
                }
            }
        }

        if (bestScore < MIN_SCORE_FOR_MATCH) null else bestUrl
    }

    private suspend fun searchVideosByQuery(query: String, apiKey: String): List<VideoCandidate> =
        withContext(Dispatchers.IO) {
            val cacheKey = "${apiKey.ifBlank { "html" }}|${query.trim().lowercase()}"
            queryCache[cacheKey]?.let { return@withContext it }
            debounceQuery(cacheKey)
            queryCache[cacheKey]?.let { return@withContext it }

            val candidates = runCatching {
                val apiCandidates = if (apiKey.isBlank()) {
                    emptyList()
                } else {
                    searchVideosByApi(query, apiKey)
                }
                if (apiCandidates.isNotEmpty()) {
                    apiCandidates
                } else {
                    searchVideosByHtml(query)
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

    private suspend fun searchVideosByApi(query: String, apiKey: String): List<VideoCandidate> {
        val response = apiService.searchVideos(
            query = query.trim(),
            apiKey = apiKey
        )
        return response.items.orEmpty().mapNotNull { item ->
            val videoId = item.id?.videoId.orEmpty()
            val title = item.snippet?.title.orEmpty()
            if (videoId.isBlank() || title.isBlank()) return@mapNotNull null
            VideoCandidate(
                title = title,
                url = "https://www.youtube.com/watch?v=$videoId"
            )
        }
    }

    private fun searchVideosByHtml(query: String): List<VideoCandidate> {
        val encodedQuery = URLEncoder.encode(query.trim(), StandardCharsets.UTF_8.toString())
        val url = URL("https://www.youtube.com/results?search_query=$encodedQuery&sp=EgIQAQ%253D%253D")
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 8_000
            readTimeout = 8_000
            setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 Chrome/123.0 Mobile Safari/537.36"
            )
            setRequestProperty("Accept-Language", "es-ES,es;q=0.9,en;q=0.8")
        }

        try {
            val responseCode = connection.responseCode
            if (responseCode !in 200..299) {
                throw IllegalStateException("YouTube respondió HTTP $responseCode")
            }

            connection.inputStream.bufferedReader().use { reader ->
                val html = reader.readText()
                val initialData = extractInitialDataJson(html)
                return parseVideoCandidates(initialData)
            }
        } finally {
            connection.disconnect()
        }
    }

    private fun extractInitialDataJson(html: String): String {
        val patterns = listOf(
            Regex("""var ytInitialData = (\{.*?\});""", setOf(RegexOption.DOT_MATCHES_ALL)),
            Regex("""window\["ytInitialData"] = (\{.*?\});""", setOf(RegexOption.DOT_MATCHES_ALL))
        )

        patterns.forEach { pattern ->
            pattern.find(html)?.groupValues?.getOrNull(1)?.let { return it }
        }

        throw IllegalStateException("No se pudo extraer ytInitialData")
    }

    private fun parseVideoCandidates(initialDataJson: String): List<VideoCandidate> {
        val root = JsonParser.parseString(initialDataJson)
        val collected = linkedMapOf<String, VideoCandidate>()
        collectVideoCandidates(root) { candidate ->
            val normalizedUrl = YouTubeVideoUrlUtils.normalizeVideoUrl(candidate.url)
            val videoId = YouTubeVideoUrlUtils.extractVideoId(normalizedUrl) ?: return@collectVideoCandidates
            collected.putIfAbsent(
                videoId,
                candidate.copy(url = normalizedUrl)
            )
        }
        return collected.values.toList()
    }

    private fun collectVideoCandidates(
        element: JsonElement,
        onCandidate: (VideoCandidate) -> Unit
    ) {
        when {
            element.isJsonArray -> {
                element.asJsonArray.forEach { child ->
                    collectVideoCandidates(child, onCandidate)
                }
            }

            element.isJsonObject -> {
                val obj = element.asJsonObject
                obj.getAsJsonObject("videoRenderer")
                    ?.toVideoCandidate()
                    ?.let(onCandidate)
                obj.entrySet().forEach { (_, value) ->
                    collectVideoCandidates(value, onCandidate)
                }
            }
        }
    }

    private fun JsonObject.toVideoCandidate(): VideoCandidate? {
        val videoId = get("videoId")?.asString.orEmpty()
        val title = getAsJsonObject("title")
            ?.extractText()
            ?.trim()
            .orEmpty()

        if (videoId.isBlank() || title.isBlank()) return null
        return VideoCandidate(
            title = title,
            url = "https://www.youtube.com/watch?v=$videoId"
        )
    }

    private fun JsonObject.extractText(): String? {
        if (has("simpleText")) return get("simpleText")?.asString

        val runs = getAsJsonArray("runs") ?: return null
        return runs
            .mapNotNull { run ->
                run.asJsonObject.get("text")?.asString
            }
            .joinToString(separator = "")
            .takeIf { it.isNotBlank() }
    }

    internal fun scoreVideoCandidate(
        videoTitle: String,
        recipeTitle: String,
        priorityKeywords: List<String>
    ): Int {
        val normalizedVideo = normalizeText(videoTitle)
        val normalizedRecipe = normalizeText(recipeTitle)
        val recipeTokens = tokenize(recipeTitle)
        val matchedTokens = recipeTokens.count { token -> normalizedVideo.contains(token) }

        var score = 0

        if (normalizedVideo == normalizedRecipe) {
            score += 220
        }

        if (normalizedVideo.contains(normalizedRecipe) && normalizedRecipe.isNotBlank()) {
            score += 160
        }

        score += matchedTokens * 18
        if (recipeTokens.isNotEmpty() && matchedTokens == recipeTokens.size) {
            score += 55
        } else if (matchedTokens >= 2) {
            score += 20
        }

        priorityKeywords.forEach { keyword ->
            if (normalizedVideo.contains(normalizeText(keyword))) score += 8
        }

        NEGATIVE_KEYWORDS.forEach { keyword ->
            if (normalizedVideo.contains(keyword)) score -= 18
        }
        if (normalizedVideo.contains("short")) score -= 10
        if (normalizedVideo.contains("shorts")) score -= 25
        if (normalizedVideo.contains("live")) score -= 15

        return score
    }

    private fun tokenize(text: String): List<String> =
        normalizeText(text)
            .split(" ")
            .filter { token -> token.length > 2 && token !in STOPWORDS }

    private fun normalizeText(text: String): String =
        Normalizer.normalize(text.lowercase(), Normalizer.Form.NFD)
            .replace("\\p{Mn}+".toRegex(), "")
            .replace("[^a-z0-9 ]".toRegex(), " ")
            .replace("\\s+".toRegex(), " ")
            .trim()
}
