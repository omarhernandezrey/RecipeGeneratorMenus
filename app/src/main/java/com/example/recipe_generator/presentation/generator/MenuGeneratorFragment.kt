package com.example.recipe_generator.presentation.generator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.recipe_generator.presentation.navigation.ComposeScreenFragment
import com.example.recipe_generator.presentation.navigation.TopLevelDestination

class MenuGeneratorFragment : ComposeScreenFragment() {
    private val menuGeneratorViewModel: MenuGeneratorViewModel by viewModels {
        MenuGeneratorViewModelFactory(
            generateMenuUseCase  = appContainer.generateMenuUseCase,
            weeklyPlanRepository = appContainer.weeklyPlanRepository,
            userRecipeRepository = appContainer.userRecipeRepository,
            userId               = appContainer.requireAuthenticatedUserId()
        )
    }

    @Composable
    override fun ScreenContent() {
        val maxDifficulty  by menuGeneratorViewModel.maxDifficulty.collectAsStateWithLifecycle()
        val selectedDiets  by menuGeneratorViewModel.selectedDiets.collectAsStateWithLifecycle()
        val selectedTypes  by menuGeneratorViewModel.selectedTypes.collectAsStateWithLifecycle()
        val portions       by menuGeneratorViewModel.portions.collectAsStateWithLifecycle()
        val uiState        by menuGeneratorViewModel.uiState.collectAsStateWithLifecycle()

        MenuGeneratorScreen(
            selectedNavItem    = TopLevelDestination.Generator.navItemIndex,
            selectedDiets      = selectedDiets,
            onToggleDiet       = menuGeneratorViewModel::toggleDiet,
            selectedDifficulty = maxDifficulty,
            onDifficultySelected = menuGeneratorViewModel::setDifficulty,
            portions           = portions,
            onPortionsChange   = menuGeneratorViewModel::setPortions,
            selectedRecipeTypes = selectedTypes,
            onToggleRecipeType = menuGeneratorViewModel::toggleType,
            uiState            = uiState,
            onGenerateClick    = menuGeneratorViewModel::generateAndSavePlan,
            onNavigate         = ::navigateToTopLevel
        )
    }
}

private class MenuGeneratorViewModelFactory(
    private val generateMenuUseCase: com.example.recipe_generator.domain.usecase.GenerateMenuUseCase,
    private val weeklyPlanRepository: com.example.recipe_generator.domain.repository.WeeklyPlanRepository,
    private val userRecipeRepository: com.example.recipe_generator.domain.repository.UserRecipeRepository,
    private val userId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return MenuGeneratorViewModel(
            generateMenuUseCase  = generateMenuUseCase,
            weeklyPlanRepository = weeklyPlanRepository,
            userRecipeRepository = userRecipeRepository,
            userId               = userId
        ) as T
    }
}
