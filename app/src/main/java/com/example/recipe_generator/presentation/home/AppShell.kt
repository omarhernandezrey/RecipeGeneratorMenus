package com.example.recipe_generator.presentation.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.recipe_generator.domain.repository.FavoritesRepository
import com.example.recipe_generator.domain.repository.UserPrefsRepository
import com.example.recipe_generator.domain.usecase.GenerateMenuUseCase
import com.example.recipe_generator.domain.usecase.GetMenuForDayUseCase
import com.example.recipe_generator.presentation.favorites.FavoritesScreen
import com.example.recipe_generator.presentation.favorites.FavoritesViewModel
import com.example.recipe_generator.presentation.generator.MenuGeneratorScreen
import com.example.recipe_generator.presentation.generator.MenuGeneratorViewModel
import com.example.recipe_generator.presentation.settings.SettingsScreen
import com.example.recipe_generator.presentation.settings.SettingsViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.launch

/**
 * AppShell - Estructura completa de la app con NavigationBar
 *
 * Contiene:
 * - 4 tabs principales: Inicio, Favoritos, Generador, Ajustes
 * - Navegación entre tabs via EditorialBottomNavBar (integrado en cada pantalla)
 * - FASE 3: Implementación completa del PLAN MAESTRO
 */
@Composable
fun AppShell(
    getMenuForDayUseCase: GetMenuForDayUseCase,
    favoritesRepository: FavoritesRepository,
    generateMenuUseCase: GenerateMenuUseCase,
    userPrefsRepository: UserPrefsRepository,
    modifier: Modifier = Modifier,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    // ── ViewModels ───────────────────────────────────────────────────
    val homeViewModel: HomeViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(getMenuForDayUseCase) as T
            }
        }
    )

    val favoritesViewModel: FavoritesViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return FavoritesViewModel(favoritesRepository) as T
            }
        }
    )

    val generatorViewModel: MenuGeneratorViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MenuGeneratorViewModel(generateMenuUseCase) as T
            }
        }
    )

    val settingsViewModel: SettingsViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return SettingsViewModel(userPrefsRepository) as T
            }
        }
    )

    // ── Navegación entre tabs ────────────────────────────────────────
    val onNavigate: (Int) -> Unit = { tab -> selectedTab = tab }

    when (selectedTab) {
        // ═══════════════════════════════════════════════════════════════
        // Tab 0: Inicio — RecipeListScreen (Menú Semanal)
        // ═══════════════════════════════════════════════════════════════
        0 -> {
            val selectedDay by homeViewModel.selectedDay.collectAsStateWithLifecycle()
            val recipes by homeViewModel.recipes.collectAsStateWithLifecycle()
            val favoriteIds by favoritesRepository
                .getFavoriteIds()
                .collectAsStateWithLifecycle(initialValue = emptySet())

            RecipeListScreen(
                selectedDay = selectedDay,
                recipes = recipes,
                onDaySelected = homeViewModel::selectDay,
                selectedNavItem = 0,
                onNavItemSelected = onNavigate,
                favoriteRecipeIds = favoriteIds,
                onToggleFavorite = { recipeId ->
                    coroutineScope.launch {
                        favoritesRepository.toggleFavorite(recipeId)
                    }
                },
                onRecipeSelected = { /* TODO: navegar a detalle */ },
                onProfileClick = { /* TODO: navegar a perfil/panels */ }
            )
        }

        // ═══════════════════════════════════════════════════════════════
        // Tab 1: Favoritos — FavoritesScreen
        // ═══════════════════════════════════════════════════════════════
        1 -> {
            val filteredRecipes by favoritesViewModel.filteredRecipes.collectAsStateWithLifecycle()
            val query by favoritesViewModel.searchQuery.collectAsStateWithLifecycle()
            val selectedCategory by favoritesViewModel.selectedCategory.collectAsStateWithLifecycle()

            val categories = listOf("Todos", "Desayuno", "Almuerzo", "Cena", "Snack")

            FavoritesScreen(
                recipes = filteredRecipes,
                categories = categories,
                query = query,
                onQueryChange = favoritesViewModel::onSearchQueryChanged,
                selectedCategory = selectedCategory,
                onCategorySelected = favoritesViewModel::onCategorySelected,
                selectedNavItem = 1,
                onNavItemSelected = onNavigate,
                onRecipeSelected = { /* TODO: navegar a detalle */ },
                onRemoveFavorite = { recipeId ->
                    coroutineScope.launch {
                        favoritesRepository.toggleFavorite(recipeId)
                    }
                }
            )
        }

        // ═══════════════════════════════════════════════════════════════
        // Tab 2: Generador — MenuGeneratorScreen
        // ═══════════════════════════════════════════════════════════════
        2 -> {
            val selectedDiets by generatorViewModel.selectedDiets.collectAsStateWithLifecycle()
            val selectedDifficulty by generatorViewModel.maxDifficulty.collectAsStateWithLifecycle()
            val portions by generatorViewModel.portions.collectAsStateWithLifecycle()
            val selectedTypes by generatorViewModel.selectedTypes.collectAsStateWithLifecycle()
            val generatedRecipes by generatorViewModel.generatedMenu.collectAsStateWithLifecycle()
            val isGenerating by generatorViewModel.isGenerating.collectAsStateWithLifecycle()

            MenuGeneratorScreen(
                selectedNavItem = 2,
                selectedDiets = selectedDiets,
                onToggleDiet = generatorViewModel::toggleDiet,
                selectedDifficulty = selectedDifficulty,
                onDifficultySelected = generatorViewModel::setDifficulty,
                portions = portions,
                onPortionsChange = generatorViewModel::setPortions,
                selectedRecipeTypes = selectedTypes,
                onToggleRecipeType = generatorViewModel::toggleType,
                generatedRecipes = generatedRecipes,
                isGenerating = isGenerating,
                onGenerateClick = generatorViewModel::generateMenu,
                onNavigate = onNavigate
            )
        }

        // ═══════════════════════════════════════════════════════════════
        // Tab 3: Ajustes — SettingsScreen
        // ═══════════════════════════════════════════════════════════════
        3 -> {
            val prefs by settingsViewModel.preferences.collectAsStateWithLifecycle()

            SettingsScreen(
                selectedDiets = prefs.selectedDiets,
                onToggleDiet = settingsViewModel::toggleDiet,
                defaultPortions = prefs.defaultPortions,
                onPortionsChange = settingsViewModel::saveDefaultPortions,
                selectedTheme = prefs.theme,
                onThemeSelect = settingsViewModel::saveTheme,
                selectedLanguage = prefs.language,
                onLanguageSelect = settingsViewModel::saveLanguage,
                selectedNavItem = 3,
                onNavItemSelected = onNavigate
            )
        }
    }
}
