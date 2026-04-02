package com.example.recipe_generator.presentation.detail

import android.content.Context
import android.content.Intent
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
import com.example.recipe_generator.domain.usecase.GetRecipeDetailUseCase
import com.example.recipe_generator.domain.usecase.ToggleFavoriteUseCase
import com.example.recipe_generator.presentation.theme.RecipeGeneratorTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
                container.favoritesRepository
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

                if (recipe != null) {
                    // F3-07: botón favorito con animateColorAsState en RecipeDetailScreen
                    RecipeDetailScreen(
                        recipe = recipe,
                        selectedNavItem = 0,
                        onNavItemSelected = {},
                        isFavorite = isFavorite,
                        onToggleFavorite = { viewModel.toggleFavorite() },
                        onBackClick = { onBackPressedDispatcher.onBackPressed() }
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
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    private val _recipe = MutableStateFlow<Recipe?>(null)
    val recipe: StateFlow<Recipe?> = _recipe.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private var currentRecipeId: String = ""

    fun loadRecipe(recipeId: String) {
        currentRecipeId = recipeId
        viewModelScope.launch {
            getRecipeDetailUseCase(recipeId).collect { r ->
                _recipe.value = r
            }
        }
        viewModelScope.launch {
            favoritesRepository.getFavoriteIds().collect { ids ->
                _isFavorite.value = recipeId in ids
            }
        }
    }

    fun toggleFavorite() {
        if (currentRecipeId.isEmpty()) return
        viewModelScope.launch {
            toggleFavoriteUseCase(currentRecipeId)
        }
    }
}

private class DetailActivityViewModelFactory(
    private val getRecipeDetailUseCase: GetRecipeDetailUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val favoritesRepository: FavoritesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return DetailActivityViewModel(
            getRecipeDetailUseCase,
            toggleFavoriteUseCase,
            favoritesRepository
        ) as T
    }
}
