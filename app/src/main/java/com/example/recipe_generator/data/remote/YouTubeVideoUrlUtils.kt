package com.example.recipe_generator.data.remote

import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

private const val YOUTUBE_SEARCH_BASE = "https://www.youtube.com/results?search_query="
private const val YOUTUBE_VIDEO_FILTER = "&sp=EgIQAQ%253D%253D"

object YouTubeVideoUrlUtils {

    fun buildEmbedUrl(videoId: String): String {
        return "https://www.youtube.com/embed/$videoId" +
            "?autoplay=1&controls=1&fs=1&playsinline=1&rel=0&hl=es"
    }

    fun buildSearchUrl(recipeTitle: String): String {
        val query = "como preparar ${recipeTitle.trim()} receta tutorial"
        val encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString())
        return "$YOUTUBE_SEARCH_BASE$encodedQuery$YOUTUBE_VIDEO_FILTER"
    }

    fun isFallbackUrl(url: String?): Boolean {
        if (url.isNullOrBlank()) return false
        val uri = parseUri(url) ?: return false
        val host = uri.host?.lowercase().orEmpty()
        return host.contains("youtube.com") && uri.path?.startsWith("/results") == true
    }

    fun isExactVideoUrl(url: String?): Boolean = extractVideoId(url) != null

    fun needsResolution(url: String?): Boolean {
        if (url.isNullOrBlank()) return true
        if (isFallbackUrl(url)) return true
        return !isExactVideoUrl(url)
    }

    fun normalizeVideoUrl(url: String): String {
        val videoId = extractVideoId(url) ?: return url
        return "https://www.youtube.com/watch?v=$videoId"
    }

    fun extractVideoId(url: String?): String? {
        if (url.isNullOrBlank()) return null

        val uri = parseUri(url) ?: return null
        val host = uri.host?.lowercase().orEmpty()
        val pathSegments = uri.path
            ?.split("/")
            .orEmpty()
            .filter { it.isNotBlank() }

        return when {
            host == "youtu.be" || host.endsWith(".youtu.be") ->
                pathSegments.firstOrNull()?.takeIf(::isValidVideoId)

            host.contains("youtube.com") -> {
                val fromQuery = findQueryParameter(uri.query, "v")?.takeIf(::isValidVideoId)
                if (fromQuery != null) {
                    fromQuery
                } else {
                    pathSegments
                        .zipWithNext()
                        .firstOrNull { (segment, _) ->
                            segment in setOf("watch", "embed", "shorts", "live", "v")
                        }
                        ?.second
                        ?.takeIf(::isValidVideoId)
                        ?: pathSegments.lastOrNull()?.takeIf(::isValidVideoId)
                }
            }

            else -> null
        }
    }

    private fun isValidVideoId(value: String): Boolean =
        value.length == 11 && value.all { it.isLetterOrDigit() || it == '_' || it == '-' }

    private fun parseUri(url: String): URI? =
        runCatching { URI(url) }.getOrNull()

    private fun findQueryParameter(query: String?, name: String): String? {
        if (query.isNullOrBlank()) return null
        return query
            .split("&")
            .mapNotNull { part ->
                val separatorIndex = part.indexOf('=')
                if (separatorIndex <= 0) return@mapNotNull null
                val key = part.substring(0, separatorIndex)
                val value = part.substring(separatorIndex + 1)
                key to value
            }
            .firstOrNull { (key, _) -> key == name }
            ?.second
    }
}
