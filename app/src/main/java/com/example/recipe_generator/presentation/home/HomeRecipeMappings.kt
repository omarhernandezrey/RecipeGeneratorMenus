package com.example.recipe_generator.presentation.home

import com.example.recipe_generator.domain.model.Ingredient
import com.example.recipe_generator.domain.model.Recipe
import com.example.recipe_generator.domain.model.RecipeStep
import com.example.recipe_generator.domain.model.UserRecipe

internal fun UserRecipe.toRecipe(): Recipe {
    val resolvedMealType = mealType.ifBlank { category }
    return Recipe(
        id = id,
        title = title,
        imageRes = imageRes,
        timeInMinutes = timeInMinutes,
        calories = calories,
        difficulty = difficulty,
        category = resolvedMealType,
        categorySubtitle = category.ifBlank { "Receta personal" },
        description = description,
        dayOfWeek = dayOfWeek,
        videoYoutube = videoYoutube,
        ingredients = ingredients.mapIndexed { index, ingredient ->
            Ingredient(
                id = index,
                name = ingredient,
                quantity = "",
                unit = ""
            )
        },
        ingredientTags = emptyList(),
        steps = steps.mapIndexed { index, step ->
            RecipeStep(
                id = index,
                stepNumber = index + 1,
                title = "Paso ${index + 1}",
                description = step
            )
        }
    )
}
