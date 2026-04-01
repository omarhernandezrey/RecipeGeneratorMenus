package com.example.recipe_generator.presentation.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.recipe_generator.presentation.navigation.ComposeScreenFragment
import com.example.recipe_generator.presentation.navigation.TopLevelDestination

class SettingsFragment : ComposeScreenFragment() {
    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory(appContainer.userPrefsRepository)
    }

    @Composable
    override fun ScreenContent() {
        val preferences by settingsViewModel.preferences.collectAsStateWithLifecycle()

        SettingsScreen(
            selectedDiets = preferences.selectedDiets,
            onToggleDiet = settingsViewModel::toggleDiet,
            defaultPortions = preferences.defaultPortions,
            onPortionsChange = settingsViewModel::saveDefaultPortions,
            selectedTheme = preferences.theme,
            onThemeSelect = settingsViewModel::saveTheme,
            selectedLanguage = preferences.language,
            onLanguageSelect = settingsViewModel::saveLanguage,
            selectedNavItem = TopLevelDestination.Settings.navItemIndex,
            onNavItemSelected = ::navigateToTopLevel
        )
    }
}

private class SettingsViewModelFactory(
    private val userPrefsRepository: com.example.recipe_generator.domain.repository.UserPrefsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return SettingsViewModel(userPrefsRepository) as T
    }
}
