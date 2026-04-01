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
        MenuGeneratorViewModelFactory(appContainer.generateMenuUseCase)
    }

    @Composable
    override fun ScreenContent() {
        val maxDifficulty by menuGeneratorViewModel.maxDifficulty.collectAsStateWithLifecycle()
        val selectedDiets by menuGeneratorViewModel.selectedDiets.collectAsStateWithLifecycle()
        val selectedTypes by menuGeneratorViewModel.selectedTypes.collectAsStateWithLifecycle()
        val portions by menuGeneratorViewModel.portions.collectAsStateWithLifecycle()
        val generatedMenu by menuGeneratorViewModel.generatedMenu.collectAsStateWithLifecycle()
        val isGenerating by menuGeneratorViewModel.isGenerating.collectAsStateWithLifecycle()

        MenuGeneratorScreen(
            selectedNavItem = TopLevelDestination.Generator.navItemIndex,
            selectedDiets = selectedDiets,
            onToggleDiet = menuGeneratorViewModel::toggleDiet,
            selectedDifficulty = maxDifficulty,
            onDifficultySelected = menuGeneratorViewModel::setDifficulty,
            portions = portions,
            onPortionsChange = menuGeneratorViewModel::setPortions,
            selectedRecipeTypes = selectedTypes,
            onToggleRecipeType = menuGeneratorViewModel::toggleType,
            generatedRecipes = generatedMenu,
            isGenerating = isGenerating,
            onGenerateClick = menuGeneratorViewModel::generateMenu,
            onNavigate = ::navigateToTopLevel
        )
    }
}

private class MenuGeneratorViewModelFactory(
    private val generateMenuUseCase: com.example.recipe_generator.domain.usecase.GenerateMenuUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return MenuGeneratorViewModel(generateMenuUseCase) as T
    }
}
