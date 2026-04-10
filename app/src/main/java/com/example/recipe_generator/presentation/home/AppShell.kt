package com.example.recipe_generator.presentation.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.recipe_generator.domain.repository.FavoritesRepository
import com.example.recipe_generator.domain.repository.UserPrefsRepository
import com.example.recipe_generator.domain.repository.WeeklyPlanRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import com.example.recipe_generator.domain.usecase.GenerateMenuUseCase
import com.example.recipe_generator.domain.usecase.GetMenuForDayUseCase
import com.example.recipe_generator.presentation.favorites.FavoritesScreen
import com.example.recipe_generator.presentation.favorites.FavoritesViewModel
import com.example.recipe_generator.presentation.generator.MenuGeneratorScreen
import com.example.recipe_generator.presentation.generator.MenuGeneratorViewModel
import com.example.recipe_generator.presentation.profile.ProfileHubScreen
import com.example.recipe_generator.presentation.settings.SettingsScreen
import com.example.recipe_generator.presentation.settings.SettingsViewModel
import com.example.recipe_generator.domain.model.Recipe
import com.example.recipe_generator.domain.usecase.GetRecipeDetailUseCase
import com.example.recipe_generator.presentation.detail.RecipeDetailBottomSheet
import com.example.recipe_generator.presentation.myrecipes.MyRecipesScreen
import com.example.recipe_generator.presentation.weeklyplan.MyWeeklyPlanScreen
import com.example.recipe_generator.presentation.components.EditorialBottomNavBar
import com.example.recipe_generator.presentation.components.editorialBottomBarContentPadding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.launch

/**
 * AppShell - Estructura completa de la app con NavigationBar
 *
 * Contiene:
 * - 6 tabs principales: Inicio, Favoritos, Mi Plan, Mis Recetas, Generador, Ajustes
 * - Navegación entre tabs via EditorialBottomNavBar (integrado en cada pantalla)
 * - FASE 3: Implementación completa del PLAN MAESTRO
 */
