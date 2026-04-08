package com.example.recipe_generator.presentation.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.example.recipe_generator.R
import com.example.recipe_generator.domain.usecase.GetMenuForDayUseCase
import com.example.recipe_generator.presentation.detail.RecipeDetailActivity
import com.example.recipe_generator.presentation.navigation.ComposeScreenFragment
import com.example.recipe_generator.presentation.navigation.TopLevelDestination
import kotlinx.coroutines.launch

class HomeFragment : ComposeScreenFragment() {
    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(
            appContainer.getMenuForDayUseCase,
            appContainer.weeklyPlanRepository,
            appContainer.requireAuthenticatedUserId()
        )
    }

    @Composable
    override fun ScreenContent() {
        val selectedDay by homeViewModel.selectedDay.collectAsStateWithLifecycle()
        val recipes by homeViewModel.recipes.collectAsStateWithLifecycle()
        val userId = appContainer.requireAuthenticatedUserId()
        val favoriteRecipeIds by appContainer.favoritesRepository
            .getFavoriteIds(userId)
            .collectAsStateWithLifecycle(initialValue = emptySet())
        val coroutineScope = rememberCoroutineScope()

        RecipeListScreen(
            selectedDay = selectedDay,
            recipes = recipes,
            onDaySelected = homeViewModel::selectDay,
            selectedNavItem = TopLevelDestination.Home.navItemIndex,
            onNavItemSelected = ::navigateToTopLevel,
            favoriteRecipeIds = favoriteRecipeIds,
            onToggleFavorite = { recipeId ->
                coroutineScope.launch {
                    appContainer.favoritesRepository.toggleFavorite(userId, recipeId)
                }
            },
            // F3-05: lanza RecipeDetailActivity con Intent.putExtra(recipeId) — LF5
            onRecipeSelected = { recipe ->
                startActivity(
                    RecipeDetailActivity.newIntent(requireContext(), recipe.id)
                )
            },
            // F2-04/F2-05: navega al layout de dos paneles (LeftMenu + Content)
            onProfileClick = {
                findNavController().navigate(R.id.action_home_to_leftMenu)
            }
        )
    }
}

private class HomeViewModelFactory(
    private val getMenuForDayUseCase: GetMenuForDayUseCase,
    private val weeklyPlanRepository: com.example.recipe_generator.domain.repository.WeeklyPlanRepository,
    private val userId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return HomeViewModel(getMenuForDayUseCase, weeklyPlanRepository, userId) as T
    }
}
