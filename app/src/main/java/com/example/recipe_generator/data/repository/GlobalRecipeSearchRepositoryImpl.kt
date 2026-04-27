package com.example.recipe_generator.data.repository

import android.net.Uri
import android.util.Log
import com.example.recipe_generator.data.local.QueryNormalizer
import com.example.recipe_generator.data.local.SemanticRecipeMatcher
import com.example.recipe_generator.data.remote.GlobalRecipeFirestoreDataSource
import com.example.recipe_generator.data.remote.GlobalRecipeFirestoreRecipe
import com.example.recipe_generator.data.remote.TheMealDbMealDto
import com.example.recipe_generator.data.remote.TheMealDbService
import com.example.recipe_generator.domain.model.GlobalRecipe
import com.example.recipe_generator.domain.model.GlobalRecipeSource
import com.example.recipe_generator.domain.repository.GlobalRecipeSearchRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.max
import kotlin.math.min

class GlobalRecipeSearchRepositoryImpl(
    private val firestoreDataSource: GlobalRecipeFirestoreDataSource,
    private val mealDbService: TheMealDbService,
    private val persistFallbackToFirestore: Boolean = false
) : GlobalRecipeSearchRepository {

    private companion object {
        private const val TAG = "GlobalRecipeSearchRepo"
        private const val MAX_MEMORY_CACHE_ENTRIES = 150
        private const val MEMORY_CACHE_TTL_MILLIS = 30 * 60 * 1000L
        private const val FIRESTORE_CACHE_TTL_MILLIS = 24 * 60 * 60 * 1000L
        private const val MIN_ACCEPTABLE_MEAL_SCORE = 0.42
        private val GENERIC_MEAL_TOKENS = setOf(
            "food", "dish", "meal", "recipe", "comida", "platillo", "plato"
        )
    }

    private data class CachedRecipe(
        val recipe: GlobalRecipe,
        val cachedAt: Long
    )

    private data class RankedMeal(
        val meal: TheMealDbMealDto,
        val score: Double
    )

    private data class SearchMetrics(
        val totalQueries: Int = 0,
        val firestoreHits: Int = 0,
        val mealDbHits: Int = 0,
        val fallbacks: Int = 0
    )

    private val memoryCacheMutex = Mutex()
    private val memoryCache = object : LinkedHashMap<String, CachedRecipe>(
        MAX_MEMORY_CACHE_ENTRIES,
        0.75f,
        true
    ) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, CachedRecipe>?): Boolean {
            return size > MAX_MEMORY_CACHE_ENTRIES
        }
    }

    private val inFlight = mutableMapOf<String, Deferred<GlobalRecipe>>()
    private val inFlightMutex = Mutex()
    private val metricsMutex = Mutex()
    private var metrics = SearchMetrics()

    override suspend fun searchRecipe(query: String): GlobalRecipe = coroutineScope {
        val rawQuery = query.trim()
        val normalizedQuery = QueryNormalizer.normalize(rawQuery)
        require(normalizedQuery.isNotBlank()) { "La búsqueda no puede estar vacía" }

        getCached(normalizedQuery)?.let { return@coroutineScope it }

        val deferred = getOrCreateInFlight(normalizedQuery, rawQuery, this)
        try {
            deferred.await().also { result ->
                if (isCacheable(result, query = normalizedQuery)) {
                    putCached(normalizedQuery, result)
                }
            }
        } finally {
            inFlightMutex.withLock {
                if (inFlight[normalizedQuery] === deferred) {
                    inFlight.remove(normalizedQuery)
                }
            }
        }
    }

    private suspend fun getOrCreateInFlight(
        normalizedQuery: String,
        rawQuery: String,
        scope: CoroutineScope
    ): Deferred<GlobalRecipe> {
        return inFlightMutex.withLock {
            inFlight[normalizedQuery]?.let { return@withLock it }
            scope.async { resolveRecipe(rawQuery, normalizedQuery) }
                .also { inFlight[normalizedQuery] = it }
        }
    }

    private suspend fun resolveRecipe(rawQuery: String, normalizedQuery: String): GlobalRecipe {
        val now = System.currentTimeMillis()
        val queryTokens = QueryNormalizer.tokenize(rawQuery).ifEmpty {
            QueryNormalizer.tokenize(normalizedQuery, removeStopWords = false)
        }
        val expandedTokens = QueryNormalizer.expandTokens(queryTokens)
        val defaultFallbackImage = selectBestFallbackImageUrl(
            name = rawQuery,
            country = "",
            candidateKeywords = expandedTokens
        )

        val fromFirestore = firestoreDataSource.findByAlias(normalizedQuery)
        if (fromFirestore != null &&
            !isExpired(fromFirestore.updatedAt, FIRESTORE_CACHE_TTL_MILLIS, now)
        ) {
            recordMetrics { copy(totalQueries = totalQueries + 1, firestoreHits = firestoreHits + 1) }
            val result = fromFirestore.toDomain(
                source = GlobalRecipeSource.FIRESTORE,
                fallbackImageUrl = defaultFallbackImage,
                defaultAlias = normalizedQuery,
                queryTokens = queryTokens
            )
            logSourceUsed(GlobalRecipeSource.FIRESTORE, rawQuery, result)
            return result
        }

        val bestMeal = searchFromMealDb(
            rawQuery = rawQuery,
            normalizedQuery = normalizedQuery,
            queryTokens = queryTokens,
            expandedTokens = expandedTokens
        )

        if (bestMeal != null && bestMeal.score >= MIN_ACCEPTABLE_MEAL_SCORE) {
            val meal = bestMeal.meal
            val normalizedName = QueryNormalizer.normalize(meal.strMeal)
            val imageUrl = ensureRelevantImageUrl(
                imageUrl = meal.strMealThumb.orEmpty(),
                queryTokens = queryTokens,
                fallbackImageUrl = selectBestFallbackImageUrl(
                    name = meal.strMeal,
                    country = meal.strArea.orEmpty(),
                    candidateKeywords = expandedTokens
                )
            )

            val aliases = (listOf(
                normalizedQuery,
                normalizedName
            ) + expandedTokens)
                .filter { it.isNotBlank() }
                .distinct()
                .take(20)

            val keywords = buildKeywords(
                mealName = meal.strMeal,
                category = meal.strCategory.orEmpty(),
                area = meal.strArea.orEmpty(),
                tags = meal.strTags.orEmpty(),
                queryTokens = queryTokens,
                expandedTokens = expandedTokens
            )

            val firestoreRecipe = GlobalRecipeFirestoreRecipe(
                id = meal.idMeal.ifBlank { "meal-$normalizedName" },
                nombre = meal.strMeal,
                imagen = imageUrl,
                aliases = aliases,
                pais = meal.strArea.orEmpty(),
                keywords = keywords,
                updatedAt = now
            )
            if (isPersistable(firestoreRecipe, normalizedQuery)) {
                firestoreDataSource.save(firestoreRecipe)
            }
            recordMetrics { copy(totalQueries = totalQueries + 1, mealDbHits = mealDbHits + 1) }
            val result = firestoreRecipe.toDomain(
                source = GlobalRecipeSource.THEMEALDB,
                fallbackImageUrl = imageUrl,
                defaultAlias = normalizedQuery,
                queryTokens = queryTokens
            )
            logSourceUsed(GlobalRecipeSource.THEMEALDB, rawQuery, result)
            return result
        }

        if (fromFirestore != null) {
            recordMetrics { copy(totalQueries = totalQueries + 1, firestoreHits = firestoreHits + 1) }
            val result = fromFirestore.toDomain(
                source = GlobalRecipeSource.FIRESTORE,
                fallbackImageUrl = defaultFallbackImage,
                defaultAlias = normalizedQuery,
                queryTokens = queryTokens
            )
            logSourceUsed(GlobalRecipeSource.FIRESTORE, rawQuery, result)
            return result
        }

        val fallbackRecipe = GlobalRecipe(
            id = "fallback-$normalizedQuery",
            nombre = rawQuery,
            imagen = selectBestFallbackImageUrl(
                name = rawQuery,
                country = "",
                candidateKeywords = expandedTokens
            ),
            aliases = listOf(normalizedQuery),
            pais = "",
            keywords = (expandedTokens + normalizedQuery).distinct(),
            source = GlobalRecipeSource.FALLBACK
        )

        if (persistFallbackToFirestore && isPersistable(fallbackRecipe.toFirestore(now), normalizedQuery)) {
            firestoreDataSource.save(
                fallbackRecipe.toFirestore(now)
            )
        }

        recordMetrics { copy(totalQueries = totalQueries + 1, fallbacks = fallbacks + 1) }
        logSourceUsed(GlobalRecipeSource.FALLBACK, rawQuery, fallbackRecipe)
        return fallbackRecipe
    }

    private suspend fun searchFromMealDb(
        rawQuery: String,
        normalizedQuery: String,
        queryTokens: List<String>,
        expandedTokens: List<String>
    ): RankedMeal? {
        val candidateQueries = buildCandidateQueries(rawQuery, normalizedQuery, expandedTokens)
        var best: RankedMeal? = null

        candidateQueries.forEach { candidate ->
            try {
                val meals = mealDbService.searchMeals(candidate).meals.orEmpty()
                meals.forEach { meal ->
                    val score = mealScore(meal, normalizedQuery, queryTokens, expandedTokens)
                    if (best == null || score > best!!.score) {
                        best = RankedMeal(meal = meal, score = score)
                    }
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Throwable) {
                // Permitir fallback automático.
            }
        }

        return best
    }

    private fun buildCandidateQueries(
        rawQuery: String,
        normalizedQuery: String,
        expandedTokens: List<String>
    ): List<String> {
        val candidates = linkedSetOf<String>()
        candidates += rawQuery.trim()
        candidates += normalizedQuery
        candidates += expandedTokens.take(4).joinToString(" ")
        expandedTokens.take(6).forEach { token ->
            candidates += token
        }
        return candidates
            .map { it.trim() }
            .filter { it.length >= 2 }
            .distinct()
            .take(8)
    }

    private fun mealScore(
        meal: TheMealDbMealDto,
        normalizedQuery: String,
        queryTokens: List<String>,
        expandedTokens: List<String>
    ): Double {
        val mealName = QueryNormalizer.normalize(meal.strMeal)
        val mealTokens = QueryNormalizer.tokenize(meal.strMeal)
        val areaTokens = QueryNormalizer.tokenize(meal.strArea.orEmpty())

        val levenshtein = normalizedLevenshteinSimilarity(normalizedQuery, mealName)
        val queryAndExpanded = (queryTokens + expandedTokens).distinct()
        val overlap = jaccard(queryAndExpanded, mealTokens)
        val semantic = SemanticRecipeMatcher.semanticSimilarity(
            queryTokens = queryAndExpanded,
            candidateTokens = mealTokens,
            contextTokens = areaTokens
        )

        val containsBonus = when {
            mealName == normalizedQuery -> 0.32
            mealName.contains(normalizedQuery) || normalizedQuery.contains(mealName) -> 0.20
            else -> 0.0
        }
        val areaBonus = if (queryAndExpanded.any { it in areaTokens }) 0.05 else 0.0
        val genericPenalty = if (isGenericMeal(mealName, mealTokens)) 0.12 else 0.0

        return (0.33 * levenshtein) + (0.20 * overlap) + (0.30 * semantic) + containsBonus + areaBonus - genericPenalty
    }

    private fun buildKeywords(
        mealName: String,
        category: String,
        area: String,
        tags: String,
        queryTokens: List<String>,
        expandedTokens: List<String>
    ): List<String> {
        val allTokens = buildList {
            addAll(QueryNormalizer.tokenize(mealName))
            addAll(QueryNormalizer.tokenize(category))
            addAll(QueryNormalizer.tokenize(area))
            addAll(QueryNormalizer.tokenize(tags))
            addAll(queryTokens)
            addAll(expandedTokens)
            addAll(
                SemanticRecipeMatcher.enrichmentTokens(
                    queryTokens = queryTokens,
                    expandedTokens = expandedTokens,
                    countryTokens = QueryNormalizer.tokenize(area)
                )
            )
        }
        return allTokens
            .map { QueryNormalizer.normalize(it) }
            .filter { it.length > 2 }
            .distinct()
            .take(30)
    }

    private fun fallbackImageUrl(
        name: String,
        country: String,
        candidateKeywords: List<String>
    ): String {
        val tokens = buildList {
            addAll(QueryNormalizer.tokenize(name))
            addAll(QueryNormalizer.tokenize(country))
            addAll(candidateKeywords)
            addAll(
                SemanticRecipeMatcher.enrichmentTokens(
                    queryTokens = QueryNormalizer.tokenize(name),
                    expandedTokens = candidateKeywords,
                    countryTokens = QueryNormalizer.tokenize(country)
                )
            )
            add("food")
            add("recipe")
            add("dish")
        }
            .map { QueryNormalizer.normalize(it) }
            .filter { it.length > 2 }
            .distinct()
            .take(8)
            .ifEmpty { listOf("food", "recipe", "dish") }

        return buildStableFallbackImageUrl(tokens)
    }

    private fun selectBestFallbackImageUrl(
        name: String,
        country: String,
        candidateKeywords: List<String>
    ): String {
        val baseTokens = buildList {
            addAll(QueryNormalizer.tokenize(name))
            addAll(QueryNormalizer.tokenize(country))
            addAll(candidateKeywords)
        }
            .filter { it.length > 2 }
            .distinct()

        val prioritized = (baseTokens.take(6) + listOf("food", "recipe", "dish"))
            .distinct()
            .ifEmpty { listOf("food", "recipe", "dish") }
        return buildStableFallbackImageUrl(prioritized)
    }

    private fun ensureRelevantImageUrl(
        imageUrl: String,
        queryTokens: List<String>,
        fallbackImageUrl: String
    ): String {
        if (imageUrl.isBlank()) return fallbackImageUrl
        val lowerUrl = imageUrl.lowercase()
        if (!isValidHttpUrl(lowerUrl)) {
            return fallbackImageUrl
        }
        return imageUrl
    }

    private fun buildStableFallbackImageUrl(tokens: List<String>): String {
        val safeText = tokens
            .map { QueryNormalizer.normalize(it) }
            .filter { it.isNotBlank() }
            .distinct()
            .take(6)
            .joinToString(" ")
            .ifBlank { "food recipe" }
        return "https://via.placeholder.com/600x400.png?text=${Uri.encode(safeText)}"
    }

    private suspend fun getCached(key: String): GlobalRecipe? = memoryCacheMutex.withLock {
        val cached = memoryCache[key] ?: return@withLock null
        if (isExpired(cached.cachedAt, MEMORY_CACHE_TTL_MILLIS)) {
            memoryCache.remove(key)
            return@withLock null
        }
        cached.recipe
    }

    private suspend fun putCached(key: String, recipe: GlobalRecipe) {
        memoryCacheMutex.withLock {
            memoryCache[key] = CachedRecipe(recipe = recipe, cachedAt = System.currentTimeMillis())
        }
    }

    private suspend fun recordMetrics(transform: SearchMetrics.() -> SearchMetrics) {
        metricsMutex.withLock {
            metrics = metrics.transform()
            Log.d(
                TAG,
                "queries=${metrics.totalQueries} firestore=${metrics.firestoreHits} mealdb=${metrics.mealDbHits} fallback=${metrics.fallbacks}"
            )
        }
    }

    private fun isCacheable(recipe: GlobalRecipe, query: String): Boolean {
        val normalizedName = QueryNormalizer.normalize(recipe.nombre)
        val overlap = jaccard(
            QueryNormalizer.tokenize(query),
            QueryNormalizer.tokenize(normalizedName)
        )
        val seemsGeneric = isGenericMeal(normalizedName, QueryNormalizer.tokenize(normalizedName))
        return overlap >= 0.10 || (!seemsGeneric && recipe.source != GlobalRecipeSource.FALLBACK)
    }

    private fun isPersistable(recipe: GlobalRecipeFirestoreRecipe, query: String): Boolean {
        val normalizedName = QueryNormalizer.normalize(recipe.nombre)
        val queryTokens = QueryNormalizer.tokenize(query)
        val nameTokens = QueryNormalizer.tokenize(normalizedName)
        val overlap = jaccard(queryTokens, nameTokens)
        return recipe.imagen.isNotBlank() && isValidHttpUrl(recipe.imagen) && overlap >= 0.12
    }

    private fun isGenericMeal(normalizedMealName: String, mealTokens: List<String>): Boolean {
        if (normalizedMealName.isBlank()) return true
        val tokenCount = mealTokens.size
        val genericTokenCount = mealTokens.count { it in GENERIC_MEAL_TOKENS }
        return tokenCount <= 1 || genericTokenCount >= tokenCount
    }

    private fun isValidHttpUrl(url: String): Boolean {
        return url.startsWith("https://") || url.startsWith("http://")
    }

    private fun logSourceUsed(source: GlobalRecipeSource, query: String, recipe: GlobalRecipe) {
        Log.i(
            TAG,
            "source=${source.name} query=\"$query\" result=\"${recipe.nombre}\" image=\"${recipe.imagen}\""
        )
    }
}

