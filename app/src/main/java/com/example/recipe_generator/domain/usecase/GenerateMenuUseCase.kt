package com.example.recipe_generator.domain.usecase

import com.example.recipe_generator.domain.model.Recipe
import com.example.recipe_generator.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Caso de uso — Generar menú semanal filtrado.
 *
 * Aplica filtros de preferencias del usuario sobre todas las recetas
 * disponibles en Room para generar un menú personalizado.
 *
 * Filtros aplicados:
 * - Nivel de dificultad máximo (Fácil=1, Medio=2, Difícil=3)
 * - Tipos de comida seleccionados (Desayuno, Almuerzo, Cena, Snack)
 * - Preferencias dietéticas (Vegetariano, Vegano, Sin Gluten, etc.)
 *
 * Capa: Domain
 */
class GenerateMenuUseCase(
    private val recipeRepository: RecipeRepository
) {
    /**
     * Ejecuta el caso de uso.
     *
     * @param maxDifficulty Nivel máximo: "Fácil" | "Medio" | "Difícil"
     * @param selectedTypes Tipos de comida seleccionados por el usuario
     * @param selectedDiets Preferencias dietéticas activas
     * @return Flow con lista filtrada de recetas para el menú generado
     */
    operator fun invoke(
        maxDifficulty: String = "Difícil",
        selectedTypes: Set<String> = emptySet(),
        selectedDiets: Set<String> = emptySet()
    ): Flow<List<Recipe>> {
        return recipeRepository.getAllRecipes().map { recipes ->
            recipes.filter { recipe ->
                matchesDifficulty(recipe.difficulty, maxDifficulty) &&
                matchesType(recipe.category, selectedTypes) &&
                matchesDiet(recipe, selectedDiets)
            }
        }
    }

    private fun matchesDifficulty(recipeDifficulty: String, maxDifficulty: String): Boolean {
        val order = listOf("Fácil", "Medio", "Difícil")
        val recipeLevel = order.indexOf(recipeDifficulty)
        val maxLevel = order.indexOf(maxDifficulty)
        return recipeLevel <= maxLevel
    }

    private fun matchesType(category: String, selectedTypes: Set<String>): Boolean {
        if (selectedTypes.isEmpty()) return true
        return selectedTypes.any { type -> category.equals(type, ignoreCase = true) }
    }

    /**
     * Filtro de dieta — lógica de negocio central (F3-28).
     *
     * Analiza los ingredientTags de la receta para determinar si
     * cumple con las preferencias dietéticas del usuario.
     * - Vegetariano: sin carne ni pescado en ingredientes
     * - Vegano: sin productos animales
     * - Sin Gluten: sin ingredientes con gluten
     */
    private fun matchesDiet(recipe: Recipe, selectedDiets: Set<String>): Boolean {
        if (selectedDiets.isEmpty()) return true

        val allTags = (recipe.ingredientTags + recipe.ingredients.map { it.name })
            .map { it.lowercase() }

        val meatKeywords = listOf("pollo", "chicken", "carne", "res", "cerdo", "pork",
            "beef", "cordero", "pechuga", "costilla", "jamón", "tocino", "bacon")
        val fishKeywords = listOf("salmón", "salmon", "atún", "tuna", "pescado", "fish",
            "camarón", "shrimp", "mariscos")
        val animalKeywords = meatKeywords + fishKeywords +
            listOf("huevo", "egg", "leche", "milk", "queso", "cheese",
                "mantequilla", "butter", "crema", "yogur", "yogurt", "miel", "honey")
        val glutenKeywords = listOf("harina", "flour", "trigo", "wheat", "pan", "bread",
            "pasta", "croissant", "avena", "oat", "cebada", "barley")

        return selectedDiets.all { diet ->
            when {
                diet.equals("Vegetariano", ignoreCase = true) || diet.equals("Vegetariana", ignoreCase = true) ->
                    allTags.none { tag -> meatKeywords.any { keyword -> tag.contains(keyword) } } &&
                    allTags.none { tag -> fishKeywords.any { keyword -> tag.contains(keyword) } }

                diet.equals("Vegano", ignoreCase = true) || diet.equals("Vegana", ignoreCase = true) ->
                    allTags.none { tag -> animalKeywords.any { keyword -> tag.contains(keyword) } }

                diet.equals("Sin Gluten", ignoreCase = true) || diet.equals("SinGluten", ignoreCase = true) ->
                    allTags.none { tag -> glutenKeywords.any { keyword -> tag.contains(keyword) } }

                else -> true
            }
        }
    }
}
