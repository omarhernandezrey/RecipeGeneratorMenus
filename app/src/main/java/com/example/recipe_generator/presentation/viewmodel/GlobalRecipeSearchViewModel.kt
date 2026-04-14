package com.example.recipe_generator.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipe_generator.data.local.QueryNormalizer
import com.example.recipe_generator.domain.model.GlobalRecipe
import com.example.recipe_generator.domain.usecase.SearchGlobalRecipeUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GlobalRecipeSearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val recipe: GlobalRecipe? = null,
    val error: String? = null,
    val isEmpty: Boolean = true
)

class GlobalRecipeSearchViewModel(
    private val searchGlobalRecipeUseCase: SearchGlobalRecipeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GlobalRecipeSearchUiState())
    val uiState: StateFlow<GlobalRecipeSearchUiState> = _uiState.asStateFlow()

    private var activeSearchJob: Job? = null
    private var lastSearchedNormalized = ""

    fun onQueryChange(value: String) {
        val normalized = QueryNormalizer.normalize(value)
        _uiState.update {
            it.copy(
                query = value,
                error = null,
                recipe = if (normalized == lastSearchedNormalized) it.recipe else null,
                isEmpty = value.isBlank()
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun search() {
        val currentQuery = uiState.value.query
        val normalized = QueryNormalizer.normalize(currentQuery)
        if (normalized.isBlank()) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    recipe = null,
                    error = null,
                    isEmpty = true
                )
            }
            return
        }

        if (normalized == lastSearchedNormalized && uiState.value.recipe != null && !uiState.value.isLoading) {
            return
        }

        if (normalized == lastSearchedNormalized && uiState.value.isLoading) {
            return
        }

        lastSearchedNormalized = normalized
        activeSearchJob?.cancel()
        activeSearchJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, isEmpty = false) }
            try {
                val recipe = searchGlobalRecipeUseCase(currentQuery)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        recipe = recipe,
                        error = null,
                        isEmpty = false
                    )
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (throwable: Throwable) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = throwable.message ?: "Error al buscar recetas",
                        recipe = null,
                        isEmpty = true
                    )
                }
            }
        }
    }
}