@Composable
fun AppShell(
    getMenuForDayUseCase: GetMenuForDayUseCase,
    getRecipeDetailUseCase: GetRecipeDetailUseCase,
    favoritesRepository: FavoritesRepository,
    generateMenuUseCase: GenerateMenuUseCase,
    userPrefsRepository: UserPrefsRepository,
    weeklyPlanRepository: WeeklyPlanRepository,
    isSyncing: StateFlow<Boolean>,
    userId: String,
    modifier: Modifier = Modifier,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var isProfileOpen by remember { mutableStateOf(false) }
    var selectedRecipe by remember { mutableStateOf<Recipe?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val isSyncingValue by isSyncing.collectAsStateWithLifecycle()

    // Favoritos hoisted para el modal y para Tab 0
    val favoriteIds by favoritesRepository
        .getFavoriteIds(userId)
        .collectAsStateWithLifecycle(initialValue = emptySet())

    // Receta completa (con ingredientes y pasos) para el modal
    val fullRecipe: Recipe? by remember(selectedRecipe?.id) {
        selectedRecipe?.let { r -> getRecipeDetailUseCase(r.id) } ?: flowOf(null)
    }.collectAsStateWithLifecycle(initialValue = selectedRecipe)

    // ── ViewModels ───────────────────────────────────────────────────
    val homeViewModel: HomeViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(getMenuForDayUseCase, weeklyPlanRepository, userId) as T
            }
        }
    )

    val favoritesViewModel: FavoritesViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return FavoritesViewModel(favoritesRepository, userId) as T
            }
        }
    )

    val appContainer = (LocalContext.current.applicationContext
        as com.example.recipe_generator.RecipeGeneratorApp).container

    val generatorViewModel: MenuGeneratorViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MenuGeneratorViewModel(
                    generateMenuUseCase  = generateMenuUseCase,
                    weeklyPlanRepository = weeklyPlanRepository,
                    userRecipeRepository = appContainer.userRecipeRepository,
                    userId               = userId
                ) as T
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

    if (isProfileOpen) {
        ProfileHubScreen(
            modifier = modifier,
            onClose = { isProfileOpen = false },
            onLogout = onLogout
        )
        return
    }

    when (selectedTab) {
        // ═══════════════════════════════════════════════════════════════
        // Tab 0: Inicio — RecipeListScreen (Menú Semanal)
        // ═══════════════════════════════════════════════════════════════
        0 -> {
            val selectedDay by homeViewModel.selectedDay.collectAsStateWithLifecycle()
            val recipes by homeViewModel.recipes.collectAsStateWithLifecycle()

            RecipeListScreen(
                selectedDay = selectedDay,
                recipes = recipes,
                onDaySelected = homeViewModel::selectDay,
                selectedNavItem = 0,
                onNavItemSelected = onNavigate,
                favoriteRecipeIds = favoriteIds,
                onToggleFavorite = { recipeId ->
                    coroutineScope.launch {
                        favoritesRepository.toggleFavorite(userId, recipeId)
                    }
                },
                onRecipeSelected = { selectedRecipe = it },
                onProfileClick = { isProfileOpen = true },
                isSyncing = isSyncingValue
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
                onRecipeSelected = { selectedRecipe = it },
                onRemoveFavorite = { recipeId ->
                    coroutineScope.launch {
                        favoritesRepository.toggleFavorite(userId, recipeId)
                    }
                }
            )
        }

        // ═══════════════════════════════════════════════════════════════
        // Tab 2: Mi Plan — MyWeeklyPlanScreen (E-01)
        // ═══════════════════════════════════════════════════════════════
        2 -> {
            Box(modifier = Modifier.fillMaxSize()) {
                MyWeeklyPlanScreen(
                    onBack = { selectedTab = 0 },
                    onRecipeClick = { recipeId ->
                        selectedRecipe = Recipe(
                            id = recipeId, title = "", imageRes = "",
                            timeInMinutes = 0, calories = 0, difficulty = "",
                            category = "", categorySubtitle = "", description = "",
                            dayOfWeek = ""
                        )
                    }
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.BottomCenter),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    EditorialBottomNavBar(selectedItem = 2, onItemSelected = onNavigate)
                }
            }
        }

        // ═══════════════════════════════════════════════════════════════
        // Tab 3: Mis Recetas — MyRecipesScreen (E-01)
        // ═══════════════════════════════════════════════════════════════
        3 -> {
            Box(modifier = Modifier.fillMaxSize()) {
                MyRecipesScreen(
                    modifier = Modifier.padding(bottom = editorialBottomBarContentPadding()),
                    onBack = { selectedTab = 0 }
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.BottomCenter),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    EditorialBottomNavBar(selectedItem = 3, onItemSelected = onNavigate)
                }
            }
        }

        // ═══════════════════════════════════════════════════════════════
        // Tab 4: Generador — MenuGeneratorScreen
        // ═══════════════════════════════════════════════════════════════
        4 -> {
            val selectedDiets      by generatorViewModel.selectedDiets.collectAsStateWithLifecycle()
            val selectedDifficulty by generatorViewModel.maxDifficulty.collectAsStateWithLifecycle()
            val portions           by generatorViewModel.portions.collectAsStateWithLifecycle()
            val selectedTypes      by generatorViewModel.selectedTypes.collectAsStateWithLifecycle()
            val generatorUiState   by generatorViewModel.uiState.collectAsStateWithLifecycle()

            MenuGeneratorScreen(
                selectedNavItem    = 4,
                selectedDiets      = selectedDiets,
                onToggleDiet       = generatorViewModel::toggleDiet,
                selectedDifficulty = selectedDifficulty,
                onDifficultySelected = generatorViewModel::setDifficulty,
                portions           = portions,
                onPortionsChange   = generatorViewModel::setPortions,
                selectedRecipeTypes = selectedTypes,
                onToggleRecipeType = generatorViewModel::toggleType,
                uiState            = generatorUiState,
                onGenerateClick    = generatorViewModel::generateAndSavePlan,
                onGoToPlan         = { selectedTab = 2 },
                onNavigate         = onNavigate
            )
        }

        // ═══════════════════════════════════════════════════════════════
        // Tab 5: Ajustes — SettingsScreen
        // ═══════════════════════════════════════════════════════════════
        5 -> {
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
                selectedNavItem = 5,
                onNavItemSelected = onNavigate,
                onLogout = onLogout
            )
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // Modal de detalle — aparece al pulsar cualquier receta en la app
    // ═══════════════════════════════════════════════════════════════════
    if (selectedRecipe != null) {
        RecipeDetailBottomSheet(
            recipe = fullRecipe ?: selectedRecipe!!,
            isFavorite = (fullRecipe?.id ?: selectedRecipe!!.id) in favoriteIds,
            onToggleFavorite = { recipeId ->
                coroutineScope.launch {
                    favoritesRepository.toggleFavorite(userId, recipeId)
                }
            },
            onDismiss = { selectedRecipe = null }
        )
    }
}
