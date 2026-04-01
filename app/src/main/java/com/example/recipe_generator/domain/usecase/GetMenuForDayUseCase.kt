package com.example.recipe_generator.domain.usecase

import com.example.recipe_generator.domain.model.Recipe
import com.example.recipe_generator.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow

/**
 * Caso de uso — Obtener menú del día seleccionado.
 *
 * Encapsula la lógica de negocio: obtener recetas de un día
 * y garantizar que el orden sea Desayuno → Almuerzo → Cena.
 *
 * Principio S de SOLID: una sola responsabilidad.
 *
 * Capa: Domain
 */
class GetMenuForDayUseCase(
    private val recipeRepository: RecipeRepository
) {
    /**
     * Ejecuta el caso de uso.
     * @param day Día de la semana: "Lunes" | "Martes" | ... | "Domingo"
     * @return Flow con lista de recetas ordenadas por tipo de comida.
     */
    operator fun invoke(day: String): Flow<List<Recipe>> {
        return recipeRepository.getRecipesByDay(day)
    }
}
