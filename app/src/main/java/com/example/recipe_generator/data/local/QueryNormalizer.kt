package com.example.recipe_generator.data.local

import java.text.Normalizer
import java.util.Locale

object QueryNormalizer {
    private val combiningMarksRegex = "\\p{M}+".toRegex()
    private val nonAlphaNumericRegex = "[^\\p{L}\\p{N}\\s]".toRegex()
    private val multiSpaceRegex = "\\s+".toRegex()
    private val splitRegex = "[\\s,;:.\\-_/]+".toRegex()

    private val stopWords = setOf(
        "de", "del", "la", "las", "el", "los", "y", "con", "a", "al", "en", "para", "por",
        "the", "a", "an", "of", "and", "with", "to", "for", "in", "on", "by",
        "da", "do", "di", "du", "et", "avec", "aux", "des"
    )

    private val synonymMap = mapOf(
        "beef" to setOf("res", "carne", "vacuno"),
        "chicken" to setOf("pollo"),
        "pork" to setOf("cerdo", "chancho"),
        "fish" to setOf("pescado"),
        "soup" to setOf("sopa", "potage"),
        "stew" to setOf("guiso"),
        "rice" to setOf("arroz"),
        "noodle" to setOf("fideo", "pasta"),
        "bread" to setOf("pan"),
        "egg" to setOf("huevo"),
        "dessert" to setOf("postre", "dolce"),
        "shrimp" to setOf("camaron", "gamba")
    )

    fun normalize(value: String): String {
        val lowered = value.lowercase(Locale.ROOT).trim()
        if (lowered.isBlank()) return ""

        val noAccents = Normalizer.normalize(lowered, Normalizer.Form.NFD)
            .replace(combiningMarksRegex, "")

        return noAccents
            .replace(nonAlphaNumericRegex, " ")
            .replace(multiSpaceRegex, " ")
            .trim()
    }

    fun tokenize(value: String, removeStopWords: Boolean = true): List<String> {
        val normalized = normalize(value)
        if (normalized.isBlank()) return emptyList()
        return normalized
            .split(splitRegex)
            .map(String::trim)
            .filter { it.length > 1 }
            .filter { !removeStopWords || it !in stopWords }
            .distinct()
    }

    fun expandTokens(tokens: List<String>): List<String> {
        if (tokens.isEmpty()) return emptyList()
        val expanded = linkedSetOf<String>()
        tokens.forEach { token ->
            expanded += token
            val englishKey = synonymMap.entries.firstOrNull { (_, values) ->
                token in values
            }?.key
            if (englishKey != null) {
                expanded += englishKey
                expanded += synonymMap[englishKey].orEmpty()
            } else {
                expanded += synonymMap[token].orEmpty()
            }
        }
        return expanded
            .map { normalize(it) }
            .filter { it.isNotBlank() }
            .distinct()
    }
}
