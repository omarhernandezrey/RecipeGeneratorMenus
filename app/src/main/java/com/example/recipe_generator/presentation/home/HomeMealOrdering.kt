package com.example.recipe_generator.presentation.home

import com.example.recipe_generator.domain.model.Recipe

internal fun List<Recipe>.sortedForHome(): List<Recipe> =
    sortedBy { homeMealTypeOrder(it.category.ifBlank { it.categorySubtitle }) }

private fun homeMealTypeOrder(mealType: String): Int = when (mealType.trim().lowercase()) {
    "desayuno" -> 0
    "almuerzo" -> 1
    "cena" -> 2
    else -> 3
}
