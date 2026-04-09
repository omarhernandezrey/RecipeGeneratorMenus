package com.example.recipe_generator.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.recipe_generator.domain.repository.UserPrefsRepository

internal class SettingsViewModelFactory(
    private val userPrefsRepository: UserPrefsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return SettingsViewModel(userPrefsRepository) as T
    }
}

