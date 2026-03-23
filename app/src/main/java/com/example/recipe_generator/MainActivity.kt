package com.example.recipe_generator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.recipe_generator.data.FavoritesRepository
import com.example.recipe_generator.data.Recipe
import com.example.recipe_generator.data.getAllRecipes
import com.example.recipe_generator.data.getFeaturedRecipeDetail
import com.example.recipe_generator.ui.components.EditorialBottomNavBar
import com.example.recipe_generator.ui.screens.MenuGeneratorScreen
import com.example.recipe_generator.ui.screens.RecipeDetailScreen
import com.example.recipe_generator.ui.screens.FavoritesScreen
import com.example.recipe_generator.ui.screens.RecipeListScreen
import com.example.recipe_generator.ui.theme.Background
import com.example.recipe_generator.ui.theme.OnSurface
import com.example.recipe_generator.ui.theme.RecipeGeneratorTheme
import com.example.recipe_generator.ui.theme.spacing_6
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecipeGeneratorTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RecipeGeneratorApp()
                }
            }
        }
    }
}

private enum class TopLevelDestination {
    Home,
    Favorites,
    Generator,
    Settings
}

@Composable
private fun RecipeGeneratorApp() {
    val allRecipes = remember { getAllRecipes() }
    val context = LocalContext.current.applicationContext
    val favoritesRepository = remember { FavoritesRepository(context) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val storedFavoriteRecipeIds by favoritesRepository.favoriteRecipeIds.collectAsState(initial = emptySet())
    var favoriteRecipeIds by remember { mutableStateOf(emptySet<String>()) }

    LaunchedEffect(storedFavoriteRecipeIds) {
        favoriteRecipeIds = storedFavoriteRecipeIds
    }

    var currentDestination by remember { mutableStateOf(TopLevelDestination.Home) }
    var selectedRecipeId by remember { mutableStateOf<String?>(null) }

    val selectedRecipe = remember(selectedRecipeId, allRecipes) {
        allRecipes.find { it.id == selectedRecipeId }
    }

    val selectedRecipeWithFavoriteState = selectedRecipe?.copy(
        isFavorite = selectedRecipe?.id in favoriteRecipeIds
    )

    val favoriteRecipes = allRecipes
        .filter { it.id in favoriteRecipeIds }
        .map { it.copy(isFavorite = true) }

    val toggleFavorite: (String) -> Unit = { recipeId ->
        coroutineScope.launch {
            val isCurrentlyFavorite = recipeId in favoriteRecipeIds
            // Optimistic update
            favoriteRecipeIds = if (isCurrentlyFavorite) {
                favoriteRecipeIds - recipeId
            } else {
                favoriteRecipeIds + recipeId
            }

            favoritesRepository.toggleFavorite(recipeId)

            snackbarHostState.showSnackbar(
                if (!isCurrentlyFavorite) {
                    "Guardado en favoritos"
                } else {
                    "Eliminado de favoritos"
                }
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when {
                selectedRecipeWithFavoriteState != null -> {
                    RecipeDetailScreen(
                        recipe = selectedRecipeWithFavoriteState,
                        selectedNavItem = destinationToIndex(currentDestination),
                        onNavItemSelected = { index ->
                            currentDestination = indexToDestination(index)
                            selectedRecipeId = null
                        },
                        isFavorite = selectedRecipeWithFavoriteState.id in favoriteRecipeIds,
                        onToggleFavorite = toggleFavorite,
                        onBackClick = { selectedRecipeId = null }
                    )
                }

                currentDestination == TopLevelDestination.Home -> {
                    RecipeListScreen(
                        selectedNavItem = destinationToIndex(currentDestination),
                        onNavItemSelected = { index ->
                            currentDestination = indexToDestination(index)
                        },
                        favoriteRecipeIds = favoriteRecipeIds,
                        onToggleFavorite = toggleFavorite,
                        onRecipeSelected = { recipe ->
                            selectedRecipeId = recipe.id
                        }
                    )
                }

                currentDestination == TopLevelDestination.Favorites -> {
                    FavoritesScreen(
                        recipes = favoriteRecipes,
                        selectedNavItem = destinationToIndex(currentDestination),
                        onNavItemSelected = { index ->
                            currentDestination = indexToDestination(index)
                        },
                        onRecipeSelected = { recipe ->
                            selectedRecipeId = recipe.id
                        },
                        onRemoveFavorite = { recipeId ->
                            coroutineScope.launch {
                                // Optimistic update
                                favoriteRecipeIds = favoriteRecipeIds - recipeId
                                favoritesRepository.removeFavorite(recipeId)
                                snackbarHostState.showSnackbar("Eliminado de favoritos")
                            }
                        }
                    )
                }

                currentDestination == TopLevelDestination.Generator -> {
                    MenuGeneratorScreen(
                        onNavigate = { index ->
                            currentDestination = indexToDestination(index)
                        }
                    )
                }

                else -> {
                    PlaceholderScreen(
                        title = when (currentDestination) {
                            TopLevelDestination.Favorites -> "Favoritos"
                            TopLevelDestination.Generator -> "Generador"
                            TopLevelDestination.Settings -> "Ajustes"
                            TopLevelDestination.Home -> "Inicio"
                        },
                        selectedNavItem = destinationToIndex(currentDestination),
                        onNavItemSelected = { index ->
                            currentDestination = indexToDestination(index)
                        }
                    )
                }
            }
        }
    }
}

private fun destinationToIndex(destination: TopLevelDestination): Int {
    return when (destination) {
        TopLevelDestination.Home -> 0
        TopLevelDestination.Favorites -> 1
        TopLevelDestination.Generator -> 2
        TopLevelDestination.Settings -> 3
    }
}

private fun indexToDestination(index: Int): TopLevelDestination {
    return when (index) {
        1 -> TopLevelDestination.Favorites
        2 -> TopLevelDestination.Generator
        3 -> TopLevelDestination.Settings
        else -> TopLevelDestination.Home
    }
}

@Composable
private fun PlaceholderScreen(
    title: String,
    selectedNavItem: Int,
    onNavItemSelected: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(spacing_6),
            verticalArrangement = Arrangement.spacedBy(spacing_6),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.displaySmall,
                color = OnSurface
            )
            Text(
                text = "Esta vista ya responde a la barra inferior.",
                style = MaterialTheme.typography.bodyLarge,
                color = OnSurface
            )
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            EditorialBottomNavBar(
                selectedItem = selectedNavItem,
                onItemSelected = onNavItemSelected
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecipeListScreenPreview() {
    RecipeGeneratorTheme {
        RecipeGeneratorApp()
    }
}

@Preview(showBackground = true)
@Composable
fun MenuGeneratorScreenPreview() {
    RecipeGeneratorTheme {
        MenuGeneratorScreen()
    }
}
