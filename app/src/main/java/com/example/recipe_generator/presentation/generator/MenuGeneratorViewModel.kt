package com.example.recipe_generator.presentation.generator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipe_generator.domain.model.Recipe
import com.example.recipe_generator.domain.usecase.GenerateMenuUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel del generador de menú.
 *
 * Mantiene los parámetros de preferencias del usuario y expone
 * el resultado del menú generado por GenerateMenuUseCase.
 *
 * Se completa en F3-34.
 * Capa: Presentation
 */
class MenuGeneratorViewModel(
    private val generateMenuUseCase: GenerateMenuUseCase
) : ViewModel() {

    // ── Parámetros del generador ──────────────────────────────────────

    private val _maxDifficulty = MutableStateFlow("Difícil")
    val maxDifficulty: StateFlow<String> = _maxDifficulty.asStateFlow()

    private val _selectedDiets = MutableStateFlow(setOf<String>())
    val selectedDiets: StateFlow<Set<String>> = _selectedDiets.asStateFlow()

    private val _selectedTypes = MutableStateFlow(setOf<String>())
    val selectedTypes: StateFlow<Set<String>> = _selectedTypes.asStateFlow()

    private val _portions = MutableStateFlow(4)
    val portions: StateFlow<Int> = _portions.asStateFlow()

    // ── Resultado ─────────────────────────────────────────────────────

    private val _generatedMenu = MutableStateFlow<List<Recipe>>(emptyList())
    val generatedMenu: StateFlow<List<Recipe>> = _generatedMenu.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()
    private var generateJob: Job? = null

    // ── Acciones ──────────────────────────────────────────────────────

    fun setDifficulty(difficulty: String) { _maxDifficulty.value = difficulty }

    fun toggleDiet(diet: String) {
        _selectedDiets.value = if (diet in _selectedDiets.value)
            _selectedDiets.value - diet else _selectedDiets.value + diet
    }

    fun toggleType(type: String) {
        _selectedTypes.value = if (type in _selectedTypes.value)
            _selectedTypes.value - type else _selectedTypes.value + type
    }

    fun setPortions(count: Int) { _portions.value = count }

    fun generateMenu() {
        generateJob?.cancel()
        generateJob = viewModelScope.launch {
            _isGenerating.value = true
            generateMenuUseCase(
                maxDifficulty = _maxDifficulty.value,
                selectedTypes = _selectedTypes.value,
                selectedDiets = _selectedDiets.value
            ).collect { recipes ->
                _generatedMenu.value = recipes
                _isGenerating.value = false
            }
        }
    }
}
