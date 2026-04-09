package com.example.recipe_generator.presentation.generator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipe_generator.domain.repository.UserRecipeRepository
import com.example.recipe_generator.domain.repository.WeeklyPlanRepository
import com.example.recipe_generator.domain.usecase.GenerateMenuUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class GeneratorUiState(
    val isGenerating: Boolean = false,
    val planSaved: Boolean = false,
    val recipesFound: Int = 0,
    val error: String? = null
)

class MenuGeneratorViewModel(
    private val generateMenuUseCase: GenerateMenuUseCase,
    private val weeklyPlanRepository: WeeklyPlanRepository,
    private val userRecipeRepository: UserRecipeRepository,
    private val userId: String
) : ViewModel() {

    private val _maxDifficulty  = MutableStateFlow("Difícil")
    val maxDifficulty: StateFlow<String> = _maxDifficulty.asStateFlow()

    private val _selectedDiets  = MutableStateFlow(setOf<String>())
    val selectedDiets: StateFlow<Set<String>> = _selectedDiets.asStateFlow()

    private val _selectedTypes  = MutableStateFlow(setOf<String>())
    val selectedTypes: StateFlow<Set<String>> = _selectedTypes.asStateFlow()

    private val _portions       = MutableStateFlow(4)
    val portions: StateFlow<Int> = _portions.asStateFlow()

    private val _uiState        = MutableStateFlow(GeneratorUiState())
    val uiState: StateFlow<GeneratorUiState> = _uiState.asStateFlow()

    fun setDifficulty(v: String)   { _maxDifficulty.value = v }
    fun toggleDiet(d: String)      { _selectedDiets.value = _selectedDiets.value.let { if (d in it) it - d else it + d } }
    fun toggleType(t: String)      { _selectedTypes.value = _selectedTypes.value.let { if (t in it) it - t else it + t } }
    fun setPortions(n: Int)        { _portions.value = n }
    fun clearPlanSaved()           { _uiState.value = _uiState.value.copy(planSaved = false) }

    /**
     * Genera un plan semanal completo (7 días × 3 comidas) y lo guarda en Room/Firestore.
     * Usa recetas del catálogo + recetas personales del usuario.
     */
    fun generateAndSavePlan() {
        viewModelScope.launch {
            _uiState.value = GeneratorUiState(isGenerating = true)

            runCatching {
                // 1. Obtener recetas del catálogo con filtros de preferencias
                val catalog = generateMenuUseCase(
                    maxDifficulty = _maxDifficulty.value,
                    selectedTypes = _selectedTypes.value,
                    selectedDiets = _selectedDiets.value
                ).first()

                // 2. Obtener recetas del usuario
                val userRecipes = userRecipeRepository.getMyRecipes(userId).first()

                // 3. Separar por tipo de comida — combina catálogo + recetas personales
                val breakfasts = buildPool(
                    catalog.filter { matchesMeal(it.category, "Desayuno") }.map { it.id },
                    userRecipes.filter { matchesMeal(it.category, "Desayuno") || matchesMeal(it.mealType, "Desayuno") }.map { it.id }
                )
                val lunches = buildPool(
                    catalog.filter { matchesMeal(it.category, "Almuerzo") }.map { it.id },
                    userRecipes.filter { matchesMeal(it.category, "Almuerzo") || matchesMeal(it.mealType, "Almuerzo") }.map { it.id }
                )
                val dinners = buildPool(
                    catalog.filter { matchesMeal(it.category, "Cena") }.map { it.id },
                    userRecipes.filter { matchesMeal(it.category, "Cena") || matchesMeal(it.mealType, "Cena") }.map { it.id }
                )

                val totalFound = breakfasts.size + lunches.size + dinners.size
                if (totalFound == 0) error("No hay recetas disponibles con los filtros seleccionados.")

                // 4. Asignar una receta por slot (circular si hay menos de 7)
                val days = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
                for ((i, day) in days.withIndex()) {
                    if (breakfasts.isNotEmpty()) {
                        weeklyPlanRepository.setMeal(userId, day, "Desayuno", breakfasts[i % breakfasts.size])
                    }
                    if (lunches.isNotEmpty()) {
                        weeklyPlanRepository.setMeal(userId, day, "Almuerzo", lunches[i % lunches.size])
                    }
                    if (dinners.isNotEmpty()) {
                        weeklyPlanRepository.setMeal(userId, day, "Cena", dinners[i % dinners.size])
                    }
                }

                _uiState.value = GeneratorUiState(planSaved = true, recipesFound = totalFound)

            }.onFailure { e ->
                _uiState.value = GeneratorUiState(error = e.message ?: "Error al generar el plan")
            }
        }
    }

    private fun matchesMeal(field: String, mealType: String) =
        field.equals(mealType, ignoreCase = true)

    private fun buildPool(catalogIds: List<String>, userIds: List<String>): List<String> =
        (userIds + catalogIds).distinct().shuffled()
}
