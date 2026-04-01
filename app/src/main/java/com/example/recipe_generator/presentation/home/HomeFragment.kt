package com.example.recipe_generator.presentation.home

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
import com.example.recipe_generator.domain.usecase.GetMenuForDayUseCase
import com.example.recipe_generator.presentation.detail.RecipeDetailScreen
import com.example.recipe_generator.presentation.navigation.ComposeScreenFragment
import com.example.recipe_generator.presentation.navigation.TopLevelDestination
import kotlinx.coroutines.launch

class HomeFragment : ComposeScreenFragment() {
    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(appContainer.getMenuForDayUseCase)
    }

    @Composable
    override fun ScreenContent() {
        val selectedDay by homeViewModel.selectedDay.collectAsStateWithLifecycle()
        val recipes by homeViewModel.recipes.collectAsStateWithLifecycle()
        val favoriteRecipeIds by appContainer.favoritesRepository
            .getFavoriteIds()
            .collectAsStateWithLifecycle(initialValue = emptySet())
        val coroutineScope = rememberCoroutineScope()
        var selectedRecipeId by rememberSaveable { mutableStateOf<String?>(null) }

        val selectedRecipe = recipes
            .firstOrNull { it.id == selectedRecipeId }
            ?.let { recipe ->
                recipe.copy(isFavorite = recipe.id in favoriteRecipeIds)
            }

        if (selectedRecipe != null) {
            RecipeDetailScreen(
                recipe = selectedRecipe,
                selectedNavItem = TopLevelDestination.Home.navItemIndex,
                onNavItemSelected = ::navigateToTopLevel,
                isFavorite = selectedRecipe.id in favoriteRecipeIds,
                onToggleFavorite = { recipeId ->
                    coroutineScope.launch {
                        appContainer.favoritesRepository.toggleFavorite(recipeId)
                    }
                },
                onBackClick = { selectedRecipeId = null }
            )
        } else {
            RecipeListScreen(
                selectedDay = selectedDay,
                recipes = recipes,
                onDaySelected = homeViewModel::selectDay,
                selectedNavItem = TopLevelDestination.Home.navItemIndex,
                onNavItemSelected = ::navigateToTopLevel,
                favoriteRecipeIds = favoriteRecipeIds,
                onToggleFavorite = { recipeId ->
                    coroutineScope.launch {
                        appContainer.favoritesRepository.toggleFavorite(recipeId)
                    }
                },
                onRecipeSelected = { recipe ->
                    selectedRecipeId = recipe.id
                }
            )
        }
    }
}

private class HomeViewModelFactory(
    private val getMenuForDayUseCase: GetMenuForDayUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return HomeViewModel(getMenuForDayUseCase) as T
    }
}
