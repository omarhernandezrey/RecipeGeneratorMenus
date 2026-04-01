package com.example.recipe_generator.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipe_generator.domain.model.Recipe
import com.example.recipe_generator.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * ViewModel de la pantalla de favoritos.
 *
 * Combina el stream de favoritos con los filtros activos
 * (búsqueda por texto + categoría) usando Flow.combine.
 *
 * Se completa en F3-33.
 * Capa: Presentation
 */
class FavoritesViewModel(
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Todos")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _filteredRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val filteredRecipes: StateFlow<List<Recipe>> = _filteredRecipes.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                favoritesRepository.getFavoriteRecipes(),
                _searchQuery,
                _selectedCategory
            ) { recipes, query, category ->
                recipes
                    .filter { recipe ->
                        query.isBlank() ||
                        recipe.title.contains(query, ignoreCase = true) ||
                        recipe.description.contains(query, ignoreCase = true)
                    }
                    .filter { recipe ->
                        category == "Todos" || recipe.category == category
                    }
            }.collect { filtered ->
                _filteredRecipes.value = filtered
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
    }
}
