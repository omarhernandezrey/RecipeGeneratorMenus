package com.example.recipe_generator.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.recipe_generator.domain.repository.FavoritesRepository

internal class FavoritesViewModelFactory(
    private val favoritesRepository: FavoritesRepository,
    private val userId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return FavoritesViewModel(favoritesRepository, userId) as T
    }
}