private fun GlobalRecipeFirestoreRecipe.toDomain(
    source: GlobalRecipeSource,
    fallbackImageUrl: String,
    defaultAlias: String,
    queryTokens: List<String>
): GlobalRecipe {
    val normalizedAliases = aliases.ifEmpty { listOf(defaultAlias) }.distinct()
    val normalizedKeywords = keywords.ifEmpty { normalizedAliases }
    val image = if (imagen.isBlank()) fallbackImageUrl else imagen

    return GlobalRecipe(
        id = id,
        nombre = nombre,
        imagen = image,
        aliases = normalizedAliases,
        pais = pais,
        keywords = normalizedKeywords,
        source = source
    )
}

private fun isExpired(timestampMillis: Long, ttlMillis: Long, now: Long = System.currentTimeMillis()): Boolean {
    if (timestampMillis <= 0L) return true
    return (now - timestampMillis) > ttlMillis
}

private fun normalizedLevenshteinSimilarity(a: String, b: String): Double {
    if (a.isEmpty() && b.isEmpty()) return 1.0
    if (a.isEmpty() || b.isEmpty()) return 0.0
    val distance = levenshteinDistance(a, b)
    val maxLen = max(a.length, b.length).coerceAtLeast(1)
    return 1.0 - (distance.toDouble() / maxLen.toDouble())
}

