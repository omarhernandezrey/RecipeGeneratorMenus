package com.example.recipe_generator.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipe_generator.domain.model.Recipe
import com.example.recipe_generator.domain.usecase.GetMenuForDayUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel de la pantalla de inicio (Menú Semanal).
 *
 * Expone estado como StateFlow inmutable hacia el Fragment/Activity.
 * Nunca importa nada de Android UI (sin View, Context, Fragment).
 *
 * Patrón MVVM: Fragment observa → ViewModel procesa → UseCase ejecuta
 *
 * Se completa en F3-31.
 * Capa: Presentation
 */
class HomeViewModel(
    private val getMenuForDayUseCase: GetMenuForDayUseCase
) : ViewModel() {

    private val _selectedDay = MutableStateFlow("Lunes")
    val selectedDay: StateFlow<String> = _selectedDay.asStateFlow()

    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadMenuForDay("Lunes")
    }

    fun selectDay(day: String) {
        _selectedDay.value = day
        loadMenuForDay(day)
    }

    private fun loadMenuForDay(day: String) {
        viewModelScope.launch {
            _isLoading.value = true
            getMenuForDayUseCase(day).collect { recipeList ->
                _recipes.value = recipeList
                _isLoading.value = false
            }
        }
    }
}
