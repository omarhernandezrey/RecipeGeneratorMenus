package com.example.recipe_generator.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipe_generator.domain.model.Recipe
import com.example.recipe_generator.domain.usecase.GetRecipeDetailUseCase
import com.example.recipe_generator.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel del detalle de receta.
 *
 * Recibe recipeId desde RecipeDetailActivity via Bundle (LF5).
 * Expone la receta completa y gestiona el toggle de favorito.
 *
 * Se completa en F3-32.
 * Capa: Presentation
 */
class RecipeDetailViewModel(
    private val getRecipeDetailUseCase: GetRecipeDetailUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _recipe = MutableStateFlow<Recipe?>(null)
    val recipe: StateFlow<Recipe?> = _recipe.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadRecipe(recipeId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            getRecipeDetailUseCase(recipeId).collect { recipe ->
                _recipe.value = recipe
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite() {
        val currentRecipe = _recipe.value ?: return
        viewModelScope.launch {
            toggleFavoriteUseCase(currentRecipe.id)
        }
    }
}
