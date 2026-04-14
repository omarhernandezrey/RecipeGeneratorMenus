package com.example.recipe_generator.presentation.myrecipes

import androidx.compose.runtime.Composable
import com.example.recipe_generator.presentation.ui.GlobalRecipeSearchScreen

@Composable
fun RecipeSearchScreen(
    onBack: () -> Unit
) {
    GlobalRecipeSearchScreen(
        onBack = onBack
    )
}

