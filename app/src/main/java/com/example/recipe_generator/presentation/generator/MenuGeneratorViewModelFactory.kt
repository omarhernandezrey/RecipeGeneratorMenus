package com.example.recipe_generator.presentation.generator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.recipe_generator.domain.usecase.GenerateMenuUseCase

internal class MenuGeneratorViewModelFactory(
    private val generateMenuUseCase: GenerateMenuUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return MenuGeneratorViewModel(generateMenuUseCase) as T
    }
}

