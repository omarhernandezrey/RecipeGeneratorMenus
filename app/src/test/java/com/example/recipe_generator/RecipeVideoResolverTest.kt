package com.example.recipe_generator

import com.example.recipe_generator.data.remote.RecipeVideoResolver
import com.example.recipe_generator.data.remote.RecipeVideoSearchEngine
import com.example.recipe_generator.data.remote.YouTubeVideoSearchService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RecipeVideoResolverTest {

    @Test
    fun `resolveForPersistence ignora fallback cacheado y conserva video exacto`() = runTest {
        val resolver = RecipeVideoResolver(
            videoSearchEngine = FakeVideoSearchEngine(
                resolvedUrl = "https://youtu.be/abc123def45"
            )
        )

        val fallback = resolver.resolve(
            currentVideoUrl = null,
            recipeTitle = "Ajiaco"
        )

        assertTrue(fallback.contains("/results?search_query="))

        val resolved = resolver.resolveForPersistence(
            currentVideoUrl = null,
            recipeTitle = "Ajiaco",
            youTubeApiKey = ""
        )

        assertEquals(
            "https://www.youtube.com/watch?v=abc123def45",
            resolved
        )
    }

    @Test
    fun `needsResolution trata resultados de busqueda como no resueltos`() {
        val resolver = RecipeVideoResolver(
            videoSearchEngine = FakeVideoSearchEngine(
                resolvedUrl = "https://www.youtube.com/watch?v=abc123def45"
            )
        )

        assertTrue(
            resolver.needsResolution(
                "https://www.youtube.com/results?search_query=ajiaco&sp=EgIQAQ%253D%253D"
            )
        )
        assertFalse(
            resolver.needsResolution("https://youtu.be/abc123def45")
        )
    }

    @Test
    fun `scoreVideoCandidate prioriza receta real sobre contenido irrelevante`() {
        val service = YouTubeVideoSearchService()

        val exactScore = service.scoreVideoCandidate(
            videoTitle = "Ajiaco colombiano receta paso a paso",
            recipeTitle = "Ajiaco colombiano",
            priorityKeywords = listOf("receta", "paso a paso")
        )
        val noisyScore = service.scoreVideoCandidate(
            videoTitle = "Mukbang probando ajiaco en restaurante de Bogota",
            recipeTitle = "Ajiaco colombiano",
            priorityKeywords = listOf("receta", "paso a paso")
        )

        assertTrue(exactScore > noisyScore)
    }
}

private class FakeVideoSearchEngine(
    private val resolvedUrl: String?
) : RecipeVideoSearchEngine {
    override suspend fun searchBestVideoUrl(
        recipeTitle: String,
        queries: List<String>,
        priorityKeywords: List<String>,
        apiKey: String
    ): String? = resolvedUrl
}