private fun levenshteinDistance(a: String, b: String): Int {
    if (a == b) return 0
    if (a.isEmpty()) return b.length
    if (b.isEmpty()) return a.length

    val dp = IntArray(b.length + 1) { it }
    for (i in 1..a.length) {
        var prevDiagonal = dp[0]
        dp[0] = i
        for (j in 1..b.length) {
            val temp = dp[j]
            val cost = if (a[i - 1] == b[j - 1]) 0 else 1
            dp[j] = min(
                min(dp[j] + 1, dp[j - 1] + 1),
                prevDiagonal + cost
            )
            prevDiagonal = temp
        }
    }
    return dp[b.length]
}

private fun jaccard(a: List<String>, b: List<String>): Double {
    if (a.isEmpty() || b.isEmpty()) return 0.0
    val setA = a.toSet()
    val setB = b.toSet()
    val intersection = setA.intersect(setB).size.toDouble()
    val union = setA.union(setB).size.toDouble().coerceAtLeast(1.0)
    return intersection / union
}

private fun GlobalRecipe.toFirestore(updatedAt: Long): GlobalRecipeFirestoreRecipe {
    return GlobalRecipeFirestoreRecipe(
        id = id,
        nombre = nombre,
        imagen = imagen,
        aliases = aliases,
        pais = pais,
        keywords = keywords,
        updatedAt = updatedAt
    )
}

