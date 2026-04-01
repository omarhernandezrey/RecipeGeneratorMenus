package com.example.recipe_generator.presentation.favorites

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.recipe_generator.presentation.detail.RecipeDetailScreen
import com.example.recipe_generator.presentation.navigation.ComposeScreenFragment
import com.example.recipe_generator.presentation.navigation.TopLevelDestination
import kotlinx.coroutines.launch

class FavoritesFragment : ComposeScreenFragment() {
    private val favoritesViewModel: FavoritesViewModel by viewModels {
        FavoritesViewModelFactory(appContainer.favoritesRepository)
    }

    @Composable
    override fun ScreenContent() {
        val favoriteRecipes by favoritesViewModel.favoriteRecipes.collectAsStateWithLifecycle()
        val filteredRecipes by favoritesViewModel.filteredRecipes.collectAsStateWithLifecycle()
        val searchQuery by favoritesViewModel.searchQuery.collectAsStateWithLifecycle()
        val selectedCategory by favoritesViewModel.selectedCategory.collectAsStateWithLifecycle()
        val coroutineScope = rememberCoroutineScope()
        var selectedRecipeId by rememberSaveable { mutableStateOf<String?>(null) }
        val selectedRecipe = favoriteRecipes.firstOrNull { it.id == selectedRecipeId }
        val categories = listOf("Todos") + favoriteRecipes
            .map { normalizeCategory(it.category) }
            .distinct()

        if (selectedRecipe != null) {
            RecipeDetailScreen(
                recipe = selectedRecipe,
                selectedNavItem = TopLevelDestination.Favorites.navItemIndex,
                onNavItemSelected = ::navigateToTopLevel,
                isFavorite = true,
                onToggleFavorite = { recipeId ->
                    coroutineScope.launch {
                        appContainer.favoritesRepository.toggleFavorite(recipeId)
                    }
                },
                onBackClick = { selectedRecipeId = null }
            )
        } else {
            FavoritesScreen(
                recipes = filteredRecipes,
                categories = categories,
                query = searchQuery,
                onQueryChange = favoritesViewModel::onSearchQueryChanged,
                selectedCategory = selectedCategory,
                onCategorySelected = favoritesViewModel::onCategorySelected,
                selectedNavItem = TopLevelDestination.Favorites.navItemIndex,
                onNavItemSelected = ::navigateToTopLevel,
                onRecipeSelected = { recipe ->
                    selectedRecipeId = recipe.id
                },
                onRemoveFavorite = { recipeId ->
                    coroutineScope.launch {
                        appContainer.favoritesRepository.removeFavorite(recipeId)
                    }
                }
            )
        }
    }
}

private class FavoritesViewModelFactory(
    private val favoritesRepository: com.example.recipe_generator.domain.repository.FavoritesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return FavoritesViewModel(favoritesRepository) as T
    }
}
