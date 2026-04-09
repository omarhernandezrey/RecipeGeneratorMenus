package com.example.recipe_generator.presentation.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.recipe_generator.presentation.navigation.ComposeScreenFragment
import com.example.recipe_generator.presentation.navigation.TopLevelDestination
import kotlinx.coroutines.launch

class SettingsFragment : ComposeScreenFragment() {
    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory(appContainer.userPrefsRepository)
    }

    @Composable
    override fun ScreenContent() {
        val preferences by settingsViewModel.preferences.collectAsStateWithLifecycle()

        val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()
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
            onNavItemSelected = ::navigateToTopLevel,
            onLogout = {
                val uid = appContainer.authRepository.getCurrentUserId()
                if (uid != null) {
                    coroutineScope.launch {
                        runCatching { appContainer.clearLocalUserData(uid) }
                    }
                }
                coroutineScope.launch {
                    appContainer.authRepository.logout()
                }
            }
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
