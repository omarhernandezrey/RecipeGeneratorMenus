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
        return selectedTypes.any { type ->
            category.contains(type, ignoreCase = true) ||
            type.contains(category, ignoreCase = true)
        }
    }

    /**
     * Filtro de dieta — lógica de negocio central.
     * Por ahora retorna true (la lógica real depende de etiquetas
     * que se agregarán en F3-27 al completar el DatabaseSeeder).
     */
    private fun matchesDiet(recipe: Recipe, selectedDiets: Set<String>): Boolean {
        if (selectedDiets.isEmpty()) return true
        return true // Se implementará en F3-28 con datos completos
    }
}
