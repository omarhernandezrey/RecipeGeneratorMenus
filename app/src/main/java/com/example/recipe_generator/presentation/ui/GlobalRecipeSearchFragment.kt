package com.example.recipe_generator.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.recipe_generator.R
import com.example.recipe_generator.domain.model.UserPreferences
import com.example.recipe_generator.presentation.theme.RecipeGeneratorTheme

class GlobalRecipeSearchFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_global_recipe_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val composeView = view.findViewById<ComposeView>(R.id.globalRecipeSearchComposeView)
        val appContainer = (requireActivity().application as com.example.recipe_generator.RecipeGeneratorApp).container
        val userPrefsRepository = appContainer.userPrefsRepository
        composeView.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        composeView.setContent {
            val preferences by userPrefsRepository.getUserPreferences()
                .collectAsState(initial = UserPreferences())
            RecipeGeneratorTheme(darkTheme = preferences.theme == "Oscuro") {
                GlobalRecipeSearchScreen(
                    onBack = { findNavController().navigateUp() }
                )
            }
        }
    }
}

