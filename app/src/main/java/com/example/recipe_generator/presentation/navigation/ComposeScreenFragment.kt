package com.example.recipe_generator.presentation.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.recipe_generator.RecipeGeneratorApp
import com.example.recipe_generator.domain.model.UserPreferences
import com.example.recipe_generator.presentation.theme.RecipeGeneratorTheme

abstract class ComposeScreenFragment : Fragment() {

    protected val appContainer
        get() = (requireActivity().application as RecipeGeneratorApp).container

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Capturar fuera del composable para no llamar requireActivity() en cada recomposición
        val userPrefsRepository = appContainer.userPrefsRepository

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                // F3-18: lee preferencia de tema desde DataStore y aplica darkTheme
                val preferences by userPrefsRepository
                    .getUserPreferences()
                    .collectAsStateWithLifecycle(initialValue = UserPreferences())

                RecipeGeneratorTheme(darkTheme = preferences.theme == "Oscuro") {
                    ScreenContent()
                }
            }
        }
    }

    protected fun navigateToTopLevel(index: Int) {
        val destination = TopLevelDestination.fromNavItemIndex(index)
        val navController = findNavController()

        if (navController.currentDestination?.id == destination.destinationId) {
            return
        }

        val options = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setRestoreState(true)
            .setPopUpTo(navController.graph.startDestinationId, false, true)
            .build()

        navController.navigate(destination.destinationId, null, options)
    }

    @Composable
    protected abstract fun ScreenContent()
}
