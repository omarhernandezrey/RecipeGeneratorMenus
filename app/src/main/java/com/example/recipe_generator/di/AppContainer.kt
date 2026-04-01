package com.example.recipe_generator.di

import android.content.Context
import com.example.recipe_generator.data.repository.FavoritesRepositoryImpl
import com.example.recipe_generator.data.repository.RecipeRepositoryImpl
import com.example.recipe_generator.data.repository.UserPrefsRepositoryImpl
import com.example.recipe_generator.domain.repository.FavoritesRepository
import com.example.recipe_generator.domain.repository.RecipeRepository
import com.example.recipe_generator.domain.repository.UserPrefsRepository
import com.example.recipe_generator.domain.usecase.GenerateMenuUseCase
import com.example.recipe_generator.domain.usecase.GetMenuForDayUseCase
import com.example.recipe_generator.domain.usecase.GetRecipeDetailUseCase
import com.example.recipe_generator.domain.usecase.ToggleFavoriteUseCase

/**
 * Contenedor de dependencias manual — sin Hilt ni Dagger.
 *
 * Actúa como Service Locator para toda la aplicación.
 * Instanciado una sola vez en RecipeGeneratorApp.onCreate().
 *
 * FLUJO DE DEPENDENCIAS (Dependency Rule — Clean Architecture):
 *
 *   Presentation → Domain ← Data
 *
 * La capa de Dominio NO conoce a Presentation ni a Data.
 * AppContainer construye las implementaciones concretas de Data
 * e inyecta las interfaces de Dominio hacia arriba (Presentation).
 *
 * Capa: DI (infraestructura transversal)
 *
 * NOTA: Las implementaciones concretas (RecipeRepositoryImpl, etc.)
 * se instanciarán aquí en F2-21 y F3-29/F3-30 cuando se cree Room
 * y DataStore. Por ahora las propiedades están declaradas como lateinit
 * para dejar explícita la arquitectura.
 */
class AppContainer(private val context: Context) {
    private val appContext = context.applicationContext

    // ── Repositorios (interfaces de Dominio, impls de Datos) ──────────
    // En Fase 0 se conectan a fuentes locales temporales.
    // En F2/F3 migran a Room/DataStore completos sin cambiar Presentation.

    val recipeRepository: RecipeRepository by lazy {
        RecipeRepositoryImpl()
    }

    val favoritesRepository: FavoritesRepository by lazy {
        FavoritesRepositoryImpl(
            context = appContext,
            recipeRepository = recipeRepository
        )
    }

    val userPrefsRepository: UserPrefsRepository by lazy {
        UserPrefsRepositoryImpl(appContext)
    }

    // ── Casos de Uso ─────────────────────────────────────────────────

    val getMenuForDayUseCase: GetMenuForDayUseCase
        get() = GetMenuForDayUseCase(recipeRepository)

    val getRecipeDetailUseCase: GetRecipeDetailUseCase
        get() = GetRecipeDetailUseCase(recipeRepository)

    val toggleFavoriteUseCase: ToggleFavoriteUseCase
        get() = ToggleFavoriteUseCase(favoritesRepository)

    val generateMenuUseCase: GenerateMenuUseCase
        get() = GenerateMenuUseCase(recipeRepository)
}
