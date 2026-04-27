package com.example.recipe_generator.data.local

import kotlin.math.sqrt

object SemanticRecipeMatcher {

    private val conceptLexicon: Map<String, Set<String>> = mapOf(
        "breakfast" to setOf("desayuno", "breakfast", "petitdejeuner"),
        "lunch" to setOf("almuerzo", "lunch", "comida"),
        "dinner" to setOf("cena", "dinner", "supper"),
        "soup" to setOf("sopa", "soup", "broth", "caldo", "potage"),
        "stew" to setOf("guiso", "stew", "ragout"),
        "rice" to setOf("arroz", "rice", "riz"),
        "noodle" to setOf("fideo", "noodle", "noodles", "pasta"),
        "bread" to setOf("pan", "bread", "pain"),
        "egg" to setOf("huevo", "egg", "eggs", "oeuf"),
        "chicken" to setOf("pollo", "chicken"),
        "beef" to setOf("res", "beef", "carne", "vacuno"),
        "pork" to setOf("cerdo", "pork", "chancho"),
        "fish" to setOf("pescado", "fish", "poisson"),
        "shrimp" to setOf("camaron", "shrimp", "prawn", "gamba"),
        "vegetarian" to setOf("vegetariano", "vegetarian"),
        "vegan" to setOf("vegano", "vegan"),
        "dessert" to setOf("postre", "dessert", "dolce"),
        "spicy" to setOf("picante", "spicy", "hot"),
        "streetfood" to setOf("callejero", "street", "streetfood"),
        "colombian" to setOf("colombia", "colombiano", "colombiana"),
        "mexican" to setOf("mexico", "mexicano", "mexicana"),
        "italian" to setOf("italia", "italiano", "italiana"),
        "japanese" to setOf("japon", "japones", "japonesa", "japanese"),
        "vietnamese" to setOf("vietnam", "vietnamita", "vietnamese")
    )

    private val tokenToConcept: Map<String, String> = buildMap {
        conceptLexicon.forEach { (concept, variants) ->
            variants.forEach { variant ->
                put(QueryNormalizer.normalize(variant), concept)
            }
        }
    }

    fun semanticSimilarity(
        queryTokens: List<String>,
        candidateTokens: List<String>,
        contextTokens: List<String> = emptyList()
    ): Double {
        val queryVector = vectorize(queryTokens + contextTokens)
        val candidateVector = vectorize(candidateTokens + contextTokens)
        return cosine(queryVector, candidateVector)
    }

    fun enrichmentTokens(
        queryTokens: List<String>,
        expandedTokens: List<String>,
        countryTokens: List<String> = emptyList()
    ): List<String> {
        val concepts = conceptBag(queryTokens + expandedTokens + countryTokens)
        return (queryTokens + expandedTokens + countryTokens + concepts)
            .map { QueryNormalizer.normalize(it) }
            .filter { it.length > 2 }
            .distinct()
    }

    private fun vectorize(tokens: List<String>): Map<String, Double> {
        if (tokens.isEmpty()) return emptyMap()
        val features = mutableMapOf<String, Double>()
        val normalizedTokens = tokens
            .map { QueryNormalizer.normalize(it) }
            .filter { it.isNotBlank() }

        normalizedTokens.forEach { token ->
            features["t:$token"] = (features["t:$token"] ?: 0.0) + 1.0
            tokenToConcept[token]?.let { concept ->
                features["c:$concept"] = (features["c:$concept"] ?: 0.0) + 2.0
            }
            charTrigrams(token).forEach { tri ->
                features["g:$tri"] = (features["g:$tri"] ?: 0.0) + 0.35
            }
        }

        return features
    }

    private fun conceptBag(tokens: List<String>): List<String> {
        return tokens
            .map { QueryNormalizer.normalize(it) }
            .mapNotNull { token -> tokenToConcept[token] }
            .distinct()
    }

    private fun charTrigrams(token: String): List<String> {
        val value = token.trim()
        if (value.length < 3) return listOf(value)
        return (0..value.length - 3).map { index ->
            value.substring(index, index + 3)
        }
    }

    private fun cosine(a: Map<String, Double>, b: Map<String, Double>): Double {
        if (a.isEmpty() || b.isEmpty()) return 0.0
        var dot = 0.0
        a.forEach { (key, value) ->
            dot += value * (b[key] ?: 0.0)
        }
        val normA = sqrt(a.values.sumOf { it * it })
        val normB = sqrt(b.values.sumOf { it * it })
        if (normA == 0.0 || normB == 0.0) return 0.0
        return dot / (normA * normB)
    }
}

