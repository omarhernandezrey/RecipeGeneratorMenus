package com.example.recipe_generator.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipe_generator.domain.model.Recipe
import com.example.recipe_generator.domain.model.WeeklyPlan
import com.example.recipe_generator.domain.repository.WeeklyPlanRepository
import com.example.recipe_generator.domain.usecase.GetMenuForDayUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * ViewModel de la pantalla de inicio (Menú Semanal).
 *
 * E-02: Si el usuario tiene un plan semanal para el día seleccionado,
 * muestra esas recetas. Si no, muestra el catálogo general.
 *
 * Expone estado como StateFlow inmutable hacia el Fragment/Activity.
 * Nunca importa nada de Android UI (sin View, Context, Fragment).
 *
 * Patrón MVVM: Fragment observa → ViewModel procesa → UseCase ejecuta
 *
 * Capa: Presentation
 */
class HomeViewModel(
    private val getMenuForDayUseCase: GetMenuForDayUseCase,
    private val weeklyPlanRepository: WeeklyPlanRepository,
    private val userId: String
) : ViewModel() {

    private val _selectedDay = MutableStateFlow("Lunes")
    val selectedDay: StateFlow<String> = _selectedDay.asStateFlow()

    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes.asStateFlow()

    /** true cuando el día seleccionado tiene al menos una comida en el plan del usuario. */
    private val _hasPlan = MutableStateFlow(false)
    val hasPlan: StateFlow<Boolean> = _hasPlan.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadMenuForDay("Lunes")
    }

    fun selectDay(day: String) {
        _selectedDay.value = day
        loadMenuForDay(day)
    }

    private fun loadMenuForDay(day: String) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _isLoading.value = true
            combine(
                weeklyPlanRepository.getDay(userId, day),
                getMenuForDayUseCase(day)
            ) { planEntries, catalogRecipes ->
                val showPlan = planEntries.isNotEmpty()
                val recipes = if (showPlan) planEntries.map { it.toRecipe() } else catalogRecipes
                Pair(showPlan, recipes.sortedForHome())
            }.collect { (showPlan, recipeList) ->
                _hasPlan.value = showPlan
                _recipes.value = recipeList
                _isLoading.value = false
            }
        }
    }
}

private fun WeeklyPlan.toRecipe(): Recipe = Recipe(
    id = recipeId,
    title = recipeTitle.ifBlank { "Receta del plan" },
    imageRes = imageRes,
    timeInMinutes = 0,
    calories = 0,
    difficulty = "",
    category = mealType,
    categorySubtitle = mealType,
    description = "",
    dayOfWeek = dayOfWeek
)
