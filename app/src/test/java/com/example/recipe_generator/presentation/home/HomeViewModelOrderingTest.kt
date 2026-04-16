package com.example.recipe_generator.presentation.home

import com.example.recipe_generator.domain.model.Recipe
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeViewModelOrderingTest {

    @Test
    fun sortedForHome_placesBreakfastLunchAndDinnerInExpectedOrder() {
        val recipes = listOf(
            recipe(id = "dinner", title = "Cena ligera", category = "Cena"),
            recipe(id = "lunch", title = "Almuerzo casero", category = "Almuerzo"),
            recipe(id = "breakfast", title = "Desayuno rapido", category = "Desayuno")
        )

        val orderedIds = recipes.sortedForHome().map { it.id }

        assertEquals(listOf("breakfast", "lunch", "dinner"), orderedIds)
    }

    @Test
    fun sortedForHome_usesSubtitleWhenCategoryIsBlank() {
        val recipes = listOf(
            recipe(id = "dinner", title = "Cena ligera", category = "", categorySubtitle = "Cena"),
            recipe(id = "breakfast", title = "Desayuno rapido", category = "", categorySubtitle = "Desayuno"),
            recipe(id = "lunch", title = "Almuerzo casero", category = "", categorySubtitle = "Almuerzo")
        )

        val orderedIds = recipes.sortedForHome().map { it.id }

        assertEquals(listOf("breakfast", "lunch", "dinner"), orderedIds)
    }

    private fun recipe(
        id: String,
        title: String,
        category: String,
        categorySubtitle: String = category
    ) = Recipe(
        id = id,
        title = title,
        imageRes = "",
        timeInMinutes = 10,
        calories = 100,
        difficulty = "Fácil",
        category = category,
        categorySubtitle = categorySubtitle,
        description = "",
        dayOfWeek = "Lunes"
    )
}
