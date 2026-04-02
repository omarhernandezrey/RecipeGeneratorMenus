package com.example.recipe_generator.presentation.favorites

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.recipe_generator.domain.repository.FavoritesRepository
import com.example.recipe_generator.presentation.detail.RecipeDetailActivity
import com.example.recipe_generator.presentation.navigation.ComposeScreenFragment
import com.example.recipe_generator.presentation.navigation.TopLevelDestination
import kotlinx.coroutines.launch

class FavoritesFragment : ComposeScreenFragment() {
    private val favoritesViewModel: FavoritesViewModel by viewModels {
        FavoritesViewModelFactory(appContainer.favoritesRepository)
    }

    @Composable
    override fun ScreenContent() {
        val filteredRecipes by favoritesViewModel.filteredRecipes.collectAsStateWithLifecycle()
        val favoriteRecipes by favoritesViewModel.favoriteRecipes.collectAsStateWithLifecycle()
        val searchQuery by favoritesViewModel.searchQuery.collectAsStateWithLifecycle()
        val selectedCategory by favoritesViewModel.selectedCategory.collectAsStateWithLifecycle()
        val coroutineScope = rememberCoroutineScope()
        val categories = listOf("Todos") + favoriteRecipes
            .map { normalizeCategory(it.category) }
            .distinct()

        FavoritesScreen(
            recipes = filteredRecipes,
            categories = categories,
            query = searchQuery,
            onQueryChange = favoritesViewModel::onSearchQueryChanged,
            selectedCategory = selectedCategory,
            onCategorySelected = favoritesViewModel::onCategorySelected,
            selectedNavItem = TopLevelDestination.Favorites.navItemIndex,
            onNavItemSelected = ::navigateToTopLevel,
            // F3-05: lanza RecipeDetailActivity con Intent.putExtra(recipeId) — LF5
            onRecipeSelected = { recipe ->
                startActivity(
                    RecipeDetailActivity.newIntent(requireContext(), recipe.id)
                )
            },
            onRemoveFavorite = { recipeId ->
                coroutineScope.launch {
                    appContainer.favoritesRepository.removeFavorite(recipeId)
                }
            }
        )
    }
}

private class FavoritesViewModelFactory(
    private val favoritesRepository: FavoritesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return FavoritesViewModel(favoritesRepository) as T
    }
}
