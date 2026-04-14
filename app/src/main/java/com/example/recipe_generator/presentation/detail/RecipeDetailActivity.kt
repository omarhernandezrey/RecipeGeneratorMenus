package com.example.recipe_generator.presentation.detail

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.recipe_generator.RecipeGeneratorApp
import com.example.recipe_generator.domain.model.Recipe
import com.example.recipe_generator.domain.repository.FavoritesRepository
import com.example.recipe_generator.domain.usecase.EnsureRecipeVideoUseCase
import com.example.recipe_generator.domain.usecase.GetRecipeDetailUseCase
import com.example.recipe_generator.domain.usecase.ToggleFavoriteUseCase
import com.example.recipe_generator.presentation.detail.components.RecipeVideoUiState
import com.example.recipe_generator.presentation.theme.RecipeGeneratorTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Collections

/**
 * Segunda Activity — RecipeDetailActivity.
 *
 * Recibe el ID de la receta via Intent.getStringExtra(EXTRA_RECIPE_ID).
 * Muestra el detalle completo con ingredientes, pasos y botón de favorito.
 *
 * Cumple LF5: segunda actividad, pila de actividades, Intent + putExtra + back stack.
 *
 * Capa: Presentation
 */
class RecipeDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_RECIPE_ID = "extra_recipe_id"

        /** Crea el Intent con el ID de la receta (LF5: putExtra). */
        fun newIntent(context: Context, recipeId: String): Intent =
            Intent(context, RecipeDetailActivity::class.java)
                .putExtra(EXTRA_RECIPE_ID, recipeId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // LF5: leer datos del Intent (getExtras / getStringExtra)
        val recipeId = intent.getStringExtra(EXTRA_RECIPE_ID) ?: run {
            finish()
            return
        }

        val container = (application as RecipeGeneratorApp).container
        val viewModel: DetailActivityViewModel = ViewModelProvider(
            this,
            DetailActivityViewModelFactory(
                container.getRecipeDetailUseCase,
                container.toggleFavoriteUseCase,
                container.ensureRecipeVideoUseCase,
                container.favoritesRepository,
                container.requireAuthenticatedUserId()
            )
        )[DetailActivityViewModel::class.java]

        viewModel.loadRecipe(recipeId)

        setContent {
            // F3-18: aplica darkTheme desde DataStore al igual que los Fragments
            val preferences by container.userPrefsRepository
                .getUserPreferences()
                .collectAsStateWithLifecycle(
                    initialValue = com.example.recipe_generator.domain.model.UserPreferences()
                )

            RecipeGeneratorTheme(darkTheme = preferences.theme == "Oscuro") {
                val recipe = viewModel.recipe.collectAsStateWithLifecycle().value
                val isFavorite = viewModel.isFavorite.collectAsStateWithLifecycle().value
                val videoUiState = viewModel.videoUiState.collectAsStateWithLifecycle().value

                if (recipe != null) {
                    // F3-07: botón favorito con animateColorAsState en RecipeDetailScreen
                    RecipeDetailScreen(
                        recipe = recipe,
                        selectedNavItem = 0,
                        onNavItemSelected = {},
                        isFavorite = isFavorite,
                        onToggleFavorite = { viewModel.toggleFavorite() },
                        onBackClick = { onBackPressedDispatcher.onBackPressed() },
                        videoUiState = videoUiState
                    )
                }
            }
        }
    }
}

/**
 * ViewModel de RecipeDetailActivity.
 * Carga la receta y expone el estado de favorito en tiempo real.
 */
class DetailActivityViewModel(
    private val getRecipeDetailUseCase: GetRecipeDetailUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val ensureRecipeVideoUseCase: EnsureRecipeVideoUseCase,
    private val favoritesRepository: FavoritesRepository,
    private val userId: String
) : ViewModel() {
    private val cachedVideoRecipeIds = Collections.synchronizedSet(mutableSetOf<String>())
    private val pendingVideoRecipeIds = Collections.synchronizedSet(mutableSetOf<String>())

    private val _recipe = MutableStateFlow<Recipe?>(null)
    val recipe: StateFlow<Recipe?> = _recipe.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()
    private val _videoUiState = MutableStateFlow<RecipeVideoUiState>(RecipeVideoUiState.Loading)
    val videoUiState: StateFlow<RecipeVideoUiState> = _videoUiState.asStateFlow()

    private var currentRecipeId: String = ""

    fun loadRecipe(recipeId: String) {
        currentRecipeId = recipeId
        viewModelScope.launch {
            getRecipeDetailUseCase(recipeId).collect { r ->
                _recipe.value = r
                if (r == null) {
                    _videoUiState.value = RecipeVideoUiState.Empty
                } else {
                    r.videoYoutube?.takeIf { it.isNotBlank() }?.let { currentUrl ->
                        _videoUiState.value = RecipeVideoUiState.Ready(
                            videoUrl = currentUrl,
                            fromFallback = isFallbackUrl(currentUrl)
                        )
                    } ?: run {
                        _videoUiState.value = RecipeVideoUiState.Loading
                    }
                    cacheVideoIfNeeded(
                        recipeId = r.id,
                        recipeTitle = r.title,
                        currentVideoUrl = r.videoYoutube
                    )
                }
            }
        }
        viewModelScope.launch {
            favoritesRepository.getFavoriteIds(userId).collect { ids ->
                _isFavorite.value = recipeId in ids
            }
        }
    }

    fun toggleFavorite() {
        if (currentRecipeId.isEmpty()) return
        viewModelScope.launch {
            toggleFavoriteUseCase(userId, currentRecipeId)
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

    private fun buildFallbackUrl(recipeTitle: String): String {
        return "https://www.youtube.com/results?search_query=${
            Uri.encode("como preparar $recipeTitle receta tutorial")
        }"
    }

    private fun isFallbackUrl(url: String): Boolean = url.contains("/results?search_query=")
}

private class DetailActivityViewModelFactory(
    private val getRecipeDetailUseCase: GetRecipeDetailUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val ensureRecipeVideoUseCase: EnsureRecipeVideoUseCase,
    private val favoritesRepository: FavoritesRepository,
    private val userId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return DetailActivityViewModel(
            getRecipeDetailUseCase,
            toggleFavoriteUseCase,
            ensureRecipeVideoUseCase,
            favoritesRepository,
            userId
        ) as T
    }
}
