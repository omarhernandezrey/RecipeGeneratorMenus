@file:Suppress("SpellCheckingInspection")

package com.example.recipe_generator.domain.usecase

import com.example.recipe_generator.data.remote.GeminiRecipeDataSource
import com.example.recipe_generator.data.remote.RecipeImageResolver
import com.example.recipe_generator.domain.model.Ingredient
import com.example.recipe_generator.domain.model.Recipe
import com.example.recipe_generator.domain.model.RecipeStep
import com.example.recipe_generator.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import java.util.UUID

class GenerateMenuUseCase(
    private val recipeRepository: RecipeRepository,
    private val geminiDataSource: GeminiRecipeDataSource
) {
    operator fun invoke(
        maxDifficulty: String = "Difícil",
        selectedTypes: Set<String> = emptySet(),
        selectedDiets: Set<String> = emptySet()
    ): Flow<List<Recipe>> = flow {
        val localRecipes = recipeRepository.getAllRecipes().first().filter { recipe ->
            matchesDifficulty(recipe.difficulty, maxDifficulty) &&
                matchesType(recipe.category, selectedTypes) &&
                matchesDiet(recipe, selectedDiets)
        }

        if (localRecipes.isNotEmpty()) {
            emit(localRecipes)
        } else {
            val generated = mutableListOf<Recipe>()
            val typesToGenerate = selectedTypes.ifEmpty { setOf("Almuerzo", "Cena") }

            for (type in typesToGenerate) {
                val prompt = "Dieta: ${selectedDiets.joinToString()}, Dificultad: $maxDifficulty"
                val json = geminiDataSource.generateRecipe(prompt, type)

                json?.let {
                    val searchKeywords = it.optString("imageSearchKeywords", it.optString("title"))
                    val professionalImageUrl = RecipeImageResolver.resolveWithKeywords(searchKeywords)
                    val recipe = it.toDomainRecipe(type, professionalImageUrl)
                    generated.add(recipe)
                }
            }

            if (generated.isNotEmpty()) {
                recipeRepository.insertAll(generated)
                emit(generated)
            }
        }
    }

    private fun JSONObject.toDomainRecipe(category: String, confirmedImageUrl: String): Recipe {
        val ingredientsJson = optJSONArray("ingredients")
        val stepsJson = optJSONArray("steps")

        val ingredientList: List<Ingredient> = List(ingredientsJson?.length() ?: 0) { i ->
            Ingredient(
                id = 0,
                name = ingredientsJson!!.getString(i),
                quantity = "",
                unit = ""
            )
        }
        val ingredientTags: List<String> = List(ingredientsJson?.length() ?: 0) { i ->
            ingredientsJson!!.getString(i)
        }
        val stepList: List<RecipeStep> = List(stepsJson?.length() ?: 0) { i ->
            RecipeStep(
                id = 0,
                stepNumber = i + 1,
                title = "Paso ${i + 1}",
                description = stepsJson!!.getString(i)
            )
        }

        return Recipe(
            id = UUID.randomUUID().toString(),
            title = optString("title", "Receta Generada"),
            imageRes = "img_placeholder",
            imageUrl = confirmedImageUrl.ifBlank { null },
            timeInMinutes = optInt("timeInMinutes", 30),
            calories = optInt("calories", 400),
            difficulty = optString("difficulty", "Medio"),
            category = category,
            categorySubtitle = "Sugerencia del Chef IA",
            description = optString("description", ""),
            proteinGrams = optInt("proteinGrams", 0),
            carbsGrams = optInt("carbsGrams", 0),
            fatGrams = optInt("fatGrams", 0),
            dayOfWeek = "Hoy",
            ingredientTags = ingredientTags,
            ingredients = ingredientList,
            steps = stepList
        )
    }

    private fun matchesDifficulty(recipeDifficulty: String, maxDifficulty: String): Boolean {
        val order = listOf("Fácil", "Medio", "Difícil")
        return order.indexOf(recipeDifficulty) <= order.indexOf(maxDifficulty)
    }

    private fun matchesType(category: String, selectedTypes: Set<String>): Boolean {
        if (selectedTypes.isEmpty()) return true
        return selectedTypes.any { type -> category.equals(type, ignoreCase = true) }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun matchesDiet(recipe: Recipe, selectedDiets: Set<String>): Boolean {
        return true
    }
}
