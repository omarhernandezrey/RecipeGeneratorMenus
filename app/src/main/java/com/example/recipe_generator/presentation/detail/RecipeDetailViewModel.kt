package com.example.recipe_generator.presentation.detail

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipe_generator.domain.model.Recipe
import com.example.recipe_generator.domain.usecase.EnsureRecipeVideoUseCase
import com.example.recipe_generator.domain.usecase.GetRecipeDetailUseCase
import com.example.recipe_generator.domain.usecase.ToggleFavoriteUseCase
import com.example.recipe_generator.presentation.detail.components.RecipeVideoUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Collections

/**
 * ViewModel del detalle de receta.
 *
 * Recibe recipeId desde RecipeDetailActivity via Bundle (LF5).
 * Expone la receta completa y gestiona el toggle de favorito.
 *
 * Se completa en F3-32.
 * Capa: Presentation
 */
class RecipeDetailViewModel(
    private val getRecipeDetailUseCase: GetRecipeDetailUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val ensureRecipeVideoUseCase: EnsureRecipeVideoUseCase,
    private val userId: String
) : ViewModel() {
    private val cachedVideoRecipeIds = Collections.synchronizedSet(mutableSetOf<String>())
    private val pendingVideoRecipeIds = Collections.synchronizedSet(mutableSetOf<String>())

    private val _recipe = MutableStateFlow<Recipe?>(null)
    val recipe: StateFlow<Recipe?> = _recipe.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _videoUiState = MutableStateFlow<RecipeVideoUiState>(RecipeVideoUiState.Loading)
    val videoUiState: StateFlow<RecipeVideoUiState> = _videoUiState.asStateFlow()

    fun loadRecipe(recipeId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            getRecipeDetailUseCase(recipeId).collect { recipe ->
                _recipe.value = recipe
                if (recipe == null) {
                    _videoUiState.value = RecipeVideoUiState.Empty
                } else {
                    recipe.videoYoutube?.takeIf { it.isNotBlank() }?.let { currentUrl ->
                        _videoUiState.value = RecipeVideoUiState.Ready(
                            videoUrl = currentUrl,
                            fromFallback = isFallbackUrl(currentUrl)
                        )
                    } ?: run {
                        _videoUiState.value = RecipeVideoUiState.Loading
                    }
                    cacheVideoIfNeeded(
                        recipeId = recipe.id,
                        recipeTitle = recipe.title,
                        currentVideoUrl = recipe.videoYoutube
                    )
                }
                _isLoading.value = false
            }
        }
    }

    fun cacheVideoIfNeeded(recipeId: String, recipeTitle: String, currentVideoUrl: String?) {
        if (recipeId.isBlank()) return
        synchronized(this) {
            if (recipeId in cachedVideoRecipeIds || recipeId in pendingVideoRecipeIds) return
            pendingVideoRecipeIds.add(recipeId)
        }

        viewModelScope.launch(Dispatchers.IO) {
            val result = runCatching {
                ensureRecipeVideoUseCase(
                    recipeId = recipeId,
                    recipeTitle = recipeTitle,
                    currentVideoUrl = currentVideoUrl,
                    ownerUserId = userId
                )
            }.getOrElse {
                _videoUiState.value = RecipeVideoUiState.Error(
                    message = "No se pudo resolver el video",
                    fallbackUrl = buildFallbackUrl(recipeTitle)
                )
                markCacheFinished(recipeId, wasSuccessful = false)
                android.util.Log.e("VideoCache", "Error cacheando video: ${it.message}")
                return@launch
            }

            _videoUiState.value = RecipeVideoUiState.Ready(
                videoUrl = result.resolvedVideoUrl,
                fromFallback = isFallbackUrl(result.resolvedVideoUrl)
            )
            markCacheFinished(recipeId, wasSuccessful = result.persisted)
        }
    }

    private fun markCacheFinished(recipeId: String, wasSuccessful: Boolean) {
        synchronized(this) {
            pendingVideoRecipeIds.remove(recipeId)
            if (wasSuccessful) cachedVideoRecipeIds.add(recipeId)
        }
    }

    fun toggleFavorite() {
        val currentRecipe = _recipe.value ?: return
        viewModelScope.launch {
            toggleFavoriteUseCase(userId, currentRecipe.id)
        }
    }

    private fun buildFallbackUrl(recipeTitle: String): String {
        return "https://www.youtube.com/results?search_query=${
            Uri.encode("como preparar $recipeTitle receta tutorial")
        }"
    }

    private fun isFallbackUrl(url: String): Boolean = url.contains("/results?search_query=")
}
