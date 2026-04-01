package com.example.recipe_generator.presentation.navigation

import com.example.recipe_generator.R

enum class TopLevelDestination(
    val destinationId: Int,
    val navItemIndex: Int
) {
    Home(R.id.homeFragment, 0),
    Favorites(R.id.favoritesFragment, 1),
    Generator(R.id.generatorFragment, 2),
    Settings(R.id.settingsFragment, 3);

    companion object {
        fun fromNavItemIndex(index: Int): TopLevelDestination {
            return entries.firstOrNull { it.navItemIndex == index } ?: Home
        }
    }
}
